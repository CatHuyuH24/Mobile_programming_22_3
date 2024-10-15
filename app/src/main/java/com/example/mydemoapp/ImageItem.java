package com.example.mydemoapp;

import java.util.Date;

public class ImageItem implements ImageItemInterface {
    private final int imageId;
    private final String date;
    public ImageItem(int imageId, String date) {
        this.imageId = imageId;
        this.date = date;
    }
    @Override
    public int getImageId() {
        return imageId;
    }
    @Override
    public String getDate() {
        return date;
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
