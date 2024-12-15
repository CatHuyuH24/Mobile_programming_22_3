package com.example.mydemoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import java.util.Random;

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

        // Initialize empty list for images
        imageList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,
                new String[]{"Decrease", "Increase"});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilter.setAdapter(adapter);

        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                RecyclerView.Adapter<?> groupAdapter = recyclerView.getAdapter();
                if(groupAdapter == null)
                {
                    Toast.makeText(getContext(), "Adapter is null", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!(groupAdapter instanceof DateGroupAdapter))
                {
                    Toast.makeText(getContext(), "Invalid Adapter Type", Toast.LENGTH_SHORT).show();
                    return;
                }

                DateGroupAdapter dateGroupAdapter = (DateGroupAdapter) groupAdapter;
                List<DateGroup> dateGroups = dateGroupAdapter.getDateGroups();

                if(dateGroups == null || dateGroups.isEmpty())
                {
                    Toast.makeText(getContext(), "Date Groups is Empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(position == 0)
                {
                    dateGroups.sort((group1, group2) -> group2.getDate().compareTo(group1.getDate()));
                } else if (position == 1) { // Increase
                    dateGroups.sort((group1, group2) -> group1.getDate().compareTo(group2.getDate()));
                }
                // Notify the adapter of data changes
                dateGroupAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // do nothing
            }
        });

        // Fetch images asynchronously
        ImageFetcher.getAllImagesAsync(getContext(), new ImageFetcher.FetchImagesCallback() {
            @Override
            public void onImagesFetched(List<ImageItem> imageItems) {
                requireActivity().runOnUiThread(() -> {
                    // Update imageList
                    imageList.clear();
                    imageList.addAll(imageItems);

                    // Group images by date
                    Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(imageItems);
                    List<DateGroup> dateGroups = new ArrayList<>();
                    for (String date : groupedMap.keySet()) {
                        dateGroups.add(new DateGroup(date, groupedMap.get(date)));
                    }

                    // Initialize the adapter with a click listener
                    DateGroupAdapter dateGroupAdapter = new DateGroupAdapter(getContext(), dateGroups, imagePath -> {
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
                    });

                    // Set the adapter
                    recyclerView.setAdapter(dateGroupAdapter);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error fetching images: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        binding.btnRandomImage.setOnClickListener(view -> handleRandomImage());

        return binding.getRoot();
    }

    private void handleRandomImage(){
        if(imageList != null && !imageList.isEmpty())
        {
            Random random = new Random();
            int randomIndex = random.nextInt(imageList.size());
            startSoloImageActivity(randomIndex);
        }
        else {
            Toast.makeText(getContext(), "List Image Empty", Toast.LENGTH_SHORT).show();
        }
    }

    private int findImageIndex(String imagePath)
    {
        for(int i =0; i < imageList.size(); i++)
        {
            if(imageList.get(i).getImagePath().equals(imagePath))
            {
                return i;
            }
        }
        return -1;
    }

    private void startSoloImageActivity(int index)
    {
        Intent intent = new Intent(getActivity(), SoloImageActivity.class);
        intent.putStringArrayListExtra("IMAGE_PATHS", getImagePathsFromList(imageList));
        intent.putExtra("CURRENT_IMAGE_INDEX", index);
        startActivity(intent);
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