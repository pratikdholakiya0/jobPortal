package com.example.jobportal.candidate.controller;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.candidate.entity.Resume;
import com.example.jobportal.candidate.service.ResumeService;
import com.example.jobportal.user.dto.ResponseMessage; // Assuming this DTO exists for status messages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidate/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createProfile(@RequestBody Resume resume, @AuthenticationPrincipal JobPortalUserPrincipal principal) {

        if (principal == null) throw new AccessDeniedException("Authentication required to create resume profile.");

        Resume profile = resumeService.createCandidateProfile(resume, principal);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile created successfully.")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<Resume> getResume(@AuthenticationPrincipal JobPortalUserPrincipal principal) {

        if (principal == null) throw new AccessDeniedException("Authentication required to get resume profile.");

        Resume profile = resumeService.getResume(principal);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateProfile(
            @RequestBody Resume resume,
            @AuthenticationPrincipal JobPortalUserPrincipal principal) {

        if (principal == null) throw new AccessDeniedException("Authentication required to update resume profile.");

        Resume updatedProfile = resumeService.updateResume(resume, principal);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile updated successfully.")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
