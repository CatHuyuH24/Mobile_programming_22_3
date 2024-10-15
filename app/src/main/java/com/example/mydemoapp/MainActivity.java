package com.example.mydemoapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private static final int PICTURES_ID = R.id.pictures;
    private static final int ALBUM_ID = R.id.album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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

        // Set default fragment
        replaceFragment(new PictureFragment());
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}