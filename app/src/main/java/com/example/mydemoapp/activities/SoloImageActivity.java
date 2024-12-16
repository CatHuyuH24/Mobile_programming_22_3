package com.example.mydemoapp.activities;

import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.GestureDetector;
import android.view.MotionEvent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import com.example.mydemoapp.R;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.utilities.AlbumManager;
import com.example.mydemoapp.utilities.ImageDeletion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;




public class SoloImageActivity extends AppCompatActivity {
    private ImageView soloImageView;
    private TextView tvTitle;
    private Button backBtn, setBackgroundBtn, addToAlbumBtn, deleteFromAlbumBtn, deleteImageBtn;

    private ArrayList<String> imagePaths;
    private int currentIndex;

    private final int CROP_REQUEST_CODE = 4;
    private final int REQUEST_CODE_DELETE_IMAGE = 5;
    private final int REQUEST_CODE_DELETE_CROPPED_IMAGE = 6;

    private Uri _croppedImageUri;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solo_image); // Ensure you have this layout

        soloImageView = findViewById(R.id.imgView_solo_image);
        tvTitle = findViewById(R.id.txtView_solo_image_title);
        backBtn = findViewById(R.id.btn_solo_back);
        setBackgroundBtn = findViewById(R.id.btn_solo_set_background);
        addToAlbumBtn = findViewById(R.id.btn_solo_add_to_album);
        deleteFromAlbumBtn = findViewById(R.id.btn_solo_delete_from_album);
        
        deleteImageBtn = findViewById(R.id.btn_delete_image);
        // Get the data from the intent
        imagePaths = getIntent().getStringArrayListExtra("IMAGE_PATHS");
        currentIndex = getIntent().getIntExtra("CURRENT_IMAGE_INDEX", -1);

        if (imagePaths != null && currentIndex != -1) {
            loadImage(currentIndex); // Load the current image
        }

        // Back button to finish the activity
        backBtn.setOnClickListener(view -> finish());

        // Set the background
        setBackgroundBtn.setOnClickListener(view -> startSettingWallpaper());

        // Add to album button
        addToAlbumBtn.setOnClickListener(view -> addToAlbum());

        // Delete from album button
        deleteFromAlbumBtn.setOnClickListener(view -> deleteFromAlbum());
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        deleteImageBtn.setOnClickListener(view -> {
            AlbumManager albumManager = new AlbumManager(this);

            // Create and show the dialog box
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        albumManager.removeImageFromAlbum("all", imagePaths.get(currentIndex));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Uri imageUri = Uri.parse(imagePaths.get(currentIndex));
                            try {
                                ImageDeletion.deleteImage(imageUri, REQUEST_CODE_DELETE_IMAGE, this);
                            } catch (RecoverableSecurityException e) {
                                // can't delete directly with contentResolver, handle RecoverableSecurityException
                                Log.e("RecoverableSecurityException deleting an image", e.getMessage());

                                // Request the user to confirm deletion through the system dialog
                                PendingIntent pendingIntent = e.getUserAction().getActionIntent();
                                try {
                                    startIntentSenderForResult(
                                            pendingIntent.getIntentSender(),
                                            REQUEST_CODE_DELETE_IMAGE,
                                            null,
                                            0,
                                            0,
                                            0
                                    );
                                } catch (IntentSender.SendIntentException ex) {
                                    throw new RuntimeException(ex);
                                }
                            } catch (Exception e) {
                                Log.e("SoloImageActivity", "Error deleting image, an exception other than RecoverableSecurityException: ", e);
                            }
                        } else {
                            Toast.makeText(this, "Can't delete the image", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // Close the dialog if the user cancels
                    })
                    .show();
        });

    }

    private void loadImage(int index) {
        // Load the image using Glide
        String imagePath = imagePaths.get(index);

        Glide.with(this)
                .load(imagePath)
                .thumbnail(0.1f) // Load images first with low quality
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Reasonable image cache
                .override(Target.SIZE_ORIGINAL)
                .into(soloImageView);

        ImageItem imageItem = processImageItemFromUri(Uri.parse(imagePath));
        String tempTitle = "Date: " + imageItem.getDate();
        tvTitle.setText(tempTitle);
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
    private void startSettingWallpaper() {
        try {
            Uri imageUri = Uri.parse(imagePaths.get(currentIndex));

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

            startActivityForResult(cropIntent, CROP_REQUEST_CODE);
        } catch (Exception e) {
            Log.e("SoloImageActivity", "Error while trying to crop the image", e);
            Toast.makeText(SoloImageActivity.this, "Failed to crop image: " + e, Toast.LENGTH_LONG).show();
            _croppedImageUri = Uri.parse(imagePaths.get(currentIndex));
            setWallpaper();
        }
    }

    private void setWallpaper(){
        if (_croppedImageUri != null) {
            try {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                Bitmap croppedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(_croppedImageUri));

                wallpaperManager.setBitmap(croppedBitmap,null,true,WallpaperManager.FLAG_SYSTEM);
                Toast.makeText(SoloImageActivity.this, "Wallpaper set successfully", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("SoloImageActivity", "Error setting wallpaper", e);
                Toast.makeText(SoloImageActivity.this, "Failed to set wallpaper: " + e, Toast.LENGTH_LONG).show();
            } finally {
                //delete the newly cropped image, whether setting image as wallpaper succeeds or not
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    try {
                        croppedShowDeletionExplanationAndDelete();

                    } catch (RecoverableSecurityException e) {
                        // can't delete directly with contentResolver, handle RecoverableSecurityException
                        Log.e("RecoverableSecurityException", e.getMessage());
                        // Request the user to confirm deletion through the system dialog
                        PendingIntent pendingIntent = e.getUserAction().getActionIntent();

                        try {
                            // Show the dialog to the user to confirm deletion
                            startIntentSenderForResult(
                                    pendingIntent.getIntentSender(),
                                    REQUEST_CODE_DELETE_CROPPED_IMAGE,
                                    null,
                                    0,
                                    0,
                                    0
                            );

                        } catch (IntentSender.SendIntentException sendIntentException) {
                            Log.e("Send intent exception", "Failed to send intent for deletion", sendIntentException);
                        }

                    } catch (Exception e) {
                        Log.e("SoloImageActivity", "Error deleting cropped image, an exception other than RecoverableSecurityException: ", e);
                    }

                }
                else {
                    Toast.makeText(this,"Can't delete the cropped image",Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Log.e("SoloImageActivity", "Cropped image URI is null");
        }

    }

        /**
         * Shows a dialog explaining why you're requesting permission to delete the image.
         */
        private void croppedShowDeletionExplanationAndDelete() {
            // Show a dialog explaining why you're requesting permission to delete the image
            new AlertDialog.Builder(this)
                    .setTitle("Permission Request")
                    .setMessage("We need your permission to \ndelete the TEMPORARY cropped image (auto-created by us) \nto keep your gallery organized.\nPlease allow us to delete it for you.")
                    .setPositiveButton("OK", (dialog, which) ->{
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            try {
                                ImageDeletion.deleteImage(_croppedImageUri, REQUEST_CODE_DELETE_CROPPED_IMAGE,this);
                            }
                            catch (RecoverableSecurityException e) {
                                // can't delete directly with contentResolver, handle RecoverableSecurityException
                                Log.e("RecoverableSecurityException", e.getMessage());
                                // Request the user to confirm deletion through the system dialog
                                PendingIntent pendingIntent = e.getUserAction().getActionIntent();

                                try {
                                    // Show the dialog to the user to confirm deletion
                                    startIntentSenderForResult(
                                            pendingIntent.getIntentSender(),
                                            REQUEST_CODE_DELETE_CROPPED_IMAGE,
                                            null,
                                            0,
                                            0,
                                            0
                                    );

                                } catch (IntentSender.SendIntentException sendIntentException) {
                                    throw new RuntimeException(sendIntentException);
                                }
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                Log.e("Send intent exception", "Failed to send intent for deletion", sendIntentException);
                                throw new RuntimeException(sendIntentException);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set the the cropped image as the wallpaper, then delete it
        if (requestCode == CROP_REQUEST_CODE && resultCode == RESULT_OK) {

            if(data == null){
                Toast.makeText(this,"Can't set the image to be the wallpaper",Toast.LENGTH_LONG).show();
                Log.e( "Setting Wallpaper Error","Data when setting wallpaper is null!!!");
                return;
            }

            // Extract the URI of the cropped image from the intent
            _croppedImageUri = data.getData();
            setWallpaper();
        }

        if (requestCode == REQUEST_CODE_DELETE_CROPPED_IMAGE) {
            try{
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        //do nothing
                    } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                        if(_croppedImageUri != null){
                            ImageDeletion.deleteImage(_croppedImageUri, REQUEST_CODE_DELETE_CROPPED_IMAGE,this);
                        }
                    }
                } else {
                    Log.e("SoloImageActivity", "User denied deletion");
                }
            }catch (Exception e) {
                Log.e("Exception occurred while trying to re-delete the CROPPED image ", e.getMessage());
            }
        }

        if(requestCode == REQUEST_CODE_DELETE_IMAGE){
            try {
                if(resultCode == RESULT_OK){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        finish();
                    } else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q){
                        if(ImageDeletion.deleteImage(Uri.parse(imagePaths.get(currentIndex)), REQUEST_CODE_DELETE_IMAGE,this)){
                            finish();
                        }
                    }
                }
                else {
                    Log.e("SoloImageActivity", "User denied deletion");
                }
            } catch (Exception e){
                Log.e("Exception occurred while trying to re-delete the image ",e.getMessage());
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || gestureDetector.onTouchEvent(event);
    }

    // In the SimpleOnGestureListener subclass, override gestures that needs detecting.
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(@NonNull MotionEvent event) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2,
                                float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling( MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            assert e1 != null;
            assert e2 != null;
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > 100 && Math.abs(velocityX) > 100) {
                    if (diffX > 0) {
                        // Swipe right
                        if (currentIndex > 0) {
                            slideOutRightAndLoadImage(--currentIndex);
                        } else {
                            Toast.makeText(SoloImageActivity.this, "This is the first image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Swipe left
                        if (currentIndex < imagePaths.size() - 1) {
                            slideOutLeftAndLoadImage(++currentIndex);
                        } else {
                            Toast.makeText(SoloImageActivity.this, "This is the last image", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
            }
            else{
                return false;
            }
            return false;
        }

    };


    public ImageItem processImageItemFromUri(Uri uri) {
        String filePath = null;
        long dateTaken = 0;

        if ("content".equals(uri.getScheme())) {
            // Use ContentResolver to retrieve metadata from the Uri
            Cursor cursor = getContentResolver().query(uri,
                    new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int filePathColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int dateColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);

                if (filePathColumn != -1) {
                    filePath = cursor.getString(filePathColumn);
                }
                if (dateColumn != -1) {
                    dateTaken = cursor.getLong(dateColumn);
                }
                cursor.close();
            }
        } else if ("file".equals(uri.getScheme())) {
            File file = new File(uri.getPath());
            if (!file.exists()){
                Log.e("File does not exist when processing image item from Uri path: ",uri.getPath());
                return null;
            }
            filePath = file.getAbsolutePath();

            // Retrieve date from file's last modified timestamp
            dateTaken = file.lastModified();
        }

        // Create an ImageItem instance
        if (filePath != null) {
            return new ImageItem(filePath, dateTaken);
        }

        return null; // Return null if the process fails
    }


}


