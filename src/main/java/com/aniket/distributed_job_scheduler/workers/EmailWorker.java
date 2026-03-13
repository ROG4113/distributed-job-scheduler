package com.aniket.distributed_job_scheduler.workers;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmailWorker implements JobWorker {

    @Override
    public void execute(String payload) {
        log.info("Executing Email Job With Payload: " + payload);
        throw new RuntimeException("SMTP Server Down!");
    }

    @Override
    public String getJobType() {
        return "Email task";
    }

}
