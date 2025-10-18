package com.circlerate.circle_rate.listing.controller;

import com.circlerate.circle_rate.listing.model.property.dto.*;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.circlerate.circle_rate.listing.payload.*;
import com.circlerate.circle_rate.listing.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//TODO: When Locality service implemented, update the post property apis
@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyListingService propertyListingService;
    private final PropertyCrudService propertyCrudService;

    @GetMapping("/listing")
    public ResponseEntity<PropertyListingResponse> getPropertiesByFilter(
            @ModelAttribute PrimaryFilterRequest primaryFilterRequest, 
            @ModelAttribute SecondaryFilterRequest secondaryFilterRequest, 
            @RequestParam(defaultValue = "0") Integer page, 
            @RequestParam(defaultValue = "500") Integer limit){
        return ResponseEntity.ok(propertyListingService.getPropertyList(primaryFilterRequest, secondaryFilterRequest, page, limit));
    }

    @PostMapping("/{propertyId}/presigned-urls")
    public ResponseEntity<PresignedUrlBatchResponse> generatePutPresignedUrls(
            @PathVariable String propertyId,
            @RequestParam String propertyType,
            @RequestBody PresignedUrlRequest request) {
        return ResponseEntity.ok(propertyListingService.generatePropertyImagesPutPresignedUrls(propertyId, propertyType, request));
    }

    @PostMapping("/{propertyId}/media")
    public ResponseEntity<Map<String, String>> updateMedia(
            @PathVariable String propertyId,
            @RequestParam String propertyType,
            @RequestBody List<String> mediaKeys) {
        propertyListingService.updatePropertyMedia(propertyId, propertyType, mediaKeys);
        return ResponseEntity.ok(Map.of("message", "Property media updated successfully"));
    }

    @PostMapping
    public ResponseEntity<? extends PropertyDto> createProperty(
            @RequestBody @Valid PropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyCrudService.createProperty(request, userId);
    }

    @GetMapping("/my-properties")
    public ResponseEntity<List<? extends PropertyDto>> getMyProperties(
            @RequestParam(required = false) PropertyType propertyType,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyCrudService.getUserProperties(userId, propertyType);
    }

    @PutMapping("/{propertyId}")
    public ResponseEntity<? extends PropertyDto> updateProperty(
            @PathVariable String propertyId,
            @RequestBody @Valid PropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyCrudService.updateProperty(propertyId, request, userId);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<String> deleteProperty(
            @PathVariable String propertyId,
            @RequestParam PropertyType propertyType,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyCrudService.deleteProperty(propertyId, propertyType, userId);
    }

    @PatchMapping("/{propertyId}/availability")
    public ResponseEntity<String> updatePropertyAvailability(
            @PathVariable String propertyId,
            @RequestParam PropertyType propertyType,
            @RequestParam boolean isAvailable,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyCrudService.updatePropertyAvailability(propertyId, propertyType, isAvailable, userId);
    }

    @GetMapping("/images/{propertyId}")
    public ResponseEntity<List<PresignedUrlResponse>> getPropertyImages(
            @PathVariable String propertyId,
            @RequestParam String propertyType){
        return propertyListingService.getPropertyImagesURL(propertyId,propertyType);
    }

    @DeleteMapping("/images/{propertyId}")
    public ResponseEntity<String> deletePropertyImage(
            @PathVariable String propertyId,
            @RequestParam String propertyType,
            @RequestBody @Valid DeleteImageRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyListingService.deletePropertyImage(propertyId, propertyType, request.getS3Keys(), userId);
    }

    @GetMapping("/{propertyId}/interested-users")
    public ResponseEntity<InterestedUsersResponse> getInterestedUsers(
            @PathVariable String propertyId,
            @RequestParam String propertyType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyListingService.getInterestedUsers(propertyId, propertyType, userId, page, limit);
    }

}

