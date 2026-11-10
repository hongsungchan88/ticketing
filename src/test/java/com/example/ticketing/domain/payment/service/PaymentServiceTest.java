package com.example.ticketing.domain.payment.service;

import com.example.ticketing.domain.booking.entity.Booking;
import com.example.ticketing.domain.booking.repository.BookingRepository;
import com.example.ticketing.domain.payment.dto.PaymentRequestDTO;
import com.example.ticketing.domain.payment.repository.PaymentRepository;
import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final PaymentService paymentService = new PaymentService(paymentRepository, bookingRepository);

    private Booking bookingOf(String ownerLoginId) {
        User owner = User.builder().loginId(ownerLoginId).pw("pw").userName("owner").email("o@test.com").build();
        Seat seat = Seat.builder().grade("VIP").seatName("A1").price(100000).seatStatus("HELD").build();
        Booking booking = Booking.builder().bookStatus("INITIATED").user(owner).seat(seat).build();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        return booking;
    }

    private PaymentRequestDTO request() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        ReflectionTestUtils.setField(dto, "bookId", 1L);
        ReflectionTestUtils.setField(dto, "amount", 100000);
        return dto;
    }

    @Test
    void 다른_사람의_예매는_결제할_수_없다() {
        Booking booking = bookingOf("owner");

        assertThatThrownBy(() -> paymentService.processPayment(request(), "attacker"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(booking.getBookStatus()).isEqualTo("INITIATED");
        assertThat(booking.getSeat().getSeatStatus()).isEqualTo("HELD");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void 본인의_예매는_결제된다() {
        Booking booking = bookingOf("owner");
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        paymentService.processPayment(request(), "owner");

        assertThat(booking.getBookStatus()).isEqualTo("CONFIRMED");
        assertThat(booking.getSeat().getSeatStatus()).isEqualTo("RESERVED");
    }
}
