package com.circlerate.circle_rate.listing.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlResponse {
    private String url;   // Pre-signed PUT URL
    private String s3Key;
}
