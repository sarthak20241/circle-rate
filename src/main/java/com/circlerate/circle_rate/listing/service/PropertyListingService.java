package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.GlobalConstants;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyAlreadyExists;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFound;
import com.circlerate.circle_rate.common.utils.S3Service;
import com.circlerate.circle_rate.listing.model.FileCategory;
import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.property.dto.CommercialPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.LandPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.ResidentialPropertyDto;
import com.circlerate.circle_rate.listing.payload.*;
import com.circlerate.circle_rate.listing.repository.PropertyRepository;
import com.circlerate.circle_rate.listing.utils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PropertyListingService {
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    PropertyUtils propertyUtils;
    @Autowired
    S3Service s3Service;

    public List<PropertyDto> getPropertyList(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Integer limit, Integer offset){
        List<Property> propertyList =  propertyRepository.getPropertiesByFilters(primaryFilterRequest,secondaryFilterRequest,limit,offset);
        return propertyUtils.mapPropertyListToPropertyDtoList(propertyList);
    }

    public PropertyDto saveProperty(Property property) {
        property.setPostedOn(new Date());
        Property savedProperty = propertyRepository.save(property);
        PropertyDto propertyDto;
        switch (property.getPropertyType()) {
            case RESIDENTIAL -> {
                propertyDto = new ResidentialPropertyDto((ResidentialProperty) savedProperty);
            }
            case COMMERCIAL -> {
                propertyDto = new CommercialPropertyDto((CommercialProperty) savedProperty);
            }
            case LAND -> {
                propertyDto = new LandPropertyDto((LandProperty) savedProperty);
            }
            default -> throw new IllegalArgumentException("Unsupported property type: " + property.getPropertyType());
        }
        return propertyDto;
    }

    public PresignedUrlBatchResponse generatePostPresignedUrls(String propertyId, PresignedUrlRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFound("Property not found: " + propertyId));

        int currentImages = property.getNoOfImages();
        int currentVideos = property.getNoOfVideos();


        List<PresignedUrlResponse> presignedUrls = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (FileUploadRequest file : request.getFiles()) {
            FileCategory category = file.getFileCategory();
            String extension = getExtension(file.getMimeType());

            if (category == FileCategory.IMAGE) {
                if (currentImages >= GlobalConstants.MAX_PROPERTY_PHOTOS) {
                    errors.add("Max images reached for property " + propertyId);
                    continue;
                }
                String key = generateS3Key(propertyId, category, extension);
                String url = s3Service.generatePresignedUrl(key, file.getMimeType());
                presignedUrls.add(new PresignedUrlResponse(url, key));
                currentImages++;
            } else if (category == FileCategory.VIDEO) {
                if (currentVideos >= GlobalConstants.MAX_PROPERTY_VIDEOS) {
                    errors.add("Max videos reached for property " + propertyId);
                    continue;
                }
                String key = generateS3Key(propertyId, category, extension);
                String url = s3Service.generatePresignedUrl(key, file.getMimeType());
                presignedUrls.add(new PresignedUrlResponse(url, key));
                currentVideos++;
            }
        }

        return new PresignedUrlBatchResponse(presignedUrls, errors);
    }

    private String generateS3Key(String propertyId, FileCategory category, String extension) {
        String randomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        long timestamp = System.currentTimeMillis();

        String folder;
        switch (category) {
            case IMAGE -> folder = "images";
            case VIDEO -> folder = "videos";
            case DOCUMENT -> folder = "documents";
            default -> throw new IllegalArgumentException("Unsupported file category: " + category);
        }

        return String.format(
                "property/%s/propertyId=%s/%s_%d%s",
                folder, propertyId, randomId, timestamp, extension
        );
    }

    private String getExtension(String mimeType) {
        if (mimeType == null) return "";
        return switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "video/mp4" -> ".mp4";
            case "application/pdf" -> ".pdf";
            default -> "";
        };
    }

    public void updatePropertyMedia(String propertyId, List<String> mediaKeys) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFound("Property not found: " + propertyId));

        if (mediaKeys != null && !mediaKeys.isEmpty()) {
            if (property.getMediaKeys() == null) {
                property.setMediaKeys(new ArrayList<>());
            }

            property.getMediaKeys().addAll(mediaKeys);

            int newImages = 0;
            int newVideos = 0;

            for (String key : mediaKeys) {
                if (key.startsWith("property/images/")) {
                    newImages++;
                } else if (key.startsWith("property/videos/")) {
                    newVideos++;
                }
            }

            property.setNoOfImages(property.getNoOfImages() + newImages);
            property.setNoOfVideos(property.getNoOfVideos() + newVideos);
        }
        propertyRepository.save(property);
    }
}
