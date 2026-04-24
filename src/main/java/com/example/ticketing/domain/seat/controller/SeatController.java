package com.example.ticketing.domain.seat.controller;

import com.example.ticketing.domain.seat.dto.SeatResponseDTO;
import com.example.ticketing.domain.seat.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SeatController {
    private final SeatService seatService;

    // 특정 공연 좌석 조회
    @GetMapping("/schedules/{sched_id}/seats")
    public ResponseEntity<List<SeatResponseDTO>> getSeatsBySchedule(@PathVariable("sched_id") Long schedId) {
        log.info("회차 ID: {}", schedId);
        List<SeatResponseDTO> response = seatService.getSeatsBySchedule(schedId);
        return ResponseEntity.ok(response);
    }
}
