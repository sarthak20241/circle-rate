package com.circlerate.circle_rate.user.payload;

import com.circlerate.circle_rate.listing.model.propertyenums.PropertyType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class InterestRequest {
    @NotBlank(message = "Property ID is required")
    private String propertyId;
    
    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    @NotNull(message = "First Name is required")
    private String firstName;
    @NotNull(message = "Last Name is required")
    private String lastName;
    @NotNull(message = "Email is required")
    private String email;
    @NotNull(message = "Contact No is required")
    private String contactNo;

    private String notes; // Optional notes about the interest
}
