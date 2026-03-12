package com.aniket.distributed_job_scheduler.services;

import java.util.List;

import com.aniket.distributed_job_scheduler.entities.Job;

public interface JobService {
    List<Job> claimJobs();
}
