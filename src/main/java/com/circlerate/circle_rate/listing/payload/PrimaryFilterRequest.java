package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import lombok.Data;

import java.util.List;

@Data
public class PrimaryFilterRequest {
    private PropertyType propertyType;
    private ListingType listingType;

    private Long minPrice;
    private Long maxPrice;

    private Long minSqFtArea;
    private Long maxSqFtArea;

    private List<Integer> noOfRooms;

    private String subLocalityName;
    private String localityName;
    private String cityName;
    private String stateName;


}
