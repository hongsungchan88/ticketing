package com.example.ticketing.domain.booking.service;

import com.example.ticketing.domain.booking.dto.BookingRequestDTO;
import com.example.ticketing.domain.booking.entity.Booking;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import com.example.ticketing.domain.user.entity.User;
import com.example.ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    public Long createBooking(BookingRequestDTO request) {
        // 사용자와 좌석 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));
        // 동시성 방어
        if (!seat.getVersion().equals(request.getVersion())) {
            throw new IllegalArgumentException("누군가 먼저 좌석을 선점했습니다. 다른 좌석을 선택해주세요.");
        }
        seat.holdSet();

        // 예매 내역 생성
        Booking booking = Booking.builder()
                .bookStatus("INITIATED")
                .user(user)
                .seat(seat)
                .build();

        // 예매 번호 반환
        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking.getBookId();
    }
}