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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PropertyUtils {
    public List<PropertyDto> mapPropertyListToPropertyDtoList(List<Property> propertyList){
        PropertyType propertyType;
        if(!propertyList.isEmpty()){
            propertyType = propertyList.getFirst().getPropertyType();
        }
        else {
            return null;
        }
        List<PropertyDto> propertyDtoList = new ArrayList<>();
        if(propertyType.equals(PropertyType.RESIDENTIAL)){
            for(Property property: propertyList){
                ResidentialProperty residentialProperty = (ResidentialProperty) property;
                propertyDtoList.add(ResidentialPropertyDto.mapResidentialPropertyToResidentialPropertyDto(residentialProperty));
            }
        }
        else if(propertyType.equals(PropertyType.COMMERCIAL)){
            for(Property property: propertyList){
                CommercialProperty commercialProperty = (CommercialProperty) property;
                propertyDtoList.add(CommercialPropertyDto.mapCommercialPropertyToCommercialPropertyDto(commercialProperty));
            }
        }
        else if(propertyType.equals(PropertyType.LAND)){
            for(Property property: propertyList){
                LandProperty landProperty = (LandProperty) property;
                propertyDtoList.add(LandPropertyDto.mapLandPropertyToLandPropertyDto(landProperty));
            }
        }
        return propertyDtoList;
    }





}
