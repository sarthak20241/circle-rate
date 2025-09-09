package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.property.dto.ResidentialPropertyDto;
import com.circlerate.circle_rate.listing.model.propertyenums.FurnishingStatus;
import com.circlerate.circle_rate.listing.model.propertyenums.ResidentialAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.SaleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
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


    public ResidentialProperty(ResidentialProperty request) {
        super(request);
        this.residentialPropertyType = request.getResidentialPropertyType();
        this.noOfRooms = request.getNoOfRooms();
        this.noOfWashrooms = request.getNoOfWashrooms();
        this.noOfBalconies = request.getNoOfBalconies();
        this.propertyFloor = request.getPropertyFloor();
        this.totalFloors = request.getTotalFloors();
        this.ageOfProperty = request.getAgeOfProperty();
        this.facing = request.getFacing();
        this.isReraApproved = request.isReraApproved();
        this.furnishingStatus = request.getFurnishingStatus();
        this.saleType = request.getSaleType();
        this.amenities = request.getAmenities();
    }


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
