package com.example.ticketing.domain.booking.repository;

import com.example.ticketing.domain.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.bookStatus = :status AND b.createdAt < :time")
    List<Booking> findTimeoutBookings(@Param("status") String status, @Param("time") LocalDateTime time);
}