package com.aniket.distributed_job_scheduler.services;

import java.util.List;
import java.util.UUID;

import com.aniket.distributed_job_scheduler.dto.JobRequestDto;
import com.aniket.distributed_job_scheduler.entities.Job;

public interface JobService {
    List<Job> claimJobs();

    UUID createJob(JobRequestDto jobRequestDto);

    Job getJobById(UUID id);

    void resetJob(UUID id);
}
