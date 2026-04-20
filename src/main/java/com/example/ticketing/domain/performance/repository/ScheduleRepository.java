package com.example.ticketing.domain.performance.repository;

import com.example.ticketing.domain.performance.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByPerf_PerfId(Long perfId);
}