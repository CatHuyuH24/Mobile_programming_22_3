package com.example.mydemoapp.activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.mydemoapp.R;

import java.util.ArrayList;

public class SoloImageActivity extends AppCompatActivity {
    private ImageView soloImageView;
    private TextView tvTitle;
    private Button backBtn, nextBtn, previousBtn, setBackgroundBtn;
    private ArrayList<String> imagePaths;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solo_image); // Ensure you have this layout

        soloImageView = findViewById(R.id.imgView_solo_image);
        tvTitle = findViewById(R.id.txtView_solo_image_title);
        backBtn = findViewById(R.id.btn_solo_back);
        nextBtn = findViewById(R.id.btn_solo_next);
        previousBtn = findViewById(R.id.btn_solo_previous);
        setBackgroundBtn = findViewById(R.id.btn_solo_set_background);

        // Get the data from the intent
        imagePaths = getIntent().getStringArrayListExtra("IMAGE_PATHS");
        currentIndex = getIntent().getIntExtra("CURRENT_IMAGE_INDEX", -1);

        if (imagePaths != null && currentIndex != -1) {
            loadImage(currentIndex); // Load the current image
        }

        // Back button to finish the activity
        backBtn.setOnClickListener(view -> finish());

        // Set the background
        setBackgroundBtn.setOnClickListener(view -> setWallpaper());

        // Previous button with slide animation
        previousBtn.setOnClickListener(view -> {
            if (currentIndex > 0) {
                slideOutRightAndLoadImage(--currentIndex); // Slide out right and load previous image
            } else {
                Toast.makeText(this, "This is the first image", Toast.LENGTH_SHORT).show();
            }
        });

        // Next button with slide animation
        nextBtn.setOnClickListener(view -> {
            if (currentIndex < imagePaths.size() - 1) {
                slideOutLeftAndLoadImage(++currentIndex); // Slide out left and load next image
            } else {
                Toast.makeText(this, "This is the last image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImage(int index) {
        // Load the image using Glide
        String imagePath = imagePaths.get(index);
        Glide.with(this).load(imagePath).into(soloImageView);
        tvTitle.setText("Image Path: " + imagePath); // Update title
    }

    private void slideOutLeftAndLoadImage(int index) {
        Animation slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

        soloImageView.startAnimation(slideOutLeft);
        slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loadImage(index); // Load the new image
                soloImageView.startAnimation(slideInLeft); // Slide in the new image
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void slideOutRightAndLoadImage(int index) {
        Animation slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        soloImageView.startAnimation(slideOutRight);
        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                loadImage(index); // Load the new image
                soloImageView.startAnimation(slideInRight); // Slide in the new image
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            String imagePath = imagePaths.get(currentIndex);
            wallpaperManager.setBitmap(BitmapFactory.decodeFile(imagePath), null, true, WallpaperManager.FLAG_SYSTEM);
            Toast.makeText(SoloImageActivity.this, "Home screen wallpaper has been changed", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(SoloImageActivity.this, "Failed to set wallpaper", Toast.LENGTH_LONG).show();
        }
    }
}
