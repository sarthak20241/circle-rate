package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;
import lombok.Data;

@Data
public class SecondaryFilterRequest {
    private OwnerType ownerType;
    private Integer noOfWashrooms;
    private Integer noOfBalconies;
    private Integer minPropertyFloor;
    private Integer maxPropertyFloor;
    private Integer minTotalFloors;
    private Integer maxTotalFloors;
    private Integer minAgeOfProperty;
    private Integer maxAgeOfProperty;
}
