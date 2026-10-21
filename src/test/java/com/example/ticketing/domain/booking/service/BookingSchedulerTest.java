package com.example.ticketing.domain.booking.service;

import com.example.ticketing.domain.booking.entity.Booking;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingSchedulerTest {

    @Test
    void 미결제_예매_취소시_좌석은_AVAILABLE로_복구된다() {
        Seat seat = Seat.builder().grade("VIP").seatName("A1").price(100000).seatStatus("HELD").build();
        Booking booking = Booking.builder().bookStatus("INITIATED").seat(seat).build();

        BookingRepository repository = mock(BookingRepository.class);
        when(repository.findTimeoutBookings(eq("INITIATED"), any())).thenReturn(List.of(booking));

        new BookingScheduler(repository).cancelUnpaidBookings();

        assertThat(booking.getBookStatus()).isEqualTo("CANCELLED");
        assertThat(seat.getSeatStatus()).isEqualTo("AVAILABLE"); // 버그 시절엔 RESERVED가 됐음
    }
}
