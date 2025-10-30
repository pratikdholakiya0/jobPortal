package com.example.jobportal.user.controller;

import com.example.jobportal.user.dto.ProfileRequest;
import com.example.jobportal.user.dto.ResponseMessage;
import com.example.jobportal.user.entity.Profile;
import com.example.jobportal.user.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ProfileService userService;

    @PostMapping("/build-profile")
    public ResponseEntity<ResponseMessage> buildProfile(@RequestBody ProfileRequest profile){
        Profile profileDb = userService.buildProfile(profile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Profile created successfully")
                .email(profileDb.getEmail())
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/get-profile")
    public ResponseEntity<Profile> getProfile(){
        Profile profileDb = userService.getProfile();
        return ResponseEntity.ok(profileDb);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ResponseMessage> updateProfile(@RequestBody ProfileRequest profile){
        Profile profileDb = userService.updateProfile(profile);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .message("Profile updated successfully")
                .email(profileDb.getEmail())
                .build();
        return ResponseEntity.ok(responseMessage);
    }
}
