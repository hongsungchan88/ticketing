package com.example.ticketing.domain.booking.entity;

import com.example.ticketing.domain.seat.entity.Seat;
import com.example.ticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "bookings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long bookId;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'INITIATED'")
    private String bookStatus;

    @Column(nullable = false)
    @CreationTimestamp() // 인스턴스가 DB에 삽입될 때 단 한번만 생성 된다.
    private LocalDateTime createdAt;

    private LocalDateTime canceledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Builder
    public Booking(String bookStatus, User user, Seat seat) {
        this.bookStatus = bookStatus;
        this.user = user;
        this.seat = seat;
    }
}
