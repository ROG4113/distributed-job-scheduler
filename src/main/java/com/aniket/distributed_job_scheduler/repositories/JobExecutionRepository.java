package com.aniket.distributed_job_scheduler.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aniket.distributed_job_scheduler.entities.JobExecution;

public interface JobExecutionRepository extends JpaRepository<JobExecution, UUID> {

}
