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
import com.aniket.distributed_job_scheduler.workers.JobWorker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final JobExecutionRepository jobExecutionRepository;

    // Injects all implementations of JobWorker into this list
    // When Spring sees private final List<JobWorker> workers; in constructor,
    // it doesn't look for a single bean.
    // It scans entire project for every class that implements the JobWorker interface (like EmailWorker).
    // The Result: It automatically collects them all and puts them into that List for us.
    // If we create an SmsWorker tomorrow, 
    // Spring will automatically add it to this list without us changing a single line of code in the Service.
    private final List<JobWorker> workers;

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

        // same as, jobs.forEach(job -> this.processJob(job));
        jobs.forEach(this::processJob);

        return jobRepository.saveAll(jobs);
    }

    private void processJob(Job job) {
        try{
            // changing the status
            job.setStatus(JobStatus.RUNNING);

            // find the Email job type
            // This is Java Streams. Think of a stream as a "conveyor belt" for your data where you can filter out things you don't want.
            JobWorker worker=workers.stream()
                .filter(w->w.getJobType().equalsIgnoreCase("Email task"))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No Worker found for this Job Type"));

            log.info("Handing Job {} Over to {}", job.getId(), job.getClass().getSimpleName());

            // execute the actual work
            worker.execute(job.getPayload());

            // update the job status
            job.setStatus(JobStatus.SUCCESS);

            // updating audit trial
            JobExecution execution=JobExecution.builder()
                .job(job)
                .status(JobStatus.SUCCESS)
                .startedAt(LocalDateTime.now())    
                .finishedAt(LocalDateTime.now())
                .build();

            jobExecutionRepository.save(execution);
        }catch(Exception e){
            log.info("Error in Job: {} Error: {}", job.getId(), e.getMessage());

            // increment retry count
            job.setRetryCount(job.getRetryCount()+1);

            if(job.getRetryCount()>=job.getMaxRetries()){
                // give up
                job.setStatus(JobStatus.FAILED);
            }
            else{
                // retry later
                job.setStatus(JobStatus.PENDING);
            }

            // log the error in audit trail
            JobExecution execution=JobExecution.builder()
                .job(job) //linking the execution to the job
                .status(job.getStatus())
                .errorMessage(e.getMessage())
                .startedAt(LocalDateTime.now())
                .finishedAt(LocalDateTime.now())
                .build();

            jobExecutionRepository.save(execution);
        }
    }
}
