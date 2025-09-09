package com.circlerate.circle_rate.listing.payload;

import lombok.Data;

import java.util.List;

@Data
public class PresignedUrlRequest {
    private String propertyId;
    private List<FileUploadRequest> files;
}