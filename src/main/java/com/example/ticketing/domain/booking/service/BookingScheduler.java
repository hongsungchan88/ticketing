package com.example.ticketing.domain.booking.service;

import com.example.ticketing.domain.booking.entity.Booking;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component // 스프링 빈 등록, 백그라운드 실행
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    // 1분마다 주기적으로 실행되도록 크론식 설정
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cancelUnpaidBookings() {
        // 기준 시간 설정
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

        // 여전히 결제 대기 상태인 예매 내역 조회
        List<Booking> targetBookings = bookingRepository.findTimeoutBookings("INITIATED", fiveMinutesAgo);

        if (!targetBookings.isEmpty()) {
            log.info("5분 미결제 자동 취소 대상 발견 : {}건",  targetBookings.size());
        }

        for (Booking booking : targetBookings) {
            booking.cancelBooking(); // 예매 내역 취소 처리
            booking.getSeat().reserveSeat(); // 좌석 상태 원상 복구
            log.info("예매 자동 취소 완료 - 예매 번호 : {}, 좌석 번호 : {}", booking.getBookId(), booking.getSeat().getSeatId());
        }
    }
}