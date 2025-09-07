package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.property.ResidentialProperty;
import com.circlerate.circle_rate.listing.model.propertyenums.FurnishingStatus;
import com.circlerate.circle_rate.listing.model.propertyenums.ResidentialAmenities;
import com.circlerate.circle_rate.listing.model.propertyenums.SaleType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ResidentialPropertyRequest extends PropertyRequest {
    private ResidentialProperty.ResidentialPropertyType residentialPropertyType;
    private int noOfRooms;
    private int noOfWashrooms;
    private int noOfBalconies;
    private int propertyFloor;
    private int totalFloors;
    private int ageOfProperty;
    private ResidentialProperty.Facing facing;
    private boolean isReraApproved;
    private FurnishingStatus furnishingStatus;
    private SaleType saleType;
    private List<ResidentialAmenities> amenities;

    @Override
    public Property toEntity() {
        ResidentialProperty property = new ResidentialProperty();
        copyCommonFields(property);
        property.setResidentialPropertyType(this.residentialPropertyType);
        property.setNoOfRooms(this.noOfRooms);
        property.setNoOfWashrooms(this.noOfWashrooms);
        property.setNoOfBalconies(this.noOfBalconies);
        property.setPropertyFloor(this.propertyFloor);
        property.setTotalFloors(this.totalFloors);
        property.setAgeOfProperty(this.ageOfProperty);
        property.setFacing(this.facing);
        property.setReraApproved(this.isReraApproved);
        property.setFurnishingStatus(this.furnishingStatus);
        property.setSaleType(this.saleType);
        property.setAmenities(this.amenities);
        return property;
    }

}

