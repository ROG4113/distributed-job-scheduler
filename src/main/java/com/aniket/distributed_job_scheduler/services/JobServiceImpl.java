package com.aniket.distributed_job_scheduler.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aniket.distributed_job_scheduler.dto.JobRequestDto;
import com.aniket.distributed_job_scheduler.entities.Job;
import com.aniket.distributed_job_scheduler.entities.JobExecution;
import com.aniket.distributed_job_scheduler.exceptions.ResourceNotFoundException;
import com.aniket.distributed_job_scheduler.model.JobStatus;
import com.aniket.distributed_job_scheduler.repositories.JobExecutionRepository;
import com.aniket.distributed_job_scheduler.repositories.JobRepository;
import com.aniket.distributed_job_scheduler.workers.JobWorker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
// @RequiredArgsConstructor // Using manual constructor so thhat we can initialise the map
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
    // private final List<JobWorker> workers; //adding Map for O(1) lookup as it will be faster if we have too many workers
    private final Map<String, JobWorker> workerMap=new HashMap<>();

    // Manual constructor to initialise map from workers list
    public JobServiceImpl(JobRepository jobRepository, JobExecutionRepository jobExecutionRepository, List<JobWorker> workers){
        this.jobRepository=jobRepository;
        this.jobExecutionRepository=jobExecutionRepository;

        // building map from workers list
        workers.forEach((job)->{
            workerMap.put(job.getJobType().toLowerCase(), job);
        });

        log.info("Initialised JobService with {} workers", workerMap.size());
    }
    
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
            // JobWorker worker=workers.stream()
            //     .filter(w->w.getJobType().equalsIgnoreCase(job.getJobType()))
            //     .findFirst()
            //     .orElseThrow(()->new RuntimeException("No Worker found for this Job Type"));

            // NOW: Using map instead of workers list to avoid O(n) lookup
            JobWorker worker=workerMap.get(job.getJobType().toLowerCase());

            // if map returns null
            if(worker==null){
                throw new RuntimeException("No Worker found for this Job Type");
            }

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
                // retry later after one minute
                job.setStatus(JobStatus.PENDING);
                job.setScheduledTime(LocalDateTime.now().plusMinutes(1));
                log.info("Job {} rescheduled for 1 minute from now", job.getId());
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

    @Override
    @Transactional
    public UUID createJob(JobRequestDto jobRequestDto) {
        Job newJob=Job.builder()
            .payload(jobRequestDto.getPayload())
            .jobType(jobRequestDto.getJobType())
            .status(JobStatus.PENDING)
            .scheduledTime(LocalDateTime.now().plusMinutes(jobRequestDto.getScheduledInMinutes()==null?0:jobRequestDto.getScheduledInMinutes()))
            .maxRetries(3)
            .build();
        
        return jobRepository.save(newJob).getId();
    }

    @Override
    public Job getJobById(UUID id) {
        return jobRepository.findById(id)
                    .orElseThrow(()->new ResourceNotFoundException("Job not found with id: " + id));
    }

    @Override
    @Transactional
    public void resetJob(UUID id) {
        Job job=getJobById(id);

        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);

        jobRepository.save(job);

        log.info("Job {} has been manually reset to PENDING", id);
    }
}
