package com.circlerate.circle_rate.listing.model.property;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
public class CommercialProperty extends Property {
    private List<Suitability> suitability;
    private int buildingFloor;
    private boolean isPrivateWashroomAvailable;
    private boolean isPublicWashroomAvailable;
    private String Amenities;

    public enum Suitability{
        RESTAURANTS,
        SHOP,
        OFFICE,
        INDUSTRIAL
    }

}
