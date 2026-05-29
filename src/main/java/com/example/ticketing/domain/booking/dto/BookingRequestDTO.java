package com.example.ticketing.domain.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookingRequestDTO {
    private Long seatId;
    private Long version; // 좌석 버전
}
