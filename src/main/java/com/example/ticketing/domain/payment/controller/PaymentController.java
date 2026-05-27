package com.example.ticketing.domain.payment.controller;

import com.example.ticketing.domain.payment.dto.PaymentRequestDTO;
import com.example.ticketing.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ResponseEntity<Long> processPayment(@RequestBody PaymentRequestDTO requestDTO) {
        log.info("결제 처리 요청 - 예매 번호 : {}, 금액 : {}", requestDTO.getBookId(), requestDTO.getAmount());

        Long payId = paymentService.processPayment(requestDTO);

        log.info("결제 및 예매 확정 완료 - 결제 번호 : {}", payId);
        return ResponseEntity.ok(payId);
    }
}