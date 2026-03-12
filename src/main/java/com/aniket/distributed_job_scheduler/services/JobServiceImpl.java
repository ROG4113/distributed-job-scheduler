package com.aniket.distributed_job_scheduler.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aniket.distributed_job_scheduler.entities.Job;
import com.aniket.distributed_job_scheduler.entities.JobExecution;
import com.aniket.distributed_job_scheduler.model.JobStatus;
import com.aniket.distributed_job_scheduler.repositories.JobExecutionRepository;
import com.aniket.distributed_job_scheduler.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    // To avoid race condition(as we are using pessimistic locking),
    // we need to change the jobStatus and exceute the job in one go(method).
    @Override
    @Transactional
    public List<Job> claimJobs() {
        log.info("Polling for pending jobs to claim");

        List<Job> jobs=jobRepository.findJobsToExecute(LocalDateTime.now());

        if(jobs.isEmpty()){
            log.info("Found no jobs, returning...");
            return Collections.emptyList();
        }
        
        log.info("Found " + jobs.size() + " jobs.");

        jobs.forEach(job->{
            // changing the status
            job.setStatus(JobStatus.RUNNING);

            // executing the job
            JobExecution execution=JobExecution.builder()
                .job(job)
                .status(JobStatus.RUNNING)
                .startedAt(LocalDateTime.now())    
                .build();

            jobExecutionRepository.save(execution);
        });

        return jobRepository.saveAll(jobs);
    }
}
