# 🎫 Ticketing System (콘서트 티켓 예매 API)

> **낙관적 락 기반 동시성 제어와 JWT 인증을 적용한 콘서트 티켓 예매 REST API**

## 💡 프로젝트 개요
여러 사용자가 같은 좌석을 동시에 예매하는 상황을 가정한 백엔드 API 서버입니다.
좌석 중복 예매를 막는 **동시성 제어**, 미결제 좌석을 되돌리는 **자동 취소 스케줄러**, 무상태(Stateless) **JWT 인증**을 구현했습니다.

## 🛠 기술 스택
* **Backend:** Java 21, Spring Boot 4.0.5, Spring Data JPA, Spring Security
* **Database:** MySQL (테스트: H2 인메모리)
* **Authentication:** JWT (jjwt 0.11.5)
* **Testing:** JUnit 5, Mockito, Apache JMeter, Postman

---

## 🔥 핵심 기술 및 트러블슈팅

### 1. 낙관적 락(Optimistic Lock)을 활용한 동시성 제어

* **문제 상황:** 여러 사용자가 같은 좌석을 동시에 예매하면 중복 예매(Overbooking)가 생길 수 있는 Race Condition.
* **해결 방법:** `Seat` 엔티티에 JPA `@Version`을 적용. 좌석 UPDATE 시 `WHERE version = ?`로 버전을 검증해 먼저 커밋한 1건만 성공하고, 나머지는 `ObjectOptimisticLockingFailureException` 또는 버전 불일치 `IllegalStateException`으로 실패.
* **검증:** `BookingServiceConcurrencyTest` — `ExecutorService` + `CountDownLatch`로 같은 좌석에 100건 동시 요청 시 성공 1건, 실패 99건, 예매 레코드 1건을 확인.

### 2. Spring Scheduler 기반 미결제 예매 자동 취소

* **문제 상황:** 예매 후 결제하지 않은 좌석이 계속 선점(HELD) 상태로 남는 문제.
* **해결 방법:** `@Scheduled`로 1분마다 실행되는 스케줄러를 구현. 예매 후 5분이 지나도록 결제되지 않은 예매를 취소하고 좌석을 `AVAILABLE`로 되돌림.

### 3. JWT Access / Refresh Token 인증

* **해결 방법:** Spring Security와 JWT로 무상태(Stateless) 인증 구성.
  * Access Token(1시간)과 DB에 저장되는 Refresh Token(7일)을 분리 발급. 로그아웃 시 Refresh Token 삭제.
  * `JwtFilter`에서 요청마다 토큰을 검증하고 인증 정보를 설정. 예매 API는 요청 바디가 아니라 토큰의 로그인 ID로 사용자를 식별.

---

## 🧯 트러블슈팅

### (1) 좌석 충돌 요청이 500으로 응답되던 문제
* **현상:** 동시 예매에서 진 요청이 `500 Internal Server Error`로 응답. 정상적인 경쟁 결과인데 서버 오류로 보임.
* **원인:** 예외 처리기가 없어 `IllegalStateException`과, 커밋 시점에 발생하는 `ObjectOptimisticLockingFailureException`이 Spring 기본 오류 응답(500)으로 처리됨. 낙관적 락 예외는 서비스 메서드가 끝난 뒤 트랜잭션 커밋에서 발생하므로 서비스 내부 try/catch로는 잡을 수 없음.
* **해결:** `@RestControllerAdvice`(`GlobalExceptionHandler`)로 전역 예외 처리 추가. 응답 바디는 `{code, message}`.
  * `ObjectOptimisticLockingFailureException`, `IllegalStateException` → `409 Conflict`
  * `IllegalArgumentException` → `400 Bad Request`
* 재시도는 넣지 않음. 좌석은 한 명만 가질 수 있어 재시도해도 결과가 같기 때문.

### (2) IDENTITY + 외래키 공유 락으로 인한 MySQL 데드락
* **현상:** 409 처리 후 부하 테스트에서 `CannotAcquireLockException: Deadlock found when trying to get lock`으로 500이 1건 발생. 위치는 `update seats ... where seat_id=? and version=?`.
* **원인:** `Booking`은 `GenerationType.IDENTITY`라서 `bookingRepository.save()` 호출 즉시 INSERT가 실행되고, 좌석 UPDATE는 커밋 시 flush에서 실행됨.
  1. 트랜잭션 A, B가 각각 `bookings` INSERT → 외래키(`seat_id`) 검사로 같은 `seats` 행에 **공유 락(S)** 획득
  2. A, B가 각각 `seats` UPDATE → **배타 락(X)** 이 필요하지만 상대의 S 락을 기다림 → 데드락
* **해결:** `holdSeat()` 직후 `seatRepository.saveAndFlush(seat)`로 좌석 UPDATE를 INSERT보다 먼저 실행.
  * 먼저 온 트랜잭션이 X 락을 잡고, 나머지는 S 락 없이 그 X 락만 기다리므로 순환 대기가 생기지 않음.
  * 기다리던 트랜잭션은 커밋된 최신 version을 보고 UPDATE 0건 → `ObjectOptimisticLockingFailureException` → 409.
* `CannotAcquireLockException`을 409로 덮지 않고 락 획득 순서를 바꿔 원인을 제거함.

### (3) 미결제 자동 취소 시 좌석이 복구되지 않던 버그
* **현상:** 5분 미결제로 예매가 취소돼도 좌석이 다시 예매 가능 상태가 되지 않음.
* **원인:** 스케줄러가 `releaseSeat()`가 아니라 `reserveSeat()`를 호출해 좌석이 `HELD → RESERVED`(판매 완료)로 바뀜.
* **해결:** `releaseSeat()`(→ `AVAILABLE`)로 수정. `BookingSchedulerTest`로 예매 `CANCELLED`, 좌석 `AVAILABLE`을 검증.

---

## 📊 부하 테스트 결과 (JMeter, 같은 좌석 동시 예매 1,000 스레드)

> 로컬 환경 한계로 일부 요청은 서버에 도달하지 못하고 연결 거부(Connection refused)로 끝났습니다.
> 아래 수치는 **서버에 도달한 요청만** 기준으로 집계했습니다.

| 단계 | 서버 도달 | 201 Created | 409 Conflict | 500 Error |
|---|---:|---:|---:|---:|
| 수정 전 | 479 | 1 | 0 | 478 |
| 409 처리 후 | 208 | 1 | 206 | 1 (Deadlock found) |
| 데드락 수정 후 | 399 | 1 | 398 | 0 |

---

## 📋 API 명세서

### User & Auth API
* `POST /api/v1/users/signup` : 회원가입 (BCrypt 단방향 암호화)
* `POST /api/v1/users/login` : 로그인 (Access & Refresh Token 발급)
* `POST /api/v1/users/refresh-token` : 토큰 재발급
* `POST /api/v1/users/logout` : 로그아웃 (Refresh Token 삭제)
* `PATCH /api/v1/users/my/password` : 비밀번호 변경
* `PATCH /api/v1/users/my` : 내 정보 수정
* `DELETE /api/v1/users/my` : 회원 탈퇴

### Performance & Seat API
* `GET /api/v1/performances` : 공연 목록
* `GET /api/v1/performances/{perf_id}/schedules` : 공연 회차 목록
* `GET /api/v1/schedules/{sched_id}/seats` : 회차별 좌석 목록

### Booking & Payment API
* `POST /api/v1/bookings` : 좌석 예매 (인증 필요, 낙관적 락 적용)
* `POST /api/v1/payments` : 결제 (좌석 `HELD → RESERVED`)

---

## 👨‍💻 프로젝트 실행 방법
1. MySQL에 `ticketing_db` 데이터베이스를 생성합니다.
2. 환경변수 `DB_USERNAME`, `DB_PASSWORD`를 설정합니다.
3. 애플리케이션을 실행한 후 `http://localhost:8080`에 접속하면 HTML UI로 기능을 테스트할 수 있습니다.
4. 테스트는 H2 인메모리 DB로 실행되므로 MySQL 없이 `./gradlew test`로 돌릴 수 있습니다.
