package com.aniket.distributed_job_scheduler.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aniket.distributed_job_scheduler.dto.JobRequestDto;
import com.aniket.distributed_job_scheduler.entities.Job;
import com.aniket.distributed_job_scheduler.services.JobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping
    public ResponseEntity<UUID> submitJob(@RequestBody JobRequestDto jobRequestDto){
        UUID jobId=jobService.createJob(jobRequestDto);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable(name = "id") UUID id){
        Job job=jobService.getJobById(id);

        return ResponseEntity.ok(job);
    }

    @PostMapping("/{id}/reset")
    public ResponseEntity<Void> resetJob(@PathVariable UUID id){
        jobService.resetJob(id);

        return ResponseEntity.noContent().build();
    }
}
