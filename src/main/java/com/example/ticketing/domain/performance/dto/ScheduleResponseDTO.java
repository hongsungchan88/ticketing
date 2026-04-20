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

    public ScheduleResponseDTO(Schedule sched) {
        this.schedId = sched.getSchedId();
        this.perfDate = sched.getPerfDate();
        this.startTime = sched.getStartTime();
        this.endTime = sched.getEndTime();

        this.perfId = sched.getPerf().getPerfId();
        this.perfName = sched.getPerf().getPerfName();
    }
}