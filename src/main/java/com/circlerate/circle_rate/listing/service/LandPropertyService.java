package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotAuthorizedException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.dto.LandPropertyDto;
import com.circlerate.circle_rate.listing.payload.LandPropertyRequest;
import com.circlerate.circle_rate.listing.repository.LandPropertyRepository;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LandPropertyService {
    private final UserRepository userRepository;
    private final LandPropertyRepository landPropertyRepository;

    public ResponseEntity<LandPropertyDto> createProperty(LandPropertyRequest request, String userId) {
        try {
            LandProperty property = (LandProperty) request.toEntity();
            User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(ResponseMessage.USER_NOT_FOUND));
            property.setOwnerId(userId);
            property.setOwnerName(user.getFirstName()+" "+ user.getLastName());
            
            LandProperty savedProperty = landPropertyRepository.save(property);
            LandPropertyDto propertyDto = new LandPropertyDto(savedProperty);

            return ResponseEntity.status(HttpStatus.CREATED).body(propertyDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<LandPropertyDto>> getUserProperties(String userId) {
        try {
            List<LandProperty> userProperties = landPropertyRepository.findByOwnerId(userId);
            List<LandPropertyDto> propertyDtos = userProperties.stream()
                    .map(LandPropertyDto::new)
                    .toList();
            return ResponseEntity.ok(propertyDtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<LandPropertyDto> updateProperty(String userId, String propertyId, LandPropertyRequest request) {
        try {
            var existingProperty = landPropertyRepository.findById(propertyId);
            if (existingProperty.isEmpty()) {
                throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
            }
            
            LandProperty property = existingProperty.get();
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
            
            LandProperty updatedProperty = landPropertyRepository.save(property);
            return ResponseEntity.ok(new LandPropertyDto(updatedProperty));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteProperty(String userId, String propertyId) {
        try {
            var property = landPropertyRepository.findById(propertyId);
            if (property.isEmpty()) {
                throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
            }
            
            if (!property.get().getOwnerId().equals(userId)) {
                throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
            }
            
            landPropertyRepository.deleteById(propertyId);
            return ResponseEntity.ok(ResponseMessage.PROPERTY_DELETED);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
