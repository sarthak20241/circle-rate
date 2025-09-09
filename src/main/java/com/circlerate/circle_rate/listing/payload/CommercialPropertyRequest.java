package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.CommercialProperty;
import com.circlerate.circle_rate.listing.model.property.Property;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class CommercialPropertyRequest extends PropertyRequest {
    private List<CommercialProperty.Suitability> suitability;
    private int buildingFloor;
    private boolean isPrivateWashroomAvailable;
    private boolean isPublicWashroomAvailable;
    private String amenities;

    @Override
    public Property toEntity() {
        CommercialProperty property = new CommercialProperty();
        copyCommonFields(property);
        property.setSuitability(this.suitability);
        property.setBuildingFloor(this.buildingFloor);
        property.setPrivateWashroomAvailable(this.isPrivateWashroomAvailable);
        property.setPublicWashroomAvailable(this.isPublicWashroomAvailable);
        property.setAmenities(this.amenities);
        return property;
    }
}



