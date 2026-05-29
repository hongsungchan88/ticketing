package com.example.ticketing.domain.seat.entity;

import com.example.ticketing.domain.performance.entity.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "seats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long seatId;

    @Column(nullable = false, length = 20)
    private String grade;

    @Column(nullable = false, length = 50)
    private String seatName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'AVAILABLE'")
    private String seatStatus;

    @Version // 낙관적 락 용도, 해당 필드를 버전 관리용으로 사용
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sched_id")
    private Schedule schedule;

    @Builder
    public Seat(String grade, String seatName, Integer price, String seatStatus, Schedule schedule) {
        this.grade = grade;
        this.seatName = seatName;
        this.price = price;
        this.seatStatus = seatStatus;
        this.schedule = schedule;
    }

    // 좌석 상태 변경
    public void holdSeat() {
        if (!this.seatStatus.equals("AVAILABLE")) {
            throw new IllegalStateException("이미 선점되거나 예매된 자석입니다.");
        }
        this.seatStatus = "HELD";
    }

    public void reserveSeat() {
        if (!this.seatStatus.equals("HELD")) {
            throw new IllegalStateException("결제 대기 중인 좌석이 아닙니다.");
        }
        this.seatStatus = "RESERVED";
    }

    public void releaseSeat() {
        this.seatStatus = "AVAILABLE";
    }
}