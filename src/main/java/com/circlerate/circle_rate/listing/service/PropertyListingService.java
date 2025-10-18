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
import com.circlerate.circle_rate.user.model.Interest;
import com.circlerate.circle_rate.user.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyListingService {
    private final ResidentialPropertyRepository residentialPropertyRepository;
    private final CommercialPropertyRepository commercialPropertyRepository;
    private final LandPropertyRepository landPropertyRepository;
    private final PropertyQueryRepository propertyQueryRepository;
    private final S3Service s3Service;
    private final InterestRepository interestRepository;

    

    public PropertyListingResponse getPropertyList(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Integer page, Integer limit){
        if (primaryFilterRequest.getPropertyType() == null) {
            throw new PropertyTypeRequiredException(ResponseMessage.PROPERTY_TYPE_REQUIRED);
        }
        Pageable pageable = PageRequest.of(page, limit, Sort.by("propertyScore").descending());
        return getPropertiesByType(primaryFilterRequest, secondaryFilterRequest, pageable);
    }
    
    private PropertyListingResponse getPropertiesByType(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Pageable pageable) {
        switch (primaryFilterRequest.getPropertyType()) {
            case RESIDENTIAL -> {
                Page<ResidentialProperty> propertiesPage = propertyQueryRepository.findResidentialPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, pageable);
                List<PropertyDto> propertyDtoList = propertiesPage.getContent().stream()
                        .map(ResidentialPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
                return new PropertyListingResponse(
                    propertyDtoList,
                    propertiesPage.getTotalElements(),
                    propertiesPage.getNumber(),
                    propertiesPage.getTotalPages(),
                    propertiesPage.hasNext(),
                    propertiesPage.hasPrevious()
                );
            }
            case COMMERCIAL -> {
                Page<CommercialProperty> propertiesPage = propertyQueryRepository.findCommercialPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, pageable);
                List<PropertyDto> propertyDtoList = propertiesPage.getContent().stream()
                        .map(CommercialPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
                return new PropertyListingResponse(
                    propertyDtoList,
                    propertiesPage.getTotalElements(),
                    propertiesPage.getNumber(),
                    propertiesPage.getTotalPages(),
                    propertiesPage.hasNext(),
                    propertiesPage.hasPrevious()
                );
            }
            case LAND -> {
                Page<LandProperty> propertiesPage = propertyQueryRepository.findLandPropertiesByFilters(
                    primaryFilterRequest, secondaryFilterRequest, pageable);
                List<PropertyDto> propertyDtoList = propertiesPage.getContent().stream()
                        .map(LandPropertyDto::new)
                        .map(dto -> (PropertyDto) dto)
                        .toList();
                return new PropertyListingResponse(
                    propertyDtoList,
                    propertiesPage.getTotalElements(),
                    propertiesPage.getNumber(),
                    propertiesPage.getTotalPages(),
                    propertiesPage.hasNext(),
                    propertiesPage.hasPrevious()
                );
            }
            default -> throw new IllegalArgumentException("Invalid property type: " + primaryFilterRequest.getPropertyType());
        }
    }
    
    
    


    public PresignedUrlBatchResponse generatePropertyImagesPutPresignedUrls(String propertyId, String propertyType, PresignedUrlRequest request) {
        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
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
                String url = s3Service.generatePutPresignedUrl(key, file.getMimeType());
                presignedUrls.add(new PresignedUrlResponse(url, key));
                currentImages++;
            } else if (category == FileCategory.VIDEO) {
                if (currentVideos >= GlobalConstants.MAX_PROPERTY_VIDEOS) {
                    errors.add("Max videos reached for property " + propertyId);
                    continue;
                }
                String key = generateS3Key(propertyId, category, extension);
                String url = s3Service.generatePutPresignedUrl(key, file.getMimeType());
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
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }

        if (mediaKeys != null && !mediaKeys.isEmpty()) {
            List<String> propertyMediaKeys = property.getMediaKeys();
            if(propertyMediaKeys == null) {
                propertyMediaKeys = new ArrayList<>();
            }
            int newImages = 0;
            int newVideos = 0;

            for (String key : mediaKeys) {
                if(propertyMediaKeys.contains(key)) {
                    log.info("property media with s3 key {} already added", key);
                    continue;
                }
                propertyMediaKeys.add(key);
                if (key.startsWith("property/images/")) {
                    newImages++;
                } else if (key.startsWith("property/videos/")) {
                    newVideos++;
                }
            }

            property.setNoOfImages(property.getNoOfImages() + newImages);
            property.setNoOfVideos(property.getNoOfVideos() + newVideos);
            property.setMediaKeys(propertyMediaKeys);
        }

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

    public ResponseEntity<List<PresignedUrlResponse>> getPropertyImagesURL(String propertyId, String propertyType) {
        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        List<String> mediaKeys = property.getMediaKeys();
        if (mediaKeys == null || mediaKeys.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        List<PresignedUrlResponse> presignedUrls = new ArrayList<>();
        for (String key : mediaKeys) {
            presignedUrls.add(new PresignedUrlResponse(s3Service.generateGetPresignedUrl(key), key));
        }
        return ResponseEntity.ok(presignedUrls);
    }

    public ResponseEntity<String> deletePropertyImage(String propertyId, String propertyType, List<String> s3Keys, String userId) {
        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        
        if (!property.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseMessage.NOT_AUTHORIZED_TO_DELETE_IMAGES);
        }
        if(property.getMediaKeys() == null || property.getMediaKeys().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseMessage.PROPERTY_HAS_NO_MEDIA);
        }
        int deletedImages = 0;
        int deletedVideos = 0;
        for (String s3Key : s3Keys) {
            if (property.getMediaKeys().contains(s3Key)) {
                s3Service.deleteObject(s3Key);
                property.getMediaKeys().remove(s3Key);
                if (s3Key.startsWith("property/images/")) {
                    deletedImages++;
                } else if (s3Key.startsWith("property/videos/")) {
                    deletedVideos++;
                }
            }
            else {
                log.error("Image not found in property: {}", s3Key);
            }
        }
        property.setNoOfImages(property.getNoOfImages() - deletedImages);
        property.setNoOfVideos(property.getNoOfVideos() - deletedVideos);
        if (property.getMediaKeys().isEmpty()) {
            property.setMediaKeys(null);
        }
        savePropertyToCorrectRepository(property, propertyType);
        log.info("{} Media Files deleted successfully:  from property: {}", deletedImages + deletedVideos , propertyId);
        return ResponseEntity.ok(ResponseMessage.IMAGE_DELETED_SUCCESSFULLY);
    }

    public ResponseEntity<InterestedUsersResponse> getInterestedUsers(
            String propertyId, 
            String propertyType, 
            String userId, 
            int page, 
            int size) {

        Property property = propertyQueryRepository.findPropertyByIdAndType(propertyId, propertyType);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        
        if (!property.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Interest> interestsPage = interestRepository.findByPropertyId(propertyId, pageable);

        InterestedUsersResponse response = new InterestedUsersResponse(
            interestsPage.getContent(),
            interestsPage.getTotalElements(),
            interestsPage.getNumber(),
            interestsPage.getTotalPages(),
            interestsPage.hasNext(),
            interestsPage.hasPrevious()
        );
        
        log.info("Retrieved {} interested users for property: {}", interestsPage.getNumberOfElements(), propertyId);
        return ResponseEntity.ok(response);
    }

    public void deletePropertyImages(Property property) {
        List<String> mediaKeys = property.getMediaKeys();
        if (mediaKeys != null) {
            for (String mediaKey : mediaKeys) {
                s3Service.deleteObject(mediaKey);
            }
        }
    }
}
