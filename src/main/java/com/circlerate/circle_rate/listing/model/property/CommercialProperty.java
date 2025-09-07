package com.circlerate.circle_rate.listing.model.property;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@NoArgsConstructor
public class CommercialProperty extends Property {
    private List<Suitability> suitability;
    private int buildingFloor;
    private boolean isPrivateWashroomAvailable;
    private boolean isPublicWashroomAvailable;
    private String amenities;

    public CommercialProperty(CommercialProperty request) {
        super(request);
        this.suitability = request.getSuitability();
        this.buildingFloor = request.getBuildingFloor();
        this.isPrivateWashroomAvailable = request.isPrivateWashroomAvailable();
        this.isPublicWashroomAvailable = request.isPublicWashroomAvailable();
        this.amenities = request.getAmenities();
    }

    public enum Suitability{
        RESTAURANTS,
        SHOP,
        OFFICE,
        INDUSTRIAL
    }

}
