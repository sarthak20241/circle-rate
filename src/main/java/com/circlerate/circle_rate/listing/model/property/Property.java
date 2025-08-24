package com.circlerate.circle_rate.listing.model.property;

import com.circlerate.circle_rate.listing.model.Attachment;
import com.circlerate.circle_rate.listing.model.propertyenums.ListingType;
import com.circlerate.circle_rate.listing.model.propertyenums.OwnerType;
import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Document("properties")
@Data
@Component
@NoArgsConstructor
public abstract class Property {
    private String id; //already indexed
    private String title;
    private String about;
    private List<Attachment> attachments;
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

}
