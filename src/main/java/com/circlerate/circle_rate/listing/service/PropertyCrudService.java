package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotAuthorizedException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.property.dto.CommercialPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.LandPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.ResidentialPropertyDto;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.circlerate.circle_rate.listing.payload.PropertyRequest;
import com.circlerate.circle_rate.listing.repository.CommercialPropertyRepository;
import com.circlerate.circle_rate.listing.repository.LandPropertyRepository;
import com.circlerate.circle_rate.listing.repository.ResidentialPropertyRepository;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.InterestRepository;
import com.circlerate.circle_rate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PropertyCrudService {
    private final ResidentialPropertyRepository residentialPropertyRepository;
    private final CommercialPropertyRepository commercialPropertyRepository;
    private final LandPropertyRepository landPropertyRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final PropertyListingService propertyListingService;

    public ResponseEntity<? extends PropertyDto> createProperty(PropertyRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(ResponseMessage.USER_NOT_FOUND));
        
        Property property = request.toEntity();
        property.setOwnerId(userId);
        property.setOwnerName(user.getFirstName() + " " + user.getLastName());
        
        Property savedProperty = savePropertyToCorrectRepository(property, request.getPropertyType());
        PropertyDto responseDto = createPropertyDto(savedProperty, request.getPropertyType());
        
        log.info("Created property: {} of type: {} for user: {}", savedProperty.getId(), request.getPropertyType(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    public ResponseEntity<List<? extends PropertyDto>> getUserProperties(String userId, PropertyType type) {
        if (type != null) {
            List<? extends PropertyDto> properties = getPropertiesByType(userId, type);
            log.info("Retrieved {} properties of type: {} for user: {}", properties.size(), type, userId);
            return ResponseEntity.ok(properties);
        } else {
            List<PropertyDto> allProperties = new ArrayList<>();
            allProperties.addAll(getPropertiesByType(userId, PropertyType.RESIDENTIAL));
            allProperties.addAll(getPropertiesByType(userId, PropertyType.COMMERCIAL));
            allProperties.addAll(getPropertiesByType(userId, PropertyType.LAND));
            
            allProperties.sort(Comparator.comparing(PropertyDto::getPostedOn).reversed());
            
            log.info("Retrieved {} properties of all types for user: {}", allProperties.size(), userId);
            return ResponseEntity.ok(allProperties);
        }
    }

    public ResponseEntity<? extends PropertyDto> updateProperty(String propertyId, PropertyRequest request, String userId) {
        Property existingProperty = findPropertyByIdAndType(propertyId, request.getPropertyType());
        if (existingProperty == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        
        if (!existingProperty.getOwnerId().equals(userId)) {
            throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
        }
        
        updatePropertyFields(existingProperty, request);
        
        Property updatedProperty = savePropertyToCorrectRepository(existingProperty, request.getPropertyType());
        PropertyDto responseDto = createPropertyDto(updatedProperty, request.getPropertyType());
        
        log.info("Updated property: {} of type: {} for user: {}", propertyId, request.getPropertyType(), userId);
        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<String> deleteProperty(String propertyId, PropertyType type, String userId) {
        Property property = findPropertyByIdAndType(propertyId, type);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        
        if (!property.getOwnerId().equals(userId)) {
            throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
        }
        
        propertyListingService.deletePropertyImages(property);
        log.info("Deleted property images for property: {}", propertyId);
        
        interestRepository.deleteByPropertyId(propertyId);
        log.info("Deleted interests for property: {}", propertyId);
        
        deletePropertyFromCorrectRepository(propertyId, type);
        log.info("Deleted property: {} of type: {} for user: {}", propertyId, type, userId);
        
        return ResponseEntity.ok(ResponseMessage.PROPERTY_DELETED);
    }

    public ResponseEntity<String> updatePropertyAvailability(String propertyId, PropertyType type, boolean isAvailable, String userId) {
        Property property = findPropertyByIdAndType(propertyId, type);
        if (property == null) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }
        
        if (!property.getOwnerId().equals(userId)) {
            throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
        }
        
        property.setAvailable(isAvailable);
        savePropertyToCorrectRepository(property, type);
        
        String status = isAvailable ? "available" : "unavailable";
        log.info("Updated property: {} availability to {} by user: {}", propertyId, status, userId);
        
        return ResponseEntity.ok(ResponseMessage.PROPERTY_AVAILABILITY_UPDATED);
    }

    private List<? extends PropertyDto> getPropertiesByType(String userId, PropertyType type) {
        return switch (type) {
            case RESIDENTIAL -> {
                List<ResidentialProperty> properties = residentialPropertyRepository.findByOwnerId(userId);
                yield properties.stream().map(ResidentialPropertyDto::new).toList();
            }
            case COMMERCIAL -> {
                List<CommercialProperty> properties = commercialPropertyRepository.findByOwnerId(userId);
                yield properties.stream().map(CommercialPropertyDto::new).toList();
            }
            case LAND -> {
                List<LandProperty> properties = landPropertyRepository.findByOwnerId(userId);
                yield properties.stream().map(LandPropertyDto::new).toList();
            }
        };
    }

    private Property findPropertyByIdAndType(String propertyId, PropertyType type) {
        return switch (type) {
            case RESIDENTIAL -> residentialPropertyRepository.findById(propertyId).orElse(null);
            case COMMERCIAL -> commercialPropertyRepository.findById(propertyId).orElse(null);
            case LAND -> landPropertyRepository.findById(propertyId).orElse(null);
        };
    }

    private Property savePropertyToCorrectRepository(Property property, PropertyType type) {
        return switch (type) {
            case RESIDENTIAL -> residentialPropertyRepository.save((ResidentialProperty) property);
            case COMMERCIAL -> commercialPropertyRepository.save((CommercialProperty) property);
            case LAND -> landPropertyRepository.save((LandProperty) property);
        };
    }

    private void deletePropertyFromCorrectRepository(String propertyId, PropertyType type) {
        switch (type) {
            case RESIDENTIAL -> residentialPropertyRepository.deleteById(propertyId);
            case COMMERCIAL -> commercialPropertyRepository.deleteById(propertyId);
            case LAND -> landPropertyRepository.deleteById(propertyId);
        }
    }

    private PropertyDto createPropertyDto(Property property, PropertyType type) {
        return switch (type) {
            case RESIDENTIAL -> new ResidentialPropertyDto((ResidentialProperty) property);
            case COMMERCIAL -> new CommercialPropertyDto((CommercialProperty) property);
            case LAND -> new LandPropertyDto((LandProperty) property);
        };
    }

    private void updatePropertyFields(Property property, PropertyRequest request) {
        // Update common fields
        property.setTitle(request.getTitle());
        property.setAbout(request.getAbout());
        property.setExpectedPriceInRupees(request.getExpectedPriceInRupees());
        property.setAreaInSqFt(request.getAreaInSqFt());
        property.setAddress(request.getAddress());
        property.setAvailable(request.isAvailable());
        
        // Update type-specific fields
        if (property instanceof ResidentialProperty residentialProperty && request instanceof com.circlerate.circle_rate.listing.payload.ResidentialPropertyRequest residentialRequest) {
            updateResidentialFields(residentialProperty, residentialRequest);
        } else if (property instanceof CommercialProperty commercialProperty && request instanceof com.circlerate.circle_rate.listing.payload.CommercialPropertyRequest commercialRequest) {
            updateCommercialFields(commercialProperty, commercialRequest);
        } else if (property instanceof LandProperty landProperty && request instanceof com.circlerate.circle_rate.listing.payload.LandPropertyRequest landRequest) {
            updateLandFields(landProperty, landRequest);
        }
    }

    private void updateResidentialFields(ResidentialProperty property, com.circlerate.circle_rate.listing.payload.ResidentialPropertyRequest request) {
        property.setNoOfRooms(request.getNoOfRooms());
        property.setNoOfWashrooms(request.getNoOfWashrooms());
        property.setNoOfBalconies(request.getNoOfBalconies());
        property.setPropertyFloor(request.getPropertyFloor());
        property.setTotalFloors(request.getTotalFloors());
        property.setAgeOfProperty(request.getAgeOfProperty());
        property.setFacing(request.getFacing());
        property.setReraApproved(request.isReraApproved());
        property.setFurnishingStatus(request.getFurnishingStatus());
        property.setSaleType(request.getSaleType());
        property.setAmenities(request.getAmenities());
    }

    private void updateCommercialFields(CommercialProperty property, com.circlerate.circle_rate.listing.payload.CommercialPropertyRequest request) {
        property.setSuitability(request.getSuitability());
        property.setBuildingFloor(request.getBuildingFloor());
        property.setPrivateWashroomAvailable(request.isPrivateWashroomAvailable());
        property.setPublicWashroomAvailable(request.isPublicWashroomAvailable());
        property.setAmenities(request.getAmenities());
    }

    private void updateLandFields(LandProperty property, com.circlerate.circle_rate.listing.payload.LandPropertyRequest request) {
        property.setSuitability(request.getSuitability());
        property.setAmenities(request.getAmenities());
    }
}
