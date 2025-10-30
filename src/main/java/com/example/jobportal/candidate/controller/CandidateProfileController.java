package com.example.jobportal.candidate.controller;

import com.example.jobportal.candidate.entity.CandidateProfile;
import com.example.jobportal.candidate.service.CandidateProfileService;
import com.example.jobportal.user.dto.ResponseMessage; // Assuming this DTO exists for status messages
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/candidate/resume")
public class CandidateProfileController {

    @Autowired
    private CandidateProfileService candidateProfileService;

    @PostMapping("/create")
    public ResponseEntity<ResponseMessage> createProfile(@RequestBody CandidateProfile candidateProfile) {
        CandidateProfile profile = candidateProfileService.createCandidateProfile(candidateProfile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile created successfully.")
                .build();
        return new ResponseEntity<>(responseMessage, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<CandidateProfile> getResume() {
        CandidateProfile profile = candidateProfileService.getResume();
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseMessage> updateProfile(@RequestBody CandidateProfile candidateProfile) {
        CandidateProfile updatedProfile = candidateProfileService.updateCandidateProfile(candidateProfile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Candidate profile updated successfully.")
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
