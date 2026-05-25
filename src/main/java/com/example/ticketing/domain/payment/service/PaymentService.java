package com.example.ticketing.domain.payment.service;

import com.example.ticketing.domain.booking.entity.Booking;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import com.example.ticketing.domain.payment.dto.PaymentRequestDTO;
import com.example.ticketing.domain.payment.entity.Payment;
import com.example.ticketing.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Long processPayment(PaymentRequestDTO requestDTO) {
        // 예매 내역 조회
        Booking booking = bookingRepository.findById(requestDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 내역입니다."));

        // 금액 검증
        if (!booking.getSeat().getPrice().equals(requestDTO.getAmount())) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        booking.confirmBooking(); // 예매 상태 : INITIATED -> CONFIRMED
        booking.getSeat().reserveSeat(); // 좌석 상태 : HELD -> RESERVED

        // 결제 내역 생성 및 저장
        Payment payment = Payment.builder()
                .payStatus("PAID") // 결제 완료
                .payAt(LocalDateTime.now())
                .book(booking)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getPayId();
    }
}