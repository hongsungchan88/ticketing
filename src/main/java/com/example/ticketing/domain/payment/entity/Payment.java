package com.example.ticketing.domain.payment.entity;

import com.example.ticketing.domain.booking.entity.Booking;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long payId;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'PENDING'")
    private String payStatus;

    private LocalDateTime payAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Booking book;

    @Builder
    public Payment(String payStatus, LocalDateTime payAt, Booking book) {
        this.payStatus = payStatus;
        this.payAt = payAt;
        this.book = book;
    }
}