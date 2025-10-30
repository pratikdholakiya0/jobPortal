package com.example.jobportal.job.controller;

import com.example.jobportal.job.dto.JobPostingDto;
import com.example.jobportal.job.entity.JobPosting;
import com.example.jobportal.job.service.JobPostingService;
import com.example.jobportal.user.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobPostingController {

    @Autowired
    private JobPostingService jobPostingService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createJobPosting(@RequestBody JobPostingDto jobPostingDto) {
        JobPosting jobPosting = jobPostingService.createJobPosting(jobPostingDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Job post created")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PutMapping("update/{jobId}")
    public ResponseEntity<ResponseMessage> updateJobPosting(
            @PathVariable String jobId,
            @RequestBody JobPostingDto jobPostingUpdates) {

        JobPosting updatedJob = jobPostingService.updateJobPosting(jobId, jobPostingUpdates);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Job post updated successfully")
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/getAllActive")
    public ResponseEntity<List<JobPosting>> getAllActiveJobs() {
        List<JobPosting> jobs = jobPostingService.findAllActiveJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobPosting> getJobById(@PathVariable String jobId) {
        JobPosting job = jobPostingService.findJobPostById(jobId);
        return ResponseEntity.ok(job);
    }
}
