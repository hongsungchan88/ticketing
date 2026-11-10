package com.example.ticketing.domain.user.service;

import com.example.ticketing.global.exception.LoginLockedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 로그인 ID별 연속 실패 횟수를 세고 5회 실패하면 10분 동안 로그인을 막는다.
// shortcut: 서버 메모리에 저장해 서버를 여러 대로 늘리면 서로 공유되지 않음, 다중 서버 배포 시 DB나 Redis로 옮길 것
@Component
public class LoginAttemptService {

    static final int MAX_FAILURES = 5;
    static final Duration LOCK_TIME = Duration.ofMinutes(10);

    private record Attempt(int failures, Instant lastFailureAt, Instant lockedUntil) {}

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final Clock clock;

    public LoginAttemptService() {
        this(Clock.systemDefaultZone());
    }

    LoginAttemptService(Clock clock) {
        this.clock = clock;
    }

    public void checkLocked(String loginId) {
        Attempt attempt = attempts.get(loginId);
        if (attempt != null && attempt.lockedUntil() != null && clock.instant().isBefore(attempt.lockedUntil())) {
            throw new LoginLockedException("로그인 시도가 너무 많습니다. 10분 후 다시 시도해주세요.");
        }
    }

    public void recordFailure(String loginId) {
        Instant now = clock.instant();
        attempts.compute(loginId, (id, old) -> {
            // 마지막 실패 후 10분이 지났으면 처음부터 다시 센다
            int failures = (old == null || isStale(old, now)) ? 1 : old.failures() + 1;
            Instant lockedUntil = failures >= MAX_FAILURES ? now.plus(LOCK_TIME) : null;
            return new Attempt(failures, now, lockedUntil);
        });
    }

    public void recordSuccess(String loginId) {
        attempts.remove(loginId);
    }

    // 없는 ID로 계속 시도해도 메모리가 계속 늘지 않도록 오래된 기록을 지운다
    @Scheduled(fixedRate = 600_000)
    public void removeStaleAttempts() {
        Instant now = clock.instant();
        attempts.values().removeIf(attempt -> isStale(attempt, now));
    }

    private boolean isStale(Attempt attempt, Instant now) {
        boolean lockOver = attempt.lockedUntil() == null || !now.isBefore(attempt.lockedUntil());
        return lockOver && !now.isBefore(attempt.lastFailureAt().plus(LOCK_TIME));
    }
}
