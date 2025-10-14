package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.GlobalConstants;
import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyTypeRequiredException;
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
import com.circlerate.circle_rate.listing.repository.ResidentialPropertyRepository;
import com.circlerate.circle_rate.listing.repository.CommercialPropertyRepository;
import com.circlerate.circle_rate.listing.repository.LandPropertyRepository;
import com.circlerate.circle_rate.listing.repository.PropertyQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyListingService {
    private final ResidentialPropertyRepository residentialPropertyRepository;
    private final CommercialPropertyRepository commercialPropertyRepository;
    private final LandPropertyRepository landPropertyRepository;
    private final PropertyQueryRepository propertyQueryRepository;
    private final S3Service s3Service;

    

    public List<PropertyDto> getPropertyList(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Integer limit, Integer offset){
        if (primaryFilterRequest.getPropertyType() == null) {
            throw new PropertyTypeRequiredException(ResponseMessage.PROPERTY_TYPE_REQUIRED);
        }
        
        return getPropertiesByType(primaryFilterRequest, secondaryFilterRequest, limit, offset);
    }
    
    private List<PropertyDto> getPropertiesByType(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Integer limit, Integer offset) {
        switch (primaryFilterRequest.getPropertyType()) {
            case RESIDENTIAL -> {
                List<ResidentialProperty> properties = propertyQueryRepository.findResidentialPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, limit, offset);
                return properties.stream()
                        .map(ResidentialPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
            }
            case COMMERCIAL -> {
                List<CommercialProperty> properties = propertyQueryRepository.findCommercialPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, limit, offset);
                return properties.stream()
                        .map(CommercialPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
            }
            case LAND -> {
                List<LandProperty> properties = propertyQueryRepository.findLandPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, limit, offset);
                return properties.stream()
                        .map(LandPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
            }
            default -> throw new IllegalArgumentException("Invalid property type: " + primaryFilterRequest.getPropertyType());

        }
    }
    
    
    


    public PresignedUrlBatchResponse generatePostPresignedUrls(String propertyId, String propertyType, PresignedUrlRequest request) {
        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException("Property not found with id: " + propertyId + " and type: " + propertyType);
        }

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

    public void updatePropertyMedia(String propertyId, String propertyType, List<String> mediaKeys) {
        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException("Property not found with id: " + propertyId + " and type: " + propertyType);
        }

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
        
        // Save to the appropriate repository based on property type
        savePropertyToCorrectRepository(property, propertyType);
    }
    
    private void savePropertyToCorrectRepository(Property property, String propertyType) {
        switch (propertyType.toUpperCase()) {
            case "RESIDENTIAL" -> residentialPropertyRepository.save((ResidentialProperty) property);
            case "COMMERCIAL" -> commercialPropertyRepository.save((CommercialProperty) property);
            case "LAND" -> landPropertyRepository.save((LandProperty) property);
            default -> throw new IllegalArgumentException("Invalid property type: " + propertyType);
        }
    }
}
