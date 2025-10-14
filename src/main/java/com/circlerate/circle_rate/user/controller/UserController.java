package com.circlerate.circle_rate.user.controller;

import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.payload.PresignedUrlResponse;
import com.circlerate.circle_rate.user.model.UserDao;
import com.circlerate.circle_rate.user.payload.InterestRequest;
import com.circlerate.circle_rate.user.payload.ProfilePictureUploadRequest;
import com.circlerate.circle_rate.user.payload.UserProfileUpdateRequest;
import com.circlerate.circle_rate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/interests")
    public ResponseEntity<String> showInterestInProperty(
            @RequestBody @Valid InterestRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return userService.showInterestInProperty(userId, request);
    }

    @GetMapping("/interests")
    public ResponseEntity<List<PropertyDto>> getUserInterestedProperties(Authentication authentication) {
        String userId = authentication.getName();
        return userService.getUserInterestedProperties(userId);
    }

    @DeleteMapping("/interests/{propertyId}")
    public ResponseEntity<String> removeInterestFromProperty(
            @PathVariable String propertyId,
            Authentication authentication) {
        String userId = authentication.getName();
        return userService.removeInterestFromProperty(userId, propertyId);
    }

    
    @GetMapping("/profile")
        public ResponseEntity<UserDao> getUserProfile(Authentication authentication) {
            String userId = authentication.getName();
            return userService.getUserProfile(userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDao> updateUserProfile(
            @RequestBody UserProfileUpdateRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return userService.updateUserProfile(userId, request);
    }

    @PostMapping("/profile/picture/upload-url")
    public ResponseEntity<PresignedUrlResponse> getProfilePictureUploadUrl(
            @RequestBody @Valid ProfilePictureUploadRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return userService.uploadProfilePicture(userId, request);
    }

    @PutMapping("/profile/picture/confirm")
    public ResponseEntity<String> confirmProfilePictureUpload(
            @RequestParam String s3Key,
            Authentication authentication) {
        String userId = authentication.getName();
        return userService.updateProfilePictureKey(userId, s3Key);
    }

    @DeleteMapping("/profile/picture")
    public ResponseEntity<String> deleteProfilePicture(Authentication authentication) {
        String userId = authentication.getName();
        return userService.deleteProfilePicture(userId);
    }
}
