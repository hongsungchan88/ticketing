package com.example.ticketing.domain.booking.repository;

import com.example.ticketing.domain.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
