package com.circlerate.circle_rate.listing.controller;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.payload.*;
import com.circlerate.circle_rate.listing.service.PropertyListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/property")
@RequiredArgsConstructor
public class PropertyController {
    private final PropertyListingService propertyListingService;

    

    //todo refer property id in user
    //todo refer property id in locality
    //accept property request
    @PostMapping("/post")
    public ResponseEntity<PropertyDto> postProperty(@RequestBody PropertyRequest propertyRequest){
        Property property = propertyRequest.toEntity();
        PropertyDto saved = propertyListingService.saveProperty(property);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/listing/{propertyCount}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByFilter(@ModelAttribute PrimaryFilterRequest primaryFilterRequest, @ModelAttribute SecondaryFilterRequest secondaryFilterRequest, @RequestParam(defaultValue = "500") Integer limit, @RequestParam Integer offset){
        return ResponseEntity.ok(propertyListingService.getPropertyList(primaryFilterRequest,secondaryFilterRequest,limit,offset));
    }

    @PostMapping("/{propertyId}/presigned-urls")
    public ResponseEntity<PresignedUrlBatchResponse> generatePostPresignedUrls(
            @PathVariable String propertyId,
            @RequestBody PresignedUrlRequest request) {
        return ResponseEntity.ok(propertyListingService.generatePostPresignedUrls(propertyId, request));
    }

    @PostMapping("/{propertyId}/media")
    public ResponseEntity<Map<String, String>> updateMedia(
            @PathVariable String propertyId,
            @RequestBody List<String> mediaKeys) {
        propertyListingService.updatePropertyMedia(propertyId, mediaKeys);
        return ResponseEntity.ok(Map.of("message", "Property media updated successfully"));
    }



}

