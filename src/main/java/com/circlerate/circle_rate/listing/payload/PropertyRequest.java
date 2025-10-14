package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public abstract class PropertyRequest {
    private String title;
    private String about;
    private long expectedPriceInRupees;
    private ListingType listingType;
    private long areaInSqFt;
    private String address;
    private String subLocalityId;
    private String subLocalityName;
    private String localityId;
    private String localityName;
    private String cityName;
    private String stateName;
    private boolean isAvailable;
    private OwnerType postedBy;

    public abstract Property toEntity();

    protected void copyCommonFields(Property property) {
        property.setTitle(this.title);
        property.setAbout(this.about);
        property.setExpectedPriceInRupees(this.expectedPriceInRupees);
        property.setListingType(this.listingType);
        property.setAreaInSqFt(this.areaInSqFt);
        property.setAddress(this.address);
        property.setSubLocalityId(this.subLocalityId);
        property.setSubLocalityName(this.subLocalityName);
        property.setLocalityId(this.localityId);
        property.setLocalityName(this.localityName);
        property.setCityName(this.cityName);
        property.setStateName(this.stateName);
        property.setPropertyScore(0);
        property.setAvailable(this.isAvailable);
        property.setPostedBy(this.postedBy);
        property.setPostedOn(new Date());
    }
}
