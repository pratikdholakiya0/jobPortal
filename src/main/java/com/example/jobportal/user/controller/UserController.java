package com.example.jobportal.user.controller;

import com.example.jobportal.auth.service.JobPortalUserPrincipal;
import com.example.jobportal.user.dto.ProfileRequest;
import com.example.jobportal.user.dto.ResponseMessage;
import com.example.jobportal.user.entity.Profile;
import com.example.jobportal.user.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ProfileService userService;

    @PostMapping("/build-profile")
    public ResponseEntity<ResponseMessage> buildProfile(@RequestBody ProfileRequest profile,
                                                        @AuthenticationPrincipal JobPortalUserPrincipal principal){

        if (principal == null) throw new AccessDeniedException("Authentication required to create profile.");

        Profile profileDb = userService.buildProfile(profile, principal);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Profile created successfully")
                .email(profileDb.getEmail())
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/get-profile")
    public ResponseEntity<Profile> getProfile(@AuthenticationPrincipal JobPortalUserPrincipal principal){
        Profile profileDb = userService.getProfile(principal);
        return ResponseEntity.ok(profileDb);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ResponseMessage> updateProfile(
            @RequestBody ProfileRequest profile,
            @AuthenticationPrincipal JobPortalUserPrincipal principal){
        Profile profileDb = userService.updateProfile(profile,  principal);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Profile updated successfully")
                .email(profileDb.getEmail())
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
