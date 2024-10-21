package com.example.mydemoapp;

public class ImageUrlItem implements ImageItemInterface {
    private final int imageResId;
    private final String date;
    private final String imageUrl;

    public ImageUrlItem(int imageResId, String date, String imageUrl) {
        this.imageResId = imageResId;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    @Override
    public int getImageId() {
        return imageResId;
    }

    @Override
    public String getDate() {
        return date;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }
}