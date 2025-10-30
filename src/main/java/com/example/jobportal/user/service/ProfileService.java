package com.example.jobportal.user.service;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.exeptionHandler.customException.CandidateProfileNotCreated;
import com.example.jobportal.user.dto.ProfileRequest;
import com.example.jobportal.user.entity.Profile;
import com.example.jobportal.user.entity.User;
import com.example.jobportal.user.repository.ProfileRepository;
import com.example.jobportal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private JobPortalUserPrincipal getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof JobPortalUserPrincipal) {
            return (JobPortalUserPrincipal) principal;
        }

        throw new AccessDeniedException("User authentication context is invalid or incomplete.");
    }

    public String getCurrentUserId(){
        return getPrincipal().getUserId();
    }

    @Transactional
    public Profile buildProfile(ProfileRequest profile){
        User user = userRepository.getUserById(getCurrentUserId());
        user.setActive(true);
        userRepository.save(user);

        Profile profileDb = Profile.builder()
                .userId(user.getId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .bio(profile.getBio())
                .email(user.getEmail())
                .location(profile.getLocation())
                .phoneNumber(profile.getPhoneNumber())
                .build();
        return profileRepository.save(profileDb);
    }

    public Profile updateProfile(ProfileRequest profile){
        Profile profileDb = profileRepository.getProfileByUserId(getCurrentUserId());

        if (profile.getFirstName() == null || !profile.getFirstName().isEmpty()) profileDb.setFirstName(profile.getFirstName());
        if (profile.getLastName() == null || !profile.getLastName().isEmpty()) profileDb.setLastName(profile.getLastName());

        if(profile.getBio() == null || !profile.getBio().isEmpty()) profileDb.setBio(profile.getBio());
        if (profile.getLocation() == null || !profile.getLocation().isEmpty()) profileDb.setLocation(profile.getLocation());
        if (profile.getPhoneNumber() == null || !profile.getPhoneNumber().isEmpty()) profileDb.setPhoneNumber(profile.getPhoneNumber());

        return profileRepository.save(profileDb);
    }

    public Profile getProfile(){
        return profileRepository.getProfileByUserId(getCurrentUserId());
    }
}
