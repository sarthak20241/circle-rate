package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY, // reuse propertyType field
        property = "propertyType",
        visible = true // also bind to enum field
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResidentialProperty.class, name = "RESIDENTIAL"),
        @JsonSubTypes.Type(value = CommercialProperty.class, name = "COMMERCIAL"),
        @JsonSubTypes.Type(value = LandProperty.class, name = "LAND")
})
@Document("properties")
@Data
@Component
@NoArgsConstructor
public abstract class Property {
    private String id; //already indexed
    private String title;
    private String about;
    private List<String> mediaKeys;
    private int noOfImages;
    private int noOfVideos;
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

    public Property(Property request) {
        this.id = request.getId();
        this.title = request.getTitle();
        this.about = request.getAbout();
        this.expectedPriceInRupees = request.getExpectedPriceInRupees();
        this.propertyType = request.getPropertyType();
        this.listingType = request.getListingType();
        this.isCirclified = request.isCirclified();
        this.areaInSqFt = request.getAreaInSqFt();
        this.ownerId = request.getOwnerId();
        this.ownerName = request.getOwnerName();
        this.address = request.getAddress();
        this.subLocalityId = request.getSubLocalityId();
        this.subLocalityName = request.getSubLocalityName();
        this.localityId = request.getLocalityId();
        this.localityName = request.getLocalityName();
        this.cityName = request.getCityName();
        this.stateName = request.getStateName();
        this.propertyScore = request.getPropertyScore();
        this.isAvailable = request.isAvailable();
        this.postedOn = request.getPostedOn();
        this.postedBy = request.getPostedBy();
        this.noOfImages = request.getNoOfImages();
        this.noOfVideos = request.getNoOfVideos();
        this.mediaKeys = request.getMediaKeys();
    }



}
