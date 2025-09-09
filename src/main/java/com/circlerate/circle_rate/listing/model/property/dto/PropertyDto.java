package com.circlerate.circle_rate.listing.model.property.dto;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@NoArgsConstructor
@AllArgsConstructor
public abstract class PropertyDto {
    private String id; //already indexed
    private String title;
    private String about;
    private long expectedPriceInRupees; //index
    private PropertyType propertyType; //index
    private ListingType listingType; //index

    private boolean isCirclified;

    private long areaInSqFt; //index

    private String ownerId;
    private String ownerName;

    private String address;
    private String subLocalityId;
    private String subLocalityName;
    private String localityId;
    private String localityName;  //index
    private String cityName; //index
    private String stateName; //index

    //index
    private int propertyScore;  //to rank properties

    private boolean isAvailable; //maybe index
    private Date postedOn;
    private OwnerType postedBy;


    protected PropertyDto(Property property) {
        this.id = property.getId();
        this.title = property.getTitle();
        this.about = property.getAbout();
        this.expectedPriceInRupees = property.getExpectedPriceInRupees();
        this.propertyType = property.getPropertyType();
        this.listingType = property.getListingType();
        this.isCirclified = property.isCirclified();
        this.areaInSqFt = property.getAreaInSqFt();
        this.ownerId = property.getOwnerId();
        this.ownerName = property.getOwnerName();
        this.address = property.getAddress();
        this.subLocalityId = property.getSubLocalityId();
        this.subLocalityName = property.getSubLocalityName();
        this.localityId = property.getLocalityId();
        this.localityName = property.getLocalityName();
        this.cityName = property.getCityName();
        this.stateName = property.getStateName();
        this.propertyScore = property.getPropertyScore();
        this.isAvailable = property.isAvailable();
        this.postedOn = property.getPostedOn();
        this.postedBy = property.getPostedBy();
    }

}
