package com.example.ticketing.domain.booking.service;

import com.example.ticketing.domain.booking.dto.BookingRequestDTO;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

// @Transactional을 붙이지 않음: 각 스레드가 실제로 커밋해야 version 충돌이 일어난다
@SpringBootTest
class BookingServiceConcurrencyTest {

    @Autowired BookingService bookingService;
    @Autowired BookingRepository bookingRepository;
    @Autowired SeatRepository seatRepository;
    @Autowired UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void 같은_좌석에_100명이_동시에_예매하면_1명만_성공한다() throws InterruptedException {
        int requests = 100;
        userRepository.save(User.builder().loginId("tester").pw("pw").userName("테스터").email("t@t.com").build());
        Seat seat = seatRepository.save(Seat.builder().grade("VIP").seatName("A1").price(100000).seatStatus("AVAILABLE").build());

        BookingRequestDTO dto = new BookingRequestDTO();
        ReflectionTestUtils.setField(dto, "seatId", seat.getSeatId());
        ReflectionTestUtils.setField(dto, "version", seat.getVersion());

        ExecutorService executor = Executors.newFixedThreadPool(32);
        CountDownLatch start = new CountDownLatch(1);     // 모든 스레드를 같은 순간에 출발시킴
        CountDownLatch done = new CountDownLatch(requests);
        AtomicInteger success = new AtomicInteger();
        Queue<Throwable> failures = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < requests; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    bookingService.createBooking(dto, "tester");
                    success.incrementAndGet();
                } catch (Throwable e) {
                    failures.add(e);
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        assertThat(success.get()).isEqualTo(1);
        assertThat(failures).hasSize(requests - 1)
                .allMatch(e -> e instanceof ObjectOptimisticLockingFailureException || e instanceof IllegalStateException);
        assertThat(bookingRepository.count()).isEqualTo(1);
        assertThat(seatRepository.findById(seat.getSeatId()).orElseThrow().getSeatStatus()).isEqualTo("HELD");
    }
}
