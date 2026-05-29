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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    // 💡 1. 파라미터에 loginId를 추가로 받습니다.
    public Long createBooking(BookingRequestDTO requestDTO, String loginId) {

        // 💡 2. 클라이언트가 조작할 수 없는 "토큰에서 꺼낸 확실한 ID"로 회원을 찾습니다!
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Seat seat = seatRepository.findById(requestDTO.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        // ... 이하 기존 동시성 방어 로직(version 검사), 상태 변경, 저장 로직은 100% 동일하게 유지합니다! ...
        if (!seat.getVersion().equals(requestDTO.getVersion())) {
            throw new IllegalStateException("누군가 먼저 좌석을 선점했습니다. 다른 좌석을 선택해주세요.");
        }

        seat.holdSeat();

        Booking booking = Booking.builder()
                .bookStatus("INITIATED")
                .user(user)
                .seat(seat)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking.getBookId();
    }
}