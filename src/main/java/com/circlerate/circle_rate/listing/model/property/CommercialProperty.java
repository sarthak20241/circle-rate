package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.propertyenums.CommercialAmenities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@Document("commercial_properties")
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CommercialProperty extends Property {
    private List<Suitability> suitability;
    private int buildingFloor;
    private boolean isPrivateWashroomAvailable;
    private boolean isPublicWashroomAvailable;
    private List<CommercialAmenities> amenities;

    public enum Suitability{
        RESTAURANTS,
        SHOP,
        OFFICE,
        INDUSTRIAL
    }

}
