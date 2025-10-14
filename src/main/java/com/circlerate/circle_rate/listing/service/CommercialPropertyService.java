package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotAuthorizedException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.dto.CommercialPropertyDto;
import com.circlerate.circle_rate.listing.payload.CommercialPropertyRequest;
import com.circlerate.circle_rate.listing.repository.CommercialPropertyRepository;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommercialPropertyService {
    private final UserRepository userRepository;
    private final CommercialPropertyRepository commercialPropertyRepository;

    public ResponseEntity<CommercialPropertyDto> createProperty(CommercialPropertyRequest request, String userId) {
        try {
            CommercialProperty property = (CommercialProperty) request.toEntity();
            User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(ResponseMessage.USER_NOT_FOUND));
            property.setOwnerId(userId);
            property.setOwnerName(user.getFirstName()+" "+ user.getLastName());
            
            CommercialProperty savedProperty = commercialPropertyRepository.save(property);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(new CommercialPropertyDto(savedProperty));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<CommercialPropertyDto>> getUserProperties(String userId) {
        try {
            List<CommercialProperty> userProperties = commercialPropertyRepository.findByOwnerId(userId);
            List<CommercialPropertyDto> propertyDtos = userProperties.stream()
                    .map(CommercialPropertyDto::new)
                    .toList();
            return ResponseEntity.ok(propertyDtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<CommercialPropertyDto> updateProperty(String userId, String propertyId, CommercialPropertyRequest request) {
        try {
            var existingProperty = commercialPropertyRepository.findById(propertyId);
            if (existingProperty.isEmpty()) {
                throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
            }
            
            CommercialProperty property = existingProperty.get();
            if (!property.getOwnerId().equals(userId)) {
                throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
            }
            
            // Update property fields
            property.setTitle(request.getTitle());
            property.setAbout(request.getAbout());
            property.setExpectedPriceInRupees(request.getExpectedPriceInRupees());
            property.setAreaInSqFt(request.getAreaInSqFt());
            property.setAddress(request.getAddress());
            property.setSuitability(request.getSuitability());
            property.setBuildingFloor(request.getBuildingFloor());
            property.setPrivateWashroomAvailable(request.isPrivateWashroomAvailable());
            property.setPublicWashroomAvailable(request.isPublicWashroomAvailable());
            property.setAmenities(request.getAmenities());
            
            CommercialProperty updatedProperty = commercialPropertyRepository.save(property);
            return ResponseEntity.ok(new CommercialPropertyDto(updatedProperty));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteProperty(String userId, String propertyId) {
        try {
            var property = commercialPropertyRepository.findById(propertyId);
            if (property.isEmpty()) {
                throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
            }
            
            if (!property.get().getOwnerId().equals(userId)) {
                throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
            }
            
            commercialPropertyRepository.deleteById(propertyId);
            return ResponseEntity.ok(ResponseMessage.PROPERTY_DELETED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
