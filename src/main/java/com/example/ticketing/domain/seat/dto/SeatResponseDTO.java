package com.example.ticketing.domain.seat.dto;

import com.example.ticketing.domain.seat.entity.Seat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponseDTO {
    private Long seatId;
    private String grade;
    private String seatName;
    private Integer price;
    private String seatStatus;

    private Long version;

    public static SeatResponseDTO from(Seat seat) {
        return SeatResponseDTO.builder()
                .seatId(seat.getSeatId())
                .grade(seat.getGrade())
                .seatName(seat.getSeatName())
                .price(seat.getPrice())
                .seatStatus(seat.getSeatStatus())
                .version(seat.getVersion())
                .build();
    }
}
