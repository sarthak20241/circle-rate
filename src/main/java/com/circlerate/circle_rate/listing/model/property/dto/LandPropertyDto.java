package com.circlerate.circle_rate.listing.model.property.dto;

import com.circlerate.circle_rate.listing.model.property.LandProperty;

import java.util.ArrayList;
import java.util.List;

public class LandPropertyDto extends PropertyDto{
    private List<LandProperty.Suitability> suitability;

    public LandPropertyDto(LandProperty property) {
        super(property); // copies base Property fields

        this.suitability = property.getSuitability() != null
                ? new ArrayList<>(property.getSuitability()) // defensive copy
                : null;
    }

    public static LandPropertyDto mapLandPropertyToLandPropertyDto(LandProperty landProperty){
        return null;
    }
}
