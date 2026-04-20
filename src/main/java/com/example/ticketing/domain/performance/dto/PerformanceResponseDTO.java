package com.example.ticketing.domain.performance.dto;

import com.example.ticketing.domain.performance.entity.Performance;
import lombok.Getter;

@Getter
public class PerformanceResponseDTO {
    private Long perfId;
    private String perfName;
    private String venue;
    private Integer duration;

    public PerformanceResponseDTO(Performance perf) {
        this.perfId = perf.getPerfId();
        this.perfName = perf.getPerfName();
        this.venue = perf.getVenue();
        this.duration = perf.getDuration();
    }
}