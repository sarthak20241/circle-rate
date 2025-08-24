package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.propertyenums.FurnishingStatus;
import com.circlerate.circle_rate.listing.model.propertyenums.ResidentialAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.SaleType;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
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

    private List<ResidentialAmenities> Amenities;



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
