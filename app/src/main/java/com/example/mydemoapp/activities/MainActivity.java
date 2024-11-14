package com.example.mydemoapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mydemoapp.R;
import com.example.mydemoapp.fragments.AlbumFragment;
import com.example.mydemoapp.fragments.PictureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int PICTURES_ID = R.id.pictures;
    private static final int ALBUM_ID = R.id.album;
    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;

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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}