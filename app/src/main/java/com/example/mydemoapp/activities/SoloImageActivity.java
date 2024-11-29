package com.example.mydemoapp.activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.example.mydemoapp.R;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.utilities.AlbumManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SoloImageActivity extends AppCompatActivity {
    private ImageView soloImageView;
    private TextView tvTitle;
    private Button backBtn, nextBtn, previousBtn, setBackgroundBtn, addToAlbumBtn, deleteFromAlbumBtn;

    private ArrayList<String> imagePaths;
    private int currentIndex;

    private final int CROP_REQUEST_CODE = 1;

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
        addToAlbumBtn = findViewById(R.id.btn_solo_add_to_album);
        deleteFromAlbumBtn = findViewById(R.id.btn_solo_delete_from_album);

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

        // Add to album button
        addToAlbumBtn.setOnClickListener(view -> addToAlbum());

        // Delete from album button
        deleteFromAlbumBtn.setOnClickListener(view -> deleteFromAlbum());
    }

    private void loadImage(int index) {
        // Load the image using Glide
        String imagePath = imagePaths.get(index);

        Glide.with(this)
                .load(imagePath)
                .thumbnail(0.1f) // Load images first with low quality
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Reasonable image cache
                .override(Target.SIZE_ORIGINAL) // Resize image if necessary
                .into(soloImageView);
        
        String tempTitle = "Image path: " + imagePath;
        tvTitle.setText(tempTitle); // Update title
    }

    private void slideOutLeftAndLoadImage(int index) {
        Animation slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);
        Animation slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);

        soloImageView.startAnimation(slideOutLeft);
        slideOutLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadImage(index); // Load the new image
                soloImageView.startAnimation(slideInLeft); // Slide in the new image
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void slideOutRightAndLoadImage(int index) {
        Animation slideOutRight = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        Animation slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);

        soloImageView.startAnimation(slideOutRight);
        slideOutRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                loadImage(index); // Load the new image
                soloImageView.startAnimation(slideInRight); // Slide in the new image
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    // Using android's built in crop feature to allow user to choose the frame/position

    private void setWallpaper() {
        try {
            Uri imageUri = Uri.parse(imagePaths.get(currentIndex));

            // Create a temporary file URI for the cropped output
            Uri croppedImageUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(imageUri, "image/*");
            cropIntent.putExtra("crop", "true");

            // Get the dimensions of the device's display
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;
            float ratio = (float)screenWidth/ (float)screenHeight;

            //to handle different devices' screen type
            int smallerSideAspect = 1;
            int largerSideAspect = (int) Math.ceil(1 * 1.0 / ratio);
            if(screenWidth < screenHeight){
                cropIntent.putExtra("aspectX", smallerSideAspect);
                cropIntent.putExtra("aspectY", largerSideAspect);
            }
            else {
                cropIntent.putExtra("aspectX",largerSideAspect);
                cropIntent.putExtra("aspectY",smallerSideAspect);
            }



            cropIntent.putExtra("outputX", screenWidth);
            cropIntent.putExtra("outputY", screenHeight);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data",true);

            // Specify the output file URI
//            cropIntent.putExtra("output", croppedImageUri);
//            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            startActivityForResult(cropIntent, CROP_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("SoloImageActivity", "Error while trying to crop the image", e);
            Toast.makeText(SoloImageActivity.this, "Failed to crop image: " + e, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //debugging
        Toast.makeText(this,"onActivityResult is called", Toast.LENGTH_LONG).show();

        if (requestCode == CROP_REQUEST_CODE && resultCode == RESULT_OK) {
            // Extract the URI of the cropped image from the intent
//            Uri croppedImageUri = data != null ? data.getData() : null;

            //debugging
            if(data == null){
                Toast.makeText(this, "data is null!!!",Toast.LENGTH_LONG).show();
                return;
            }

            Uri croppedImageUri = data.getData();
            if (croppedImageUri != null) {
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    Bitmap croppedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(croppedImageUri));

                    // Get the dimensions of the device's display
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    int screenWidth = displayMetrics.widthPixels;
                    int screenHeight = displayMetrics.heightPixels;

                    Log.e("screenWidth: ", Integer.toString(screenWidth));
                    Log.e("screenHeight: ", Integer.toString(screenHeight));
                    Log.e("ratio: ", Integer.toString(screenWidth/screenHeight));

                    // Define a crop rectangle (center-crop the bitmap to match screen dimensions)
                    int bitmapWidth = croppedBitmap.getWidth();
                    int bitmapHeight = croppedBitmap.getHeight();

                    int left = (bitmapWidth - screenWidth) / 2;
                    int top = (bitmapHeight - screenHeight) / 2;
                    int right = left + screenWidth;
                    int bottom = top + screenHeight;

                    // Ensure the crop rectangle stays within bounds of the bitmap
                    Rect cropRect = new Rect(
                            Math.max(left, 0),
                            Math.max(top, 0),
                            Math.min(right, bitmapWidth),
                            Math.min(bottom, bitmapHeight)
                    );

//                    wallpaperManager.setBitmap(croppedBitmap, cropRect, true, WallpaperManager.FLAG_SYSTEM);
                    wallpaperManager.setBitmap(croppedBitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                    Toast.makeText(SoloImageActivity.this, "Wallpaper set successfully", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("SoloImageActivity", "Error setting wallpaper", e);
                    Toast.makeText(SoloImageActivity.this, "Failed to set wallpaper: " + e, Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("SoloImageActivity", "Cropped image URI is null");
            }
        }
    }


    private void addToAlbum() {
        AlbumManager albumManager = new AlbumManager(this);
        List<Album> albums = albumManager.loadAlbums();
        List<String> albumNames = new ArrayList<>();

        for (Album album : albums) {
            if (album.getName().equals("All")) {
                continue;
            }
            albumNames.add(album.getName());
        }

        if (albumNames.isEmpty()) {
            Toast.makeText(this, "No albums found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show dialog to select an album to add the image to
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select an album")
                .setItems(albumNames.toArray(new String[0]), (dialog, which) -> {
                    try {
                        String selectedAlbumName = albumNames.get(which);
                        albumManager.addImageToAlbum(selectedAlbumName, imagePaths.get(currentIndex));
                        Toast.makeText(this, "Image added to album: " + selectedAlbumName, Toast.LENGTH_SHORT).show();
                    } catch (IllegalArgumentException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void deleteFromAlbum() {
        AlbumManager albumManager = new AlbumManager(this);
        List<String> albumNames = albumManager.getAlbumNames(imagePaths.get(currentIndex));

        if (albumNames.isEmpty()) {
            Toast.makeText(this, "This image is not in any album", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show dialog to select an album to delete the image from
        new MaterialAlertDialogBuilder(this)
                .setTitle("Select an album")
                .setItems(albumNames.toArray(new String[0]), (dialog, which) -> {
                    String selectedAlbumName = albumNames.get(which);
                    albumManager.removeImageFromAlbum(selectedAlbumName, imagePaths.get(currentIndex));
                    Toast.makeText(this, "Image deleted from album: " + selectedAlbumName, Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
