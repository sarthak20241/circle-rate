package com.circlerate.circle_rate.user.payload;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String aboutme;
}
