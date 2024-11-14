package com.example.mydemoapp.models;

public class ImageItem implements ImageItemInterface {
    private final String imagePath;
    private final long dateTaken;

    public ImageItem(String imagePath, long dateTaken) {
        this.imagePath = imagePath;
        this.dateTaken = dateTaken;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    @Override
    public String getDate() {
        // Convert dateTaken to a formatted date string
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date(dateTaken));
    }

    @Override
    public String getImageUrl() {
        return "";
    }

    @Override
    public int getImageId() {
        return 0;
    }
}
