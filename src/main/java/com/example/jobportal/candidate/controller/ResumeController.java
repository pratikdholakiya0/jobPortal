package com.example.jobportal.candidate.controller;

import com.example.jobportal.candidate.entity.Resume;
import com.example.jobportal.candidate.service.ResumeService;
import com.example.jobportal.user.dto.ResponseMessage; // Assuming this DTO exists for status messages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidate/resume")
public class ResumeController {

    @Autowired
    private ResumeService candidateProfileService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createProfile(@RequestBody Resume candidateProfile) {
        Resume profile = candidateProfileService.createCandidateProfile(candidateProfile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile created successfully.")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<Resume> getResume() {
        Resume profile = candidateProfileService.getResume();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateProfile(@RequestBody Resume candidateProfile) {
        Resume updatedProfile = candidateProfileService.updateCandidateProfile(candidateProfile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile updated successfully.")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
