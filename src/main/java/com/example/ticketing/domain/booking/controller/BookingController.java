package com.example.ticketing.domain.booking.controller;


import com.example.ticketing.domain.booking.dto.BookingRequestDTO;
import com.example.ticketing.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/bookings")
    // 💡 1. 파라미터에 Authentication 추가
    public ResponseEntity<Long> createBooking(@RequestBody BookingRequestDTO requestDTO, Authentication authentication) {

        // 💡 2. JwtFilter가 금고에 넣어두었던 로그인 ID(loginId)를 꺼냅니다!
        String loginId = authentication.getName();

        log.info("예매 요청 API 호출 - 인증된 사용자 ID: {}, 좌석 ID: {}", loginId, requestDTO.getSeatId());

        // 3. 서비스로 DTO와 함께 토큰에서 꺼낸 확실한 ID를 넘겨줍니다.
        Long bookingId = bookingService.createBooking(requestDTO, loginId);

        log.info("예매 선점 성공! 예매 번호: {}", bookingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
    }
}
