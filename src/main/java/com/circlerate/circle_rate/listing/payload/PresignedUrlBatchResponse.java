package com.circlerate.circle_rate.listing.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlBatchResponse {
    private List<PresignedUrlResponse> urls;
    private List<String> errors; // error messages for exceeded limits
}