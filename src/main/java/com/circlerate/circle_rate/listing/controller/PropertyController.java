package com.circlerate.circle_rate.listing.controller;

import com.circlerate.circle_rate.listing.model.Attachment;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.payload.PrimaryFilterRequest;
import com.circlerate.circle_rate.listing.payload.SecondaryFilterRequest;
import com.circlerate.circle_rate.listing.service.PropertyListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/property")
public class PropertyController {
    @Autowired
    PropertyListingService propertyListingService;

    //todo
    @PostMapping("/post")
    public ResponseEntity<PropertyDto> postProperty(Property property){
        return null;
    }

    @GetMapping("/listing/{propertyCount}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByFilter(@ModelAttribute PrimaryFilterRequest primaryFilterRequest, @ModelAttribute SecondaryFilterRequest secondaryFilterRequest, @RequestParam(defaultValue = "500") Integer limit, @RequestParam Integer offset){
        return ResponseEntity.ok(propertyListingService.getPropertyList(primaryFilterRequest,secondaryFilterRequest,limit,offset));
    }

    //todo
    @GetMapping("/get-image/{propertyId}")
    public ResponseEntity<Attachment> getPropertyImages(){
        return null;
    }

}

