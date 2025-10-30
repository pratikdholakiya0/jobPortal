package com.example.jobportal.candidate.service;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.candidate.entity.Resume;
import com.example.jobportal.candidate.repository.ResumeRepository;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileAlreadyCreated;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileNotCreated;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ResumeService {
    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private UserRepository userRepository;

    private JobPortalUserPrincipal getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof JobPortalUserPrincipal) {
            return (JobPortalUserPrincipal) principal;
        }

        throw new AccessDeniedException("User authentication context is invalid or incomplete.");
    }

    private String getCurrentUserId() {
        return getPrincipal().getUserId();
    }

    public Resume createCandidateProfile(Resume candidateProfile) {
        String userId = getCurrentUserId();
        Resume candidateProfileEntity = resumeRepository.getResumeByUserId(userId);
        if (candidateProfileEntity != null) throw new CandidateProfileAlreadyCreated("Candidate Profile has benn Already Created");

        candidateProfile.setUserId(userId);
        return resumeRepository.save(candidateProfile);
    }

    public Resume updateCandidateProfile(Resume candidateProfile) {
        String userId = getCurrentUserId();
        Resume candidateProfileEntity = resumeRepository.getResumeByUserId(userId);
        if (candidateProfileEntity == null) throw new CandidateProfileNotCreated("Candidate profile is not created yet of user : "+ userId);

        if (candidateProfile.getBio() != null) candidateProfileEntity.setBio(candidateProfile.getBio());
        if (candidateProfile.getEducationList() != null) candidateProfileEntity.setEducationList(candidateProfile.getEducationList());
        if (candidateProfile.getSkillList() != null) candidateProfileEntity.setSkillList(candidateProfile.getSkillList());
        if (candidateProfile.getExperienceList() != null) candidateProfileEntity.setExperienceList(candidateProfile.getExperienceList());

        return resumeRepository.save(candidateProfileEntity);
    }

    public Resume getResume() {
        String userId = getCurrentUserId();
        Optional<Resume> existingProfileOpt = resumeRepository.findByUserId(userId);
        if (existingProfileOpt.isPresent()) {
            return existingProfileOpt.get();
        }
        throw new CandidateProfileNotCreated("candidate profile is not created");
    }
}
