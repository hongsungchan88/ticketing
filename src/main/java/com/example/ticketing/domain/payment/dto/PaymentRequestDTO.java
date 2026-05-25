package com.example.ticketing.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestDTO {
    private Long bookId;
    private Integer amount;
}