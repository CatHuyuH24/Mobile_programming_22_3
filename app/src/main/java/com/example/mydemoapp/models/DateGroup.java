package com.example.mydemoapp.models;

import java.util.List;

public class DateGroup {
    private final String date;
    private final List<ImageItem> images;

    public DateGroup(String date, List<ImageItem> images) {
        this.date = date;
        this.images = images;
    }

    public String getDate() {
        return date;
    }

    public List<ImageItem> getImages() {
        return images;
    }

    public void removeImageAt(int index){images.remove(index);}
}