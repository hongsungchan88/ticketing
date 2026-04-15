package com.example.ticketing.domain.performance.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA 엔티티로 지정
@Getter
@Table(name = "performances")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 파라미터 없는 디폴트 생성자 자동 생성, JPA 프록시를 위해 PROTECTED로 제한
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 값 자동으로 생성
    private Long perfId;

    @Column(nullable = false, length = 255)
    private String perfName;

    @Column(nullable = false, length = 255)
    private String venue;

    @Column(nullable = false)
    private Integer duration;

    @Builder
    public Performance(String perfName, String venue, Integer duration) {
        this.perfName = perfName;
        this.venue = venue;
        this.duration = duration;
    }
}
