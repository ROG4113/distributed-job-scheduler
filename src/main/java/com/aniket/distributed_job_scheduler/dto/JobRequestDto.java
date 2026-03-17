package com.aniket.distributed_job_scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRequestDto {
    @NotNull
    @NotBlank
    private String jobType;

    @NotNull
    @NotBlank
    private String payload;

    @NotNull
    private Integer scheduledInMinutes;
}
