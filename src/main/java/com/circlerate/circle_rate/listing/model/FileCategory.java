package com.circlerate.circle_rate.listing.model;

public enum FileCategory {
    IMAGE("image"),
    VIDEO("video"),
    DOCUMENT("document");

    private final String folderName;

    FileCategory(String folderName) {
        this.folderName = folderName;
    }

    public String getFolderName() {
        return folderName;
    }
}