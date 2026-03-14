package com.aniket.distributed_job_scheduler.dto;

import lombok.Data;

@Data
public class JobRequestDto {
    private String jobType;
    private String payload;
    private Integer ScheduledInMinutes;
}
