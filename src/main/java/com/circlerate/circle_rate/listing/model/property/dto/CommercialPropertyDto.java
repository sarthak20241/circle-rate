package com.circlerate.circle_rate.listing.model.property.dto;

import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@NoArgsConstructor
public class CommercialPropertyDto extends PropertyDto {
    private List<CommercialProperty.Suitability> suitability;
    private int buildingFloor;
    private boolean isPrivateWashroomAvailable;
    private boolean isPublicWashroomAvailable;
    private String amenities;

    public CommercialPropertyDto(CommercialProperty property) {
        super(property); // copies base Property fields

        this.suitability = property.getSuitability() != null
                ? new ArrayList<>(property.getSuitability()) // defensive copy
                : null;

        this.buildingFloor = property.getBuildingFloor();
        this.isPrivateWashroomAvailable = property.isPrivateWashroomAvailable();
        this.isPublicWashroomAvailable = property.isPublicWashroomAvailable();
        this.amenities = property.getAmenities();
    }

    public static CommercialPropertyDto mapCommercialPropertyToCommercialPropertyDto(CommercialProperty commercialProperty){
        return new CommercialPropertyDto(commercialProperty);
    }

}

