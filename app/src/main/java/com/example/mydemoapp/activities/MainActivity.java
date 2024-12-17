package com.example.mydemoapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mydemoapp.R;
import com.example.mydemoapp.fragments.AlbumFragment;
import com.example.mydemoapp.fragments.PictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PICTURES_ID = R.id.pictures;
    private static final int ALBUM_ID = R.id.album;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_READ_MEDIA_IMAGES = 2;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 3;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == PICTURES_ID) {
                replaceFragment(new PictureFragment());
                return true;
            } else if (item.getItemId() == ALBUM_ID) {
                replaceFragment(new AlbumFragment());
                return true;
            } else {
                return false;
            }
        });
        bottomNavigationView.setSelectedItemId(PICTURES_ID);

        // Request permissions based on the device's API level
        requestPermissionsIfNeeded();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Set default fragment
        int selectedId = bottomNavigationView.getSelectedItemId();
        if (selectedId == PICTURES_ID) {
            replaceFragment(new PictureFragment());
        } else if (selectedId == ALBUM_ID) {
            replaceFragment(new AlbumFragment());
        }
    }

    private void requestPermissionsIfNeeded() {
        // Handle permissions for Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkAndRequestPermission(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    REQUEST_CODE_READ_MEDIA_IMAGES
            );
        }
        // Handle permissions for Android 10+ (API 29) but below 13
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkAndRequestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );
        }
        // Handle permissions for Android 9 and below
        else {
            checkAndRequestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    REQUEST_CODE_READ_EXTERNAL_STORAGE
            );
            checkAndRequestPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            );
        }
    }


    private void checkAndRequestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch(requestCode){
                  case REQUEST_CODE_READ_EXTERNAL_STORAGE:
                  case REQUEST_CODE_READ_MEDIA_IMAGES:
                  case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                      Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                      replaceFragment(new PictureFragment());
                      break;
              }
        } else {
            // Permission denied, show an explanation or handle the denial
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
