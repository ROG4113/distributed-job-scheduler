package com.aniket.distributed_job_scheduler.workers;

public interface JobWorker {
    
    void execute(String payload);
    
    String getJobType();
}
