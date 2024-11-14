package com.example.mydemoapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.R;
import com.example.mydemoapp.adapters.AlbumImageAdapter;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.Image;
import com.example.mydemoapp.utilities.AlbumManager;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {
    private Spinner albumSpinner;
    private Button addAlbumButton;
    private Button removeAlbumButton;
    private RecyclerView recyclerView;

    private AlbumManager albumManager;

    private AlbumImageAdapter imageAdapter;
    private ArrayAdapter<String> albumAdapter;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        albumSpinner = view.findViewById(R.id.album_spinner);
        addAlbumButton = view.findViewById(R.id.add_album_button);
        removeAlbumButton = view.findViewById(R.id.remove_album_button);
        recyclerView = view.findViewById(R.id.recycler_view);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }

        albumManager = new AlbumManager(requireContext());

        setupRecyclerView();
        setupAlbumSpinner();
        displayAllAlbum();

        addAlbumButton.setOnClickListener(v -> showAddAlbumDialog());
        removeAlbumButton.setOnClickListener(v -> removeAlbum());

        return view;
    }

    private List<Album> getAlbums() {
        return albumManager.loadAlbums();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        imageAdapter = new AlbumImageAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(imageAdapter);
    }

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
                if (position != 0) {
                    String albumName = albumAdapter.getItem(position);
                    Album selectedAlbum = albumManager.getAlbumByName(albumName);
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

    private void displayAllAlbum() {
        Album allAlbum = albumManager.getAlbumByName("All");

        if (allAlbum == null) {
            allAlbum = new Album("All");
            albumManager.addAlbum(allAlbum);
        }

        List<Image> allImages = loadImagesFromDevice();
        allAlbum.setImages(allImages);

        displayImages(allAlbum);
    }

    private void displayImages(Album album) {
        List<Image> images = album.getImages();
        imageAdapter.setImages(images);
        imageAdapter.notifyDataSetChanged();
    }

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

    public List<Image> loadImagesFromDevice() {
        List<Image> images = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(columnIndex);
                Image image = new Image(imagePath);
                images.add(image);
            }
            cursor.close();
        }

        return images;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}