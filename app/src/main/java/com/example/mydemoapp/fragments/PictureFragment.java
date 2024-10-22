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
import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.utilities.ImageGrouping;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.models.ImageItemInterface;
import com.example.mydemoapp.R;
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.databinding.FragmentPictureBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {
    private FragmentPictureBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPictureBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data for RecyclerView
        List<ImageItemInterface> imageList = new ArrayList<>();
        imageList.add(new ImageItem(R.drawable.flower_1, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_3, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_2, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_4, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_5, "2023-10-03"));

        // Group images by date
        Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(imageList);
        List<DateGroup> dateGroups = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            dateGroups.add(new DateGroup(date, groupedMap.get(date)));
        }

        // Initialize the adapter with a click listener
        DateGroupAdapter dateGroupAdapter = new DateGroupAdapter(getContext(), dateGroups, new DateGroupAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int imageResId) {
                // Find the index of the clicked image
                int index = -1;
                for (int i = 0; i < imageList.size(); i++) {
                    if (imageList.get(i).getImageId() == imageResId) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    // Create an intent to start SoloImageActivity
                    Intent intent = new Intent(getActivity(), SoloImageActivity.class);
                    intent.putIntegerArrayListExtra("IMAGE_IDS", getImageIdsFromList(imageList));
                    intent.putExtra("CURRENT_IMAGE_INDEX", index);
                    startActivity(intent);
                }
            }
        });

        recyclerView.setAdapter(dateGroupAdapter);

        return binding.getRoot();
    }

    private ArrayList<Integer> getImageIdsFromList(List<ImageItemInterface> imageList) {
        ArrayList<Integer> imageIds = new ArrayList<>();
        for (ImageItemInterface item : imageList) {
            if (item instanceof ImageItem) {
                imageIds.add(((ImageItem) item).getImageId());
            }
        }
        return imageIds;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
