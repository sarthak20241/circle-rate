package com.circlerate.circle_rate.listing.utils;

import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.LandProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.property.dto.CommercialPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.LandPropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import com.circlerate.circle_rate.listing.model.property.dto.ResidentialPropertyDto;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PropertyUtils {
    public List<PropertyDto> mapPropertyListToPropertyDtoList(List<Property> propertyList){
        List<PropertyDto> propertyDtoList = new ArrayList<>();
        for(Property property: propertyList){
            propertyDtoList.add(mapPropertyToPropertyDto(property));
        }
        return propertyDtoList;
    }

    public PropertyDto mapPropertyToPropertyDto(Property property) {
        PropertyType propertyType = property.getPropertyType();
        
        if (propertyType.equals(PropertyType.RESIDENTIAL)) {
            ResidentialProperty residentialProperty = (ResidentialProperty) property;
            return new ResidentialPropertyDto(residentialProperty);
        } else if (propertyType.equals(PropertyType.COMMERCIAL)) {
            CommercialProperty commercialProperty = (CommercialProperty) property;
            return new CommercialPropertyDto(commercialProperty);
        } else if (propertyType.equals(PropertyType.LAND)) {
            LandProperty landProperty = (LandProperty) property;
            return new LandPropertyDto(landProperty);
        }
        
        return null;
    }





}
