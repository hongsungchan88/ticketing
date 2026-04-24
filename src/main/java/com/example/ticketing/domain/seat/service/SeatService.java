package com.example.ticketing.domain.seat.service;

import com.example.ticketing.domain.seat.dto.SeatResponseDTO;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {
    private final SeatRepository seatRepository;

    public List<SeatResponseDTO> getSeatsBySchedule(Long schedId) {
        List<Seat> seats = seatRepository.findAllBySchedule_SchedId(schedId);

        return seats.stream()
                .map(SeatResponseDTO::from)
                .toList();
    }
}
