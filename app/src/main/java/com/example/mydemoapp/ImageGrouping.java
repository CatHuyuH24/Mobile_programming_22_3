package com.example.mydemoapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGrouping {
    public static Map<String, List<ImageItem>> groupByDate(List<ImageItemInterface> imageList) {
        Map<String, List<ImageItem>> imageByDate = new HashMap<>();
        for (ImageItemInterface imageItem : imageList) {
            imageByDate.computeIfAbsent(imageItem.getDate(), k -> new ArrayList<>()).add((ImageItem) imageItem);
        }
        return imageByDate;
    }
}