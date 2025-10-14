package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.propertyenums.FurnishingStatus;
import com.circlerate.circle_rate.listing.model.propertyenums.ResidentialAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.SaleType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document("residential_properties")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Component
public class ResidentialProperty extends Property {
    private ResidentialPropertyType residentialPropertyType;

    private int noOfRooms; //index
    private int noOfWashrooms;
    private int noOfBalconies;
    private int propertyFloor;
    private int totalFloors;
    private int ageOfProperty;
    private Facing facing;

    private boolean isReraApproved;

    private FurnishingStatus furnishingStatus; //index
    private SaleType saleType;

    private List<ResidentialAmenities> amenities;


    public enum ResidentialPropertyType{
        APARTMENT,
        BUILDER_FLOOR,
        FLAT,
        VILLA,
        STUDIO_APARTMENT,
        OTHER
    }

    public enum Facing{
        NORTH,
        SOUTH,
        EAST,
        WEST,
        SOUTHEAST,
        SOUTHWEST,
        NORTHEAST,
        NORTHWEST
    }

}
