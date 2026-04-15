package com.example.ticketing.domain.performance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long schedId;

    @Column(nullable = false)
    private LocalDate perfDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perf_id")
    private Performance perf;

    @Builder
    public Schedule(LocalDate perfDate, LocalTime startTime, LocalTime endTime, Performance perf) {
        this.perfDate = perfDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.perf = perf;
    }
}
