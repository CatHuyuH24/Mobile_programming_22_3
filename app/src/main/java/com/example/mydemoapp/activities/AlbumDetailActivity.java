package com.example.mydemoapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.R;
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.utilities.AlbumManager;
import com.example.mydemoapp.utilities.ImageGrouping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlbumDetailActivity extends AppCompatActivity {
    private RecyclerView albumImagesRecyclerView;
    private Toolbar toolbar;
    private TextView albumNameTitle;
    private ImageButton deleteAlbumButton;
    private List<ImageItem> albumImages;
    private AlbumManager albumManager;
    private String albumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_activity_detail);

        albumImagesRecyclerView = findViewById(R.id.album_images_recycler_view);
        toolbar = findViewById(R.id.toolbar);
        albumNameTitle = findViewById(R.id.album_name_title);
        deleteAlbumButton = findViewById(R.id.delete_album_button);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        albumName = getIntent().getStringExtra("ALBUM_NAME");
        albumNameTitle.setText(albumName);

        albumManager = new AlbumManager(this);

        loadAlbumImages();

        setupDeleteAlbumButton();
    }

    // Load images for the selected album
    private void loadAlbumImages() {
        Album selectedAlbum = albumManager.getAlbumByName(albumName);

        if (selectedAlbum != null) {
            // Sort images by date taken
            albumImages = selectedAlbum.getImages().stream()
                    .sorted((i1, i2) -> Long.compare(i2.getDateTaken(), i1.getDateTaken()))
                    .collect(Collectors.toList());

            // Group images by date
            Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(albumImages);
            List<DateGroup> dateGroups = new ArrayList<>();
            for (String date : groupedMap.keySet()) {
                dateGroups.add(new DateGroup(date, groupedMap.get(date)));
            }

            dateGroups = dateGroups.stream()
                    .sorted((d1, d2) -> d2.getDate().compareTo(d1.getDate()))
                    .collect(Collectors.toList());

            ArrayList<String> imagePaths = albumImages.stream()
                    .map(ImageItem::getImagePath)
                    .collect(Collectors.toCollection(ArrayList::new));

            DateGroupAdapter adapter = new DateGroupAdapter(this, dateGroups, imagePath -> {
                Intent intent = new Intent(this, SoloImageActivity.class);
                intent.putExtra("IMAGE_PATHS", imagePaths);
                intent.putExtra("CURRENT_IMAGE_INDEX", imagePaths.indexOf(imagePath));
                startActivity(intent);
            });

            albumImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            albumImagesRecyclerView.setAdapter(adapter);
        }
    }

    // Setup delete album button
    private void setupDeleteAlbumButton() {
        if (albumName.equals("All")) {
            deleteAlbumButton.setEnabled(false);
            deleteAlbumButton.setAlpha(0.5f);
        }

        deleteAlbumButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Album")
                    .setMessage("Are you sure you want to delete album \"" + albumName + "\"?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        albumManager.removeAlbum(albumName);

                        Toast.makeText(this, "Album deleted", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // Reload album images when activity is resumed
    @Override
    public void onResume() {
        super.onResume();
        loadAlbumImages();
    }

    // Handle back button press
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}