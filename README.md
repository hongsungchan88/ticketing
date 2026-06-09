# 🎫 Ticketing System (콘서트 티켓 예매 API)

> **대규모 트래픽을 고려한 동시성 제어 및 JWT 기반 안전한 콘서트 티켓팅 시스템 REST API**

## 💡 프로젝트 개요
다수의 사용자가 동시에 한정된 좌석을 예매하는 콘서트 티켓팅 상황을 가정하여 개발한 백엔드 API 서버입니다. 
데이터 정합성을 보장하기 위한 **동시성 제어**, 미결제 좌석의 **자동 롤백 스케줄러**, 그리고 무상태(Stateless) 기반의 **안전한 사용자 인증 아키텍처**를 구축하는 데 집중했습니다.

## 🛠 기술 스택
* **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security
* **Database:** MySQL 8.0
* **Authentication:** JWT (JSON Web Token)
* **Testing:** Postman, Apache JMeter

---

## 🔥 핵심 기술 및 트러블슈팅 (Key Features)

### 1. 낙관적 락(Optimistic Lock)을 활용한 동시성 제어

* **문제 상황:** 인기 콘서트 예매 시, 다수의 사용자가 동시에 같은 좌석을 클릭할 경우 초과 예약(Overbooking)이 발생하는 Race Condition 문제 발생.
* **해결 방법:** JPA `@Version` 어노테이션을 활용하여 낙관적 락(Optimistic Lock) 구현. 데이터베이스 수준에서 버전(Version)을 검증하여, 최초의 1건만 커밋하고 나머지 요청은 예외(`IllegalStateException`) 처리.
* **성과:** 1,000명의 동시 접속 부하 테스트(JMeter) 결과, **단 1명만 예매 성공하고 999명은 안전하게 튕겨내는 것을 데이터로 증명.**
  * [여기에 JMeter Summary Report 캡처 이미지 삽입]
  * [여기에 View Results Tree 초록/빨간 방패 캡처 이미지 삽입]

### 2. Spring Scheduler 기반 예매 자동 롤백 시스템

* **문제 상황:** 사용자가 예매만 해두고 결제를 하지 않는 '유령 좌석' 문제 방지 필요.
* **해결 방법:** `@Scheduled`를 활용하여 1분마다 주기적으로 실행되는 백그라운드 스케줄러 구현. 예매 후 5분이 지나도록 결제되지 않은 좌석의 상태를 'AVAILABLE'로 자동 롤백.
* **성과:** 불필요한 좌석 선점을 방지하고 자원의 회전율을 극대화함.

### 3. JWT Access / Refresh Token 기반 보안 아키텍처

* **문제 상황:** 기존 세션 방식은 서버 메모리를 차지하여 스케일 아웃에 불리하며, Access Token만 사용할 경우 탈취 위험이 존재.
* **해결 방법:** Spring Security와 JWT를 도입하여 무상태(Stateless) 인증 환경 구축.
  * 생명주기가 짧은 Access Token(1시간)과 DB에 저장되어 관리되는 Refresh Token(7일)을 분리 발급하여 보안성 강화.
  * `JwtFilter`를 구현하여 API 요청마다 인가(Authorization) 처리 및 위조 토큰 검증.

---

## 📋 API 명세서 (주요 기능)

### User & Auth API
* `POST /api/v1/users/signup` : 회원가입 (BCrypt 단방향 암호화)
* `POST /api/v1/users/login` : 로그인 (Access & Refresh Token 발급)
* `POST /api/v1/users/refresh-token` : 토큰 재발급
* `POST /api/v1/users/logout` : 로그아웃 (Refresh Token 파기)
* `PATCH /api/v1/users/my/password` : 비밀번호 변경
* `DELETE /api/v1/users/my` : 회원 탈퇴

### Booking API
* `POST /api/v1/bookings` : 좌석 예매 (인가된 사용자만 접근 가능, 동시성 제어 적용)

---

## 👨‍💻 프로젝트 실행 방법
1. MySQL에 `ticketing_db` 데이터베이스를 생성합니다.
2. `application.yml` 파일에 환경변수를 세팅합니다.
   * `DB_USERNAME`, `DB_PASSWORD`
   * `JWT_SECRET` (임의의 긴 문자열)
3. 어플리케이션을 실행한 후, `http://localhost:8080`에 접속하여 제공되는 HTML UI를 통해 즉시 기능을 테스트해 볼 수 있습니다.
