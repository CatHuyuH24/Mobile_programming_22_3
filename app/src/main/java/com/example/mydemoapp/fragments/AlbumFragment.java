package com.example.mydemoapp.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.R;
import com.example.mydemoapp.activities.SoloImageActivity;
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.utilities.AlbumManager;
import com.example.mydemoapp.utilities.ImageFetcher;
import com.example.mydemoapp.utilities.ImageGrouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlbumFragment extends Fragment {
    // UI components
    private Spinner albumSpinner;
    private Button addAlbumButton;
    private Button removeAlbumButton;
    private RecyclerView recyclerView;

    // Data
    private List<DateGroup> dateGroups;
    private AlbumManager albumManager;
    private String selectedAlbumName;

    // Adapter
    private ArrayAdapter<String> albumAdapter;

    // Constructor
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumSpinner = view.findViewById(R.id.album_spinner);
        addAlbumButton = view.findViewById(R.id.add_album_button);
        removeAlbumButton = view.findViewById(R.id.remove_album_button);
        recyclerView = view.findViewById(R.id.recycler_view);

        albumManager = new AlbumManager(requireContext());
        dateGroups = new ArrayList<>();
        selectedAlbumName = "All";

        setupRecyclerView();
        setupAlbumSpinner();
        displayAllAlbum();

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());
        removeAlbumButton.setOnClickListener(v -> removeAlbum());

        return view;
    }

    // Get all albums from storage
    private List<Album> getAlbums() {
        return albumManager.loadAlbums();
    }

    // Setup recycler view for displaying images
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        DateGroupAdapter dateGroupAdapter = new DateGroupAdapter(getContext(), dateGroups, imagePath -> {
            // Find the index of the clicked image
            ArrayList<String> imagePaths = albumManager.getImagePathsFromAlbum(selectedAlbumName);
            int index = -1;
            for (int i = 0; i < imagePaths.size(); i++) {
                if (imagePaths.get(i).equals(imagePath)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                // Create an intent to start SoloImageActivity
                Intent intent = new Intent(getActivity(), SoloImageActivity.class);
                intent.putStringArrayListExtra("IMAGE_PATHS", imagePaths);
                intent.putExtra("CURRENT_IMAGE_INDEX", index);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(dateGroupAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        displayImages(albumManager.getAlbumByName(selectedAlbumName));
    }

    // Setup album spinner for selecting albums
    private void setupAlbumSpinner() {
        List<String> albumNames = new ArrayList<>();
        for (Album album : getAlbums()) {
            albumNames.add(album.getName());
        }

        albumAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, albumNames);
        albumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        albumSpinner.setAdapter(albumAdapter);

        albumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAlbumName = albumAdapter.getItem(position);

                if (position != 0) {
                    Album selectedAlbum = albumManager.getAlbumByName(selectedAlbumName);
                    displayImages(selectedAlbum);
                } else {
                    displayAllAlbum();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // Display all images in the 'All' album
    private void displayAllAlbum() {
        Album allAlbum = albumManager.getAlbumByName("All");
        List<ImageItem> allImages = ImageFetcher.getAllImages(requireContext());

        if (allAlbum == null) {
            allAlbum = new Album("All");
            allAlbum.setImages(allImages);
            albumManager.addAlbum(allAlbum);

            if (albumAdapter.getCount() == 0) {
                albumAdapter.add("All");
            }
        } else {
            allAlbum.setImages(allImages);
            albumManager.updateAlbum(allAlbum);
        }

        displayImages(allAlbum);
    }

    // Display images in the selected album
    private void displayImages(Album album) {
        List<ImageItem> images = album.getImages();

        Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(images);
        dateGroups.clear();
        for (String date : groupedMap.keySet()) {
            dateGroups.add(new DateGroup(date, groupedMap.get(date)));
        }

        recyclerView.getAdapter().notifyDataSetChanged();
    }

    // Show dialog for adding new album to storage
    private void showAddAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add new album");

        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String albumName = input.getText().toString();
            if (albumName.isEmpty()) {
                Toast.makeText(requireContext(), "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Album album : getAlbums()) {
                if (album.getName().equals(albumName)) {
                    Toast.makeText(requireContext(), "Album already exists", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            Album newAlbum = new Album(albumName, new ArrayList<>());
            albumManager.addAlbum(newAlbum);

            albumAdapter.add(albumName);
            albumAdapter.notifyDataSetChanged();
            albumSpinner.setSelection(albumAdapter.getCount() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Remove selected album from storage
    private void removeAlbum() {
        String selectedAlbum = albumSpinner.getSelectedItem().toString();

        if (selectedAlbum.equals("All")) {
            Toast.makeText(requireContext(), "Cannot delete album 'All'", Toast.LENGTH_SHORT).show();
            return;
        }

        albumManager.removeAlbum(selectedAlbum);

        albumAdapter.remove(selectedAlbum);
        albumAdapter.notifyDataSetChanged();
        albumSpinner.setSelection(0);

        Toast.makeText(requireContext(), "Deleted album: " + selectedAlbum, Toast.LENGTH_SHORT).show();
    }

    // Clean up
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}