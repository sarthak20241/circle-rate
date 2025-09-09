package com.circlerate.circle_rate.listing.payload;

import com.circlerate.circle_rate.listing.model.FileCategory;
import lombok.Data;

@Data
public class FileUploadRequest {
    private FileCategory fileCategory;
    private String mimeType;// MIME type (image/png, video/mp4, application/pdf)
}