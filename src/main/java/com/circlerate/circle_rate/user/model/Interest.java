package com.circlerate.circle_rate.user.model;

import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import com.circlerate.circle_rate.user.payload.InterestRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("interests")
@NoArgsConstructor
@AllArgsConstructor
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
