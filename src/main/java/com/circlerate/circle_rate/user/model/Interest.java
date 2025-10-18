package com.circlerate.circle_rate.user.model;

import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.circlerate.circle_rate.user.payload.InterestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("interests")
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({
    @CompoundIndex(name = "property_created_idx", def = "{'propertyId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "user_created_idx", def = "{'userId': 1, 'createdAt': -1}"),
    @CompoundIndex(name = "user_property_idx", def = "{'userId': 1, 'propertyId': 1}", unique = true)
})
public class Interest {
    @Id
    private String id;
    private String userId;
    private String propertyId;
    private PropertyType propertyType;
    private Date createdAt;

    //form details
    private String firstName;
    private String lastName;
    private String contactNo;
    private String email;

    private String notes; // Optional notes from user about their interest

    public Interest(String userId, InterestRequest request){
        this.userId = userId;
        this.propertyId = request.getPropertyId();
        this.propertyType = request.getPropertyType();
        this.firstName = request.getFirstName();
        this.lastName = request.getLastName();
        this.email = request.getEmail();
        this.contactNo = request.getContactNo();
        this.createdAt = new Date();
        this.notes = request.getNotes();
    }
}
