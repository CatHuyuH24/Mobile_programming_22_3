package com.example.mydemoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.R;
import com.example.mydemoapp.activities.AlbumDetailActivity;
import com.example.mydemoapp.adapters.AlbumThumbnailAdapter;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.utilities.AlbumManager;
import com.example.mydemoapp.utilities.ImageFetcher;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {
    // UI components
    private ImageButton addAlbumButton;
    private RecyclerView albumRecyclerView;

    // Data
    private List<Album> albums;
    private AlbumManager albumManager;
    private AlbumThumbnailAdapter albumThumbnailAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        addAlbumButton = view.findViewById(R.id.add_album_button);
        albumRecyclerView = view.findViewById(R.id.album_recycler_view);

        albumManager = new AlbumManager(requireContext());
        albums = new ArrayList<>();

        setupAlbumRecyclerView();
        loadAlbums();

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());

        return view;
    }

    // Setup RecyclerView for albums
    private void setupAlbumRecyclerView() {
        albumThumbnailAdapter = new AlbumThumbnailAdapter(requireContext(), albums, album -> {
            Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
            intent.putExtra("ALBUM_NAME", album.getName());
            startActivity(intent);
        });

        albumRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        albumRecyclerView.setAdapter(albumThumbnailAdapter);
    }

    // Load albums from storage
    private void loadAlbums() {
        ImageFetcher.getAllImagesAsync(requireContext(), new ImageFetcher.FetchImagesCallback() {
            @Override
            public void onImagesFetched(List<ImageItem> allImages) {
                requireActivity().runOnUiThread(() -> {
                    albums.clear();
                    albumManager.removeAlbum("All");

                    Album allAlbum = new Album("All", allImages);
                    albumManager.addAlbum(allAlbum);

                    albums.addAll(albumManager.loadAlbums());
                    albumThumbnailAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> Toast
                        .makeText(requireContext(), "Error fetching images: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show());
            }
        });
    }

    // Show dialog to add new album
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

            // Check if album with the same name already exists
            boolean albumExists = albums.stream()
                    .anyMatch(album -> album.getName().equals(albumName));

            if (albumExists) {
                Toast.makeText(requireContext(), "Album already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            Album newAlbum = new Album(albumName, new ArrayList<>());
            albumManager.addAlbum(newAlbum);

            albums.add(newAlbum);
            albumThumbnailAdapter.notifyItemInserted(albums.size() - 1);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Reload albums when fragment is resumed
    @Override
    public void onResume() {
        super.onResume();
        loadAlbums();
    }
}