package com.example.ticketing.domain.performance.service;

import com.example.ticketing.domain.performance.dto.PerformanceResponseDTO;
import com.example.ticketing.domain.performance.dto.ScheduleResponseDTO;
import com.example.ticketing.domain.performance.entity.Performance;
import com.example.ticketing.domain.performance.entity.Schedule;
import com.example.ticketing.domain.performance.repository.PerformanceRepository;
import com.example.ticketing.domain.performance.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용
public class PerformanceService {
    private final PerformanceRepository performanceRepository;
    private final ScheduleRepository scheduleRepository;

    // 전체 조회
    public List<PerformanceResponseDTO> searchAll() {
        // DB에 담긴 전체 공연 목록을 가져와서 DTO로 변환
        List<Performance> perfList = performanceRepository.findAll();
        return perfList.stream()
                .map(PerformanceResponseDTO::from)
                .toList();
    }

    // 특정 공연의 전체 회차 일정 조회
    public List<ScheduleResponseDTO> schedByPerfId(Long perfId) {
        // 특정 공연 id와 일치하는 일정을 가져오기
        List<Schedule> schedList = scheduleRepository.findAllByPerf_PerfId(perfId);
        return schedList.stream()
                .map(ScheduleResponseDTO::from)
                .toList();
    }
}