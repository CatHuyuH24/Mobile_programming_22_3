package com.example.mydemoapp.utilities;

import com.example.mydemoapp.models.ImageItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGrouping {
    public static Map<String, List<ImageItem>> groupByDate(List<ImageItem> imageList) {
        Map<String, List<ImageItem>> imageByDate = new HashMap<>();
        for (ImageItem imageItem : imageList) {
            imageByDate.computeIfAbsent(imageItem.getDate(), k -> new ArrayList<>()).add(imageItem);
        }
        return imageByDate;
    }
}