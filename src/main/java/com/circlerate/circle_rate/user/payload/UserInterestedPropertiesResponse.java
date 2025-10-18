package com.circlerate.circle_rate.user.payload;

import com.circlerate.circle_rate.listing.model.property.dto.PropertyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInterestedPropertiesResponse {
    private List<PropertyDto> properties;
    private long totalInterests;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
