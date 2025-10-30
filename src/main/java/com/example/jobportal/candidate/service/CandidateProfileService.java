package com.example.jobportal.candidate.service;

import com.example.jobportal.candidate.entity.CandidateProfile;
import com.example.jobportal.candidate.repository.CandidateProfileRepository;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileAlreadyCreated;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileNotCreated;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateProfileService {
    @Autowired
    private CandidateProfileRepository candidateProfileRepository;

    @Autowired
    private UserRepository userRepository;

    private String getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email);
        return user.getId();
    }

    public CandidateProfile createCandidateProfile(CandidateProfile candidateProfile) {
        String userId = getCurrentUserId();
        CandidateProfile candidateProfileEntity = candidateProfileRepository.findCandidateProfileByUserId(userId);
        if (candidateProfileEntity != null) throw new CandidateProfileAlreadyCreated("Candidate Profile has benn Already Created");

        candidateProfile.setUserId(userId);
        return candidateProfileRepository.save(candidateProfile);
    }

    public CandidateProfile updateCandidateProfile(CandidateProfile candidateProfile) {
        String userId = getCurrentUserId();
        CandidateProfile candidateProfileEntity = candidateProfileRepository.findCandidateProfileByUserId(userId);
        if (candidateProfileEntity == null) throw new CandidateProfileNotCreated("Candidate profile is not created yet of user : "+ userId);

        if (candidateProfile.getBio() != null) candidateProfileEntity.setBio(candidateProfile.getBio());
        if (candidateProfile.getEducationList() != null) candidateProfileEntity.setEducationList(candidateProfile.getEducationList());
        if (candidateProfile.getSkillList() != null) candidateProfileEntity.setSkillList(candidateProfile.getSkillList());
        if (candidateProfile.getExperienceList() != null) candidateProfileEntity.setExperienceList(candidateProfile.getExperienceList());

        return candidateProfileRepository.save(candidateProfileEntity);
    }

    public CandidateProfile getResume() {
        String userId = getCurrentUserId();
        Optional<CandidateProfile> existingProfileOpt = candidateProfileRepository.findByUserId(userId);
        if (existingProfileOpt.isPresent()) {
            return existingProfileOpt.get();
        }
        throw new CandidateProfileNotCreated("candidate profile is not created");
    }
}
