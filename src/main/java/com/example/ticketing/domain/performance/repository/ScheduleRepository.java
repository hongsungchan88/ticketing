package com.example.ticketing.domain.performance.repository;

import com.example.ticketing.domain.performance.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
