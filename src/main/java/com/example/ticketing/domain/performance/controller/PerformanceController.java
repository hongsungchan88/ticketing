package com.example.ticketing.domain.performance.controller;

import com.example.ticketing.domain.performance.dto.PerformanceResponseDTO;
import com.example.ticketing.domain.performance.dto.ScheduleResponseDTO;
import com.example.ticketing.domain.performance.service.PerformanceService;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor // final이 붙은 핃드의 생성자를 자동으로 만들어 의존성을 주입한다.
public class PerformanceController {

    private final PerformanceService performanceService;

    @GetMapping("/performances")
    public ResponseEntity<List<PerformanceResponseDTO>> searchAll() {
        log.info("전체 공연 목록 조회");
        List<PerformanceResponseDTO> response = performanceService.searchAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/performances/{perf_id}/schedules")
    public ResponseEntity<List<ScheduleResponseDTO>> schedByPerfId(@PathVariable("perf_id") Long perfId) {
        log.info("특정 공연 전체 회차 일정 조회");
        List<ScheduleResponseDTO> response = performanceService.schedByPerfId(perfId);
        return ResponseEntity.ok(response);
    }

    // create

    // update

    // delete
}