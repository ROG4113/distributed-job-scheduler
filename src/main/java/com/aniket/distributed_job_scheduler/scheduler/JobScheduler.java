package com.aniket.distributed_job_scheduler.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aniket.distributed_job_scheduler.services.JobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JobScheduler {
    
    private final JobService jobService;

    @Scheduled(fixedDelay = 5000)
    public void runJobPolling(){
        log.info("Starting polling cycle...");
        try{
            jobService.claimJobs();
        }
        catch(Exception e){
            log.error("Critical error during polling heartbeat, shutting down...", e);
        }
    }
}
