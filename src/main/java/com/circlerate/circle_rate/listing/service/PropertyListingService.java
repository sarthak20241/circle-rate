package com.circlerate.circle_rate.listing.service;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.payload.PrimaryFilterRequest;
import com.circlerate.circle_rate.listing.payload.SecondaryFilterRequest;
import com.circlerate.circle_rate.listing.repository.PropertyRepository;
import com.circlerate.circle_rate.listing.utils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyListingService {
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    PropertyUtils propertyUtils;
    public List<PropertyDto> getPropertyList(PrimaryFilterRequest primaryFilterRequest, SecondaryFilterRequest secondaryFilterRequest, Integer limit, Integer offset){
        List<Property> propertyList =  propertyRepository.getPropertiesByFilters(primaryFilterRequest,secondaryFilterRequest,limit,offset);
        return propertyUtils.mapPropertyListToPropertyDtoList(propertyList);
    }
}
