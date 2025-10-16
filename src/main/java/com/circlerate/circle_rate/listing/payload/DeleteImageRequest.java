package com.circlerate.circle_rate.listing.payload;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DeleteImageRequest {
    @NotBlank(message = "S3 key is required")
    private String s3Key;
}
