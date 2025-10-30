package com.example.jobportal.application.controller;

import com.example.jobportal.application.dto.ApplicationStatusUpdate;
import com.example.jobportal.application.dto.ApplicationSubmissionRequest;
import com.example.jobportal.application.entity.Application;
import com.example.jobportal.application.entity.ApplicationActivity;
import com.example.jobportal.application.service.ApplicationService;
import com.example.jobportal.user.dto.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping("/submit")
    public ResponseEntity<ResponseMessage> submitApplication(
            @RequestBody ApplicationSubmissionRequest request) {

        applicationService.applyToJob(request);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Application submitted successfully.")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<Application>> getCandidateApplications() {
        List<Application> applications = applicationService.getApplicationsByCandidate();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/by-employer")
    public ResponseEntity<List<Application>> getApplicationsForEmployer() {
        List<Application> applications = applicationService.getApplicationsByEmployer();
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/history/{applicationId}")
    public ResponseEntity<List<ApplicationActivity>> getApplicationHistory(@PathVariable String applicationId) {
        List<ApplicationActivity> history = applicationService.getApplicationHistory(applicationId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/status/update")
    public ResponseEntity<ResponseMessage> updateApplicationStatus(
            @RequestBody ApplicationStatusUpdate request) {

        applicationService.updateApplicationStatus(request);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Application status updated successfully.")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
