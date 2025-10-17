package com.circlerate.circle_rate.listing.controller;

import com.circlerate.circle_rate.listing.model.property.dto.*;
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
    private final ResidentialPropertyService residentialPropertyService;
    private final CommercialPropertyService commercialPropertyService;
    private final LandPropertyService landPropertyService;

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


    @PostMapping("/residential")
    public ResponseEntity<ResidentialPropertyDto> createResidentialProperty(
            @RequestBody @Valid ResidentialPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return residentialPropertyService.createProperty(request, userId);
    }

    @GetMapping("/residential/my-properties")
    public ResponseEntity<List<ResidentialPropertyDto>> getMyResidentialProperties(
            Authentication authentication) {
        String userId = authentication.getName();
        return residentialPropertyService.getUserProperties(userId);
    }

    @PutMapping("/residential/{propertyId}")
    public ResponseEntity<ResidentialPropertyDto> updateResidentialProperty(
            @PathVariable String propertyId,
            @RequestBody @Valid ResidentialPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return residentialPropertyService.updateProperty(userId, propertyId, request);
    }

    @DeleteMapping("/residential/{propertyId}")
    public ResponseEntity<String> deleteResidentialProperty(
            @PathVariable String propertyId,
            Authentication authentication) {
        String userId = authentication.getName();
        return residentialPropertyService.deleteProperty(userId, propertyId);
    }

    @PostMapping("/commercial")
    public ResponseEntity<CommercialPropertyDto> createCommercialProperty(
            @RequestBody @Valid CommercialPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return commercialPropertyService.createProperty(request, userId);
    }

    @GetMapping("/commercial/my-properties")
    public ResponseEntity<List<CommercialPropertyDto>> getMyCommercialProperties(
            Authentication authentication) {
        String userId = authentication.getName();
        return commercialPropertyService.getUserProperties(userId);
    }

    @PutMapping("/commercial/{propertyId}")
    public ResponseEntity<CommercialPropertyDto> updateCommercialProperty(
            @PathVariable String propertyId,
            @RequestBody @Valid CommercialPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return commercialPropertyService.updateProperty(userId, propertyId, request);
    }

    @DeleteMapping("/commercial/{propertyId}")
    public ResponseEntity<String> deleteCommercialProperty(
            @PathVariable String propertyId,
            Authentication authentication) {
        String userId = authentication.getName();
        return commercialPropertyService.deleteProperty(userId, propertyId);
    }

    // Land Property Endpoints
    @PostMapping("/land")
    public ResponseEntity<LandPropertyDto> createLandProperty(
            @RequestBody @Valid LandPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return landPropertyService.createProperty(request, userId);
    }

    @GetMapping("/land/my-properties")
    public ResponseEntity<List<LandPropertyDto>> getMyLandProperties(
            Authentication authentication) {
        String userId = authentication.getName();
        return landPropertyService.getUserProperties(userId);
    }

    @PutMapping("/land/{propertyId}")
    public ResponseEntity<LandPropertyDto> updateLandProperty(
            @PathVariable String propertyId,
            @RequestBody @Valid LandPropertyRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return landPropertyService.updateProperty(userId, propertyId, request);
    }

    @DeleteMapping("/land/{propertyId}")
    public ResponseEntity<String> deleteLandProperty(
            @PathVariable String propertyId,
            Authentication authentication) {
        String userId = authentication.getName();
        return landPropertyService.deleteProperty(userId, propertyId);
    }

    @GetMapping("/images/{propertyId}")
    public ResponseEntity<List<PresignedUrlResponse>> getPropertyImages(
            @PathVariable String propertyId,
            @RequestParam String propertyType){
        return propertyListingService.getPropertyImagesURL(propertyId,propertyType);
    }

    @DeleteMapping("/image/{propertyId}")
    public ResponseEntity<String> deletePropertyImage(
            @PathVariable String propertyId,
            @RequestParam String propertyType,
            @RequestBody @Valid DeleteImageRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        return propertyListingService.deletePropertyImage(propertyId, propertyType, request.getS3Key(), userId);
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

