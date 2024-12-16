package com.example.mydemoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.mydemoapp.utilities.ImageDeletion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {
    private FragmentPictureBinding binding;
    private List<ImageItem> imageList; // Create imageList
    private boolean isSelectionMode = false;
    private final List<String> selectedImagePaths = new ArrayList<>();
    private DateGroupAdapter dateGroupAdapter;

    private TextView selectedImageNumber;
    private Button deleteBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPictureBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedImageNumber = binding.selectedImagesNumber;
        deleteBtn = binding.btnDeleteImage;

        // Initialize empty list for images
        imageList = new ArrayList<>();

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
                    dateGroupAdapter =
                            new DateGroupAdapter(getContext(), dateGroups,
                                    imageIndex -> onImageClick(imageIndex),
                                    imageIndexLongClick -> onLongImageClick(imageIndexLongClick));

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

        deleteBtn.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Delete button clicked", Toast.LENGTH_SHORT).show();
        });

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

    private void onImageClick(int imageIndex) {
//        int index = -1;
//        for (int i = 0; i < imageList.size(); i++) {
//            if (imageList.get(i).getImagePath().equals(imagePath)) {
//                index = i;
//                break;
//            }
//        }

        if(isSelectionMode){
            toggleSelectionAndNotify(imageIndex);
        } else {
            if (imageIndex != -1) {
                // Create an intent to start SoloImageActivity
                Intent intent = new Intent(getActivity(), SoloImageActivity.class);
                intent.putStringArrayListExtra("IMAGE_PATHS", getImagePathsFromList(imageList));
                intent.putExtra("CURRENT_IMAGE_INDEX", imageIndex);
                startActivity(intent);
            }
        }


    }

    private void onLongImageClick(int imageIndex){
//        int index = -1;
//        for (int i = 0; i < imageList.size(); i++) {
//            if (imageList.get(i).getImagePath().equals(imagePath)) {
//                index = i;
//                break;
//            }
//        }
        if(!isSelectionMode){
            isSelectionMode = true;
            selectedImageNumber.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        toggleSelectionAndNotify(imageIndex);
    }

    private void toggleSelectionAndNotify(int imageIndex){
        String imagePath = imageList.get(imageIndex).getImagePath();
        if(selectedImagePaths.contains(imagePath)){
            selectedImagePaths.remove(imagePath);
        } else {
            selectedImagePaths.add(imagePath);
        }
        dateGroupAdapter.onLongImageClick(imageIndex);

        String numberNotification = "Selected "+selectedImagePaths.size()+" images";
        selectedImageNumber.setText(numberNotification);

        if(selectedImagePaths.isEmpty()){
            isSelectionMode = false;
            selectedImageNumber.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.INVISIBLE);
        }
    }
}