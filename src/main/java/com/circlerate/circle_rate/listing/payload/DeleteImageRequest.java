package com.circlerate.circle_rate.listing.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


import java.util.List;

@Data
public class DeleteImageRequest {
    @NotEmpty(message = "no s3 keys provided to delete")
    private List<String> s3Keys;
}
