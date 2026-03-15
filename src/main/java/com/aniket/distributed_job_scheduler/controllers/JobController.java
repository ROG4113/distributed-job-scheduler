package com.aniket.distributed_job_scheduler.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aniket.distributed_job_scheduler.dto.JobRequestDto;
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
}
