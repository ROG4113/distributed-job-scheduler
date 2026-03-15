package com.aniket.distributed_job_scheduler.bootstrap;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.aniket.distributed_job_scheduler.entities.Job;
import com.aniket.distributed_job_scheduler.model.JobStatus;
import com.aniket.distributed_job_scheduler.repositories.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobDataLoader implements CommandLineRunner {

    private final JobRepository jobRepository;

    @Override
    public void run(String...args){
        // if(jobRepository.count()==0){
        //     log.info("Seeding initial pending job...");
        //     Job testJob=Job.builder()
        //         .status(JobStatus.PENDING)
        //         .jobType("Email task")
        //         .payload("to:as0009916@gmail.com")
        //         .scheduledTime(LocalDateTime.now().minusMinutes(1))
        //         .maxRetries(3)
        //         .build();
    
        //     jobRepository.save(testJob);
    
        //     log.info("Job created with id: " + testJob.getId());
        // }
    }
}
