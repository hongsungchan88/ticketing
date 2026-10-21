package com.example.ticketing.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 모든 컨트롤러에서 던져진 예외를 한 곳에서 HTTP 응답으로 변환
public class GlobalExceptionHandler {

    public record ErrorResponse(String code, String message) {}

    // 낙관적 락 충돌: 커밋 시점에 version이 이미 바뀌어 UPDATE가 0건 → 다른 사람이 먼저 선점
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(ObjectOptimisticLockingFailureException e) {
        log.info("낙관적 락 충돌: {}", e.getMessage());
        return build(HttpStatus.CONFLICT, "누군가 먼저 좌석을 선점했습니다. 다른 좌석을 선택해주세요.");
    }

    // 비즈니스 상태 충돌 (이미 선점된 좌석, 버전 불일치 등)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException e) {
        return build(HttpStatus.CONFLICT, e.getMessage());
    }

    // 잘못된 요청 (존재하지 않는 좌석/사용자 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(status.name(), message));
    }
}
