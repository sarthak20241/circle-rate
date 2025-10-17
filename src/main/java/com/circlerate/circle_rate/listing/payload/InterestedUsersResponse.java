package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.user.model.Interest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestedUsersResponse {
    private List<Interest> interests;
    private long totalInterests;
    private int currentPage;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
