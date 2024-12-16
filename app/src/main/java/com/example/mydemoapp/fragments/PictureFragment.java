package com.example.mydemoapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {
    private FragmentPictureBinding binding;
    private List<ImageItem> imageList; // Create imageList
    private boolean isSelectionMode = false;
    private final List<Integer> selectedImageIndices = new ArrayList<>();
    private DateGroupAdapter dateGroupAdapter;

    private TextView selectedImageNumber;
    private Button deleteBtn;
    private final int REQUEST_CODE_DELETE_IMAGES = 201;
    private ActivityResultLauncher<IntentSenderRequest> deleteImageLauncher;
    private Uri _toBeDeletedUri;
    
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

            // smallest to largest, since deleting from the end (stack-like)
            Collections.sort(selectedImageIndices);
            keepDeletingNextSelectedImage();
        });

        deleteImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if(result.getResultCode() != RESULT_OK){
                        return;//do nothing
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            ImageDeletion.deleteImage(_toBeDeletedUri,REQUEST_CODE_DELETE_IMAGES,requireActivity());

                            int removedIndex = selectedImageIndices.get(selectedImageIndices.size()-1);
                            imageList.remove(removedIndex);
                            selectedImageIndices.remove(selectedImageIndices.size()-1);
                            dateGroupAdapter.notifyItemRemoved(removedIndex);
                            dateGroupAdapter.removeImageOnDisplay(removedIndex);

                            updateImagesNumberDisplay();

                            if(!selectedImageIndices.isEmpty()){
                                keepDeletingNextSelectedImage();
                            } else {
                                disableSelectionMode();
                            }
                        } catch (IntentSender.SendIntentException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(getContext(), "Can't delete the image(s)", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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
            toggleSelection(imageIndex);
            updateImagesNumberDisplay();
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

        toggleSelection(imageIndex);
        updateImagesNumberDisplay();
    }

    private void toggleSelection(int imageIndex){
        if(selectedImageIndices.contains(imageIndex)){
            selectedImageIndices.remove((Integer) imageIndex);
        } else {
            selectedImageIndices.add(imageIndex);
        }
        dateGroupAdapter.onLongImageClick(imageIndex);

        if(selectedImageIndices.isEmpty()){
            disableSelectionMode();
        }
    }

    private void updateImagesNumberDisplay() {
        String numberNotification = "Selected "+selectedImageIndices.size()+" images";
        selectedImageNumber.setText(numberNotification);
    }

    private void disableSelectionMode() {
        isSelectionMode = false;
        selectedImageNumber.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);
    }

    private void keepDeletingNextSelectedImage(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            try {
                //stack-like to ensure loop
                _toBeDeletedUri = Uri.parse(imageList.get(selectedImageIndices.get(selectedImageIndices.size()-1)).getImagePath());
                ImageDeletion.deleteImage(_toBeDeletedUri,REQUEST_CODE_DELETE_IMAGES,requireActivity());
            } catch (RecoverableSecurityException e) {
                // Can't delete directly with contentResolver, handle RecoverableSecurityException
                // Request the user to confirm deletion through the system dialog
                Log.e("RecoverableSecurityException", e.getMessage());
                PendingIntent pendingIntent = e.getUserAction().getActionIntent();
                deleteImageLauncher.launch(new IntentSenderRequest(pendingIntent.getIntentSender(),null,0,0));

            } catch (Exception e) {
                Log.e("PictureFragment", "Error deleting image, an exception other than RecoverableSecurityException: ", e);
                Toast.makeText(requireContext(),"Oops! Couldn't delete the image(s)...", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Can't delete the image(s)", Toast.LENGTH_SHORT).show();
        }
    }
}