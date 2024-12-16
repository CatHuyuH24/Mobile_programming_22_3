package com.example.mydemoapp.models;

public class ImageItem {
    private final String imagePath;
    private final long dateTaken;
    private boolean isSelected;

    public ImageItem(String imagePath, long dateTaken) {
        this.imagePath = imagePath;
        this.dateTaken = dateTaken;
        this.isSelected = false;
    }

    public String getImagePath() {
        return imagePath;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public String getDate() {
        // Convert dateTaken to a formatted date string
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date(dateTaken));
    }

    public int getImageId() {
        return 0;
    }

    public void toggleIsSelected(){
        isSelected = !isSelected;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
