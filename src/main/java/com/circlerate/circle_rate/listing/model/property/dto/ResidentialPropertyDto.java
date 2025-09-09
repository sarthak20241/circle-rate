package com.circlerate.circle_rate.listing.model.property.dto;

import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.propertyenums.FurnishingStatus;
import com.circlerate.circle_rate.listing.model.propertyenums.ResidentialAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.SaleType;
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
public class ResidentialPropertyDto extends PropertyDto{
    private ResidentialProperty.ResidentialPropertyType residentialPropertyType;

    private int noOfRooms; //index
    private int noOfWashrooms;
    private int noOfBalconies;
    private int propertyFloor;
    private int totalFloors;
    private int ageOfProperty;
    private ResidentialProperty.Facing facing;

    private boolean isReraApproved;

    private FurnishingStatus furnishingStatus; //index
    private SaleType saleType;

    private List<ResidentialAmenities> amenities;

    public ResidentialPropertyDto(ResidentialProperty property) {
        super(property); // copies base Property fields

        this.residentialPropertyType = property.getResidentialPropertyType();
        this.noOfRooms = property.getNoOfRooms();
        this.noOfWashrooms = property.getNoOfWashrooms();
        this.noOfBalconies = property.getNoOfBalconies();
        this.propertyFloor = property.getPropertyFloor();
        this.totalFloors = property.getTotalFloors();
        this.ageOfProperty = property.getAgeOfProperty();
        this.facing = property.getFacing();
        this.isReraApproved = property.isReraApproved();
        this.furnishingStatus = property.getFurnishingStatus();
        this.saleType = property.getSaleType();
        this.amenities = property.getAmenities() != null
                ? new ArrayList<>(property.getAmenities()) // defensive copy
                : null;
    }

    public static ResidentialPropertyDto mapResidentialPropertyToResidentialPropertyDto(ResidentialProperty residentialProperty){
        return new ResidentialPropertyDto(residentialProperty);
    }


}
