package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.property.Property;
import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;



import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "propertyType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResidentialPropertyRequest.class, name = "RESIDENTIAL"),
        @JsonSubTypes.Type(value = CommercialPropertyRequest.class, name = "COMMERCIAL"),
        @JsonSubTypes.Type(value = LandPropertyRequest.class, name = "LAND")
})
public abstract class PropertyRequest {
    private String title;
    private String about;
    private long expectedPriceInRupees;
    private PropertyType propertyType;
    private ListingType listingType;
    private long areaInSqFt;
    private String ownerId;
    private String ownerName;
    private String address;
    private String subLocalityId;
    private String subLocalityName;
    private String localityId;
    private String localityName;
    private String cityName;
    private String stateName;
    private int propertyScore;
    private boolean isAvailable;
    private OwnerType postedBy;

    public abstract Property toEntity();

    protected void copyCommonFields(Property property) {
        property.setTitle(this.title);
        property.setAbout(this.about);
        property.setExpectedPriceInRupees(this.expectedPriceInRupees);
        property.setPropertyType(this.propertyType);
        property.setListingType(this.listingType);
        property.setAreaInSqFt(this.areaInSqFt);
        property.setOwnerId(this.ownerId);
        property.setOwnerName(this.ownerName);
        property.setAddress(this.address);
        property.setSubLocalityId(this.subLocalityId);
        property.setSubLocalityName(this.subLocalityName);
        property.setLocalityId(this.localityId);
        property.setLocalityName(this.localityName);
        property.setCityName(this.cityName);
        property.setStateName(this.stateName);
        property.setPropertyScore(this.propertyScore);
        property.setAvailable(this.isAvailable);
        property.setPostedBy(this.postedBy);
    }
}
