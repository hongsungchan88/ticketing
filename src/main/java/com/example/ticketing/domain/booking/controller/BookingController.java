package com.example.ticketing.domain.booking.controller;


import com.example.ticketing.domain.booking.dto.BookingRequestDTO;
import com.example.ticketing.domain.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> createBooking(@RequestBody BookingRequestDTO requestDTO) {
        log.info("유저 ID: {}, 좌석 ID: {}", requestDTO.getUserId(), requestDTO.getSeatId());
        Long bookingId = bookingService.createBooking(requestDTO);

        log.info("예매 성공! 예매 번호: {}", bookingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingId);
    }
}
