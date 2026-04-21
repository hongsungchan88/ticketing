package com.example.ticketing.domain.performance.dto;

import com.example.ticketing.domain.performance.entity.Performance;
import lombok.Getter;

@Getter
public class PerformanceResponseDTO {
    private Long perfId;
    private String perfName;
    private String venue;
    private Integer duration;

    public static PerformanceResponseDTO from(Performance perf) {
        PerformanceResponseDTO dto = new PerformanceResponseDTO();
        dto.perfId = perf.getPerfId();
        dto.perfName = perf.getPerfName();
        dto.venue = perf.getVenue();
        dto.duration = perf.getDuration();
        return dto;
    }
}