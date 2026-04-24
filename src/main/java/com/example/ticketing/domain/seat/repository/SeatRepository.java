package com.example.ticketing.domain.seat.repository;

import com.example.ticketing.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllBySchedule_SchedId(Long scheduleId);
}