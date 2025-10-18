package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.common.constants.ResponseMessage;
import com.circlerate.circle_rate.common.exception.custom_exception.PropertyNotFoundException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotAuthorizedException;
import com.circlerate.circle_rate.common.exception.custom_exception.UserNotFoundException;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.property.dto.ResidentialPropertyDto;
import com.circlerate.circle_rate.listing.payload.ResidentialPropertyRequest;
import com.circlerate.circle_rate.listing.repository.ResidentialPropertyRepository;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentialPropertyService {
    private final UserRepository userRepository;
    private final ResidentialPropertyRepository residentialPropertyRepository;

    public ResponseEntity<ResidentialPropertyDto> createProperty(ResidentialPropertyRequest request, String userId) {
        ResidentialProperty property = (ResidentialProperty) request.toEntity();
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(ResponseMessage.USER_NOT_FOUND));
        property.setOwnerId(userId);
        property.setOwnerName(user.getFirstName()+" "+ user.getLastName());
        ResidentialProperty savedProperty = residentialPropertyRepository.save(property);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResidentialPropertyDto(savedProperty));
    }

    public ResponseEntity<List<ResidentialPropertyDto>> getUserProperties(String userId) {
        try {
            List<ResidentialProperty> userProperties = residentialPropertyRepository.findByOwnerId(userId);
            List<ResidentialPropertyDto> propertyDtos = userProperties.stream()
                    .map(ResidentialPropertyDto::new)
                    .toList();
            return ResponseEntity.ok(propertyDtos);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<ResidentialPropertyDto> updateProperty(String userId, String propertyId, ResidentialPropertyRequest request) {
        var existingProperty = residentialPropertyRepository.findById(propertyId);
        if (existingProperty.isEmpty()) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }

        ResidentialProperty property = existingProperty.get();
        if (!property.getOwnerId().equals(userId)) {
            throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
        }

        // Update property fields
        property.setTitle(request.getTitle());
        property.setAbout(request.getAbout());
        property.setExpectedPriceInRupees(request.getExpectedPriceInRupees());
        property.setAreaInSqFt(request.getAreaInSqFt());
        property.setAddress(request.getAddress());
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

        ResidentialProperty updatedProperty = residentialPropertyRepository.save(property);
        return ResponseEntity.ok(new ResidentialPropertyDto(updatedProperty));
    }

    public ResponseEntity<String> deleteProperty(String userId, String propertyId) {
        var property = residentialPropertyRepository.findById(propertyId);
        if (property.isEmpty()) {
            throw new PropertyNotFoundException(ResponseMessage.PROPERTY_NOT_FOUND);
        }

        if (!property.get().getOwnerId().equals(userId)) {
            throw new UserNotAuthorizedException(ResponseMessage.USER_NOT_AUTHORIZED);
        }

        residentialPropertyRepository.deleteById(propertyId);
        return ResponseEntity.ok(ResponseMessage.PROPERTY_DELETED);
    }
}
