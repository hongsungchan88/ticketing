package com.example.ticketing.domain.performance.dto;

import com.example.ticketing.domain.performance.entity.Schedule;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class ScheduleResponseDTO {
    private Long schedId;
    private LocalDate perfDate;
    private LocalTime startTime;
    private LocalTime endTime;

    // perf entity에서 필요한 정보만 가져옴
    private Long perfId;
    private String perfName;

    public static ScheduleResponseDTO from(Schedule sched) {
        ScheduleResponseDTO dto = new ScheduleResponseDTO();
        dto.schedId = sched.getSchedId();
        dto.perfDate = sched.getPerfDate();
        dto.startTime = sched.getStartTime();
        dto.endTime = sched.getEndTime();

        dto.perfId = sched.getPerf().getPerfId();
        dto.perfName = sched.getPerf().getPerfName();
        return dto;
    }
}