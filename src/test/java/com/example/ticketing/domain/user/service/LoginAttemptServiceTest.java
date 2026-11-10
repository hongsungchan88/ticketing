package com.example.ticketing.domain.user.service;

import com.example.ticketing.global.exception.LoginLockedException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginAttemptServiceTest {

    // 테스트에서 시간을 직접 움직이기 위한 시계
    static class TestClock extends Clock {
        Instant now = Instant.parse("2026-11-10T00:00:00Z");
        void plus(Duration d) { now = now.plus(d); }
        @Override public Instant instant() { return now; }
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
    }

    private final TestClock clock = new TestClock();
    private final LoginAttemptService service = new LoginAttemptService(clock);

    private void fail(int times) {
        for (int i = 0; i < times; i++) service.recordFailure("user1");
    }

    @Test
    void 네_번_실패까지는_로그인할_수_있다() {
        fail(4);
        assertThatCode(() -> service.checkLocked("user1")).doesNotThrowAnyException();
    }

    @Test
    void 다섯_번_실패하면_잠기고_10분_뒤_풀린다() {
        fail(5);
        assertThatThrownBy(() -> service.checkLocked("user1")).isInstanceOf(LoginLockedException.class);

        clock.plus(Duration.ofMinutes(9));
        assertThatThrownBy(() -> service.checkLocked("user1")).isInstanceOf(LoginLockedException.class);

        clock.plus(Duration.ofMinutes(1));
        assertThatCode(() -> service.checkLocked("user1")).doesNotThrowAnyException();
    }

    @Test
    void 잠금이_풀린_뒤에는_다시_다섯_번까지_시도할_수_있다() {
        fail(5);
        clock.plus(Duration.ofMinutes(10));
        fail(4);
        assertThatCode(() -> service.checkLocked("user1")).doesNotThrowAnyException();
    }

    @Test
    void 로그인에_성공하면_실패_횟수가_초기화된다() {
        fail(4);
        service.recordSuccess("user1");
        fail(4);
        assertThatCode(() -> service.checkLocked("user1")).doesNotThrowAnyException();
    }

    @Test
    void 다른_아이디의_실패는_서로_영향을_주지_않는다() {
        fail(5);
        assertThatCode(() -> service.checkLocked("user2")).doesNotThrowAnyException();
    }
}
