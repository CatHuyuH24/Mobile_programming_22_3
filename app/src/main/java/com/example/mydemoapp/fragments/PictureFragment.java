package com.example.mydemoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.activities.SoloImageActivity;
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.databinding.FragmentPictureBinding;
import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.utilities.ImageFetcher;
import com.example.mydemoapp.utilities.ImageGrouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {
    private FragmentPictureBinding binding;
    private List<ImageItem> imageList; // Create imageList

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPictureBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch images
        List<ImageItem> imageItems = ImageFetcher.getAllImages(getContext());
        imageList = new ArrayList<>(imageItems); // Create imageList

        // Group images by date
        Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(imageItems);
        List<DateGroup> dateGroups = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            dateGroups.add(new DateGroup(date, groupedMap.get(date)));
        }

        // Initialize the adapter with a click listener
        DateGroupAdapter dateGroupAdapter = new DateGroupAdapter(getContext(), dateGroups, new DateGroupAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(String imagePath) {
                // Find the index of the clicked image
                int index = -1;
                for (int i = 0; i < imageList.size(); i++) {
                    if (imageList.get(i).getImagePath().equals(imagePath)) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    // Create an intent to start SoloImageActivity
                    Intent intent = new Intent(getActivity(), SoloImageActivity.class);
                    intent.putStringArrayListExtra("IMAGE_PATHS", getImagePathsFromList(imageList));
                    intent.putExtra("CURRENT_IMAGE_INDEX", index);
                    startActivity(intent);
                }
            }
        });

        recyclerView.setAdapter(dateGroupAdapter);

        return binding.getRoot();
    }

    private ArrayList<String> getImagePathsFromList(List<ImageItem> imageList) {
        ArrayList<String> imagePaths = new ArrayList<>();
        for (ImageItem item : imageList) {
            imagePaths.add(item.getImagePath());

        }
        return imagePaths;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}