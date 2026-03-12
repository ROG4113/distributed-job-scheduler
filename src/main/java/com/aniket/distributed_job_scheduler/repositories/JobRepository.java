package com.aniket.distributed_job_scheduler.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.aniket.distributed_job_scheduler.entities.Job;

import jakarta.persistence.LockModeType;

public interface JobRepository extends JpaRepository<Job, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT j from Job j WHERE j.status='PENDING' AND j.scheduledTime<=:now")
    List<Job> findJobsToExecute(LocalDateTime now);
}
