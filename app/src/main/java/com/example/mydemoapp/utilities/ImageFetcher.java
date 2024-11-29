package com.example.mydemoapp.utilities;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.mydemoapp.models.ImageItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageFetcher {
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Asynchronous method to get list of images
    public static void getAllImagesAsync(Context context, FetchImagesCallback callback) {
        executorService.execute(() -> {
            try {
                List<ImageItem> imageItems = new ArrayList<>();
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String[] projection = {
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATE_TAKEN
                };
                String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        long dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN));
                        Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        imageItems.add(new ImageItem(contentUri.toString(), dateTaken));
                    }
                    cursor.close();
                }

                if (imageItems.isEmpty()) {
                    // Show message if no image (use Toast on UI thread)
                    showToastOnUiThread(context, "No images found");
                }

                // Return results via callback
                callback.onImagesFetched(imageItems);
            } catch (Exception e) {
                callback.onError(e);
            }
        });
    }

    // Utility method to display Toast on UI thread
    private static void showToastOnUiThread(Context context, String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        );
    }

    // Callback interface to return asynchronous results
    public interface FetchImagesCallback {
        void onImagesFetched(List<ImageItem> imageItems);

        void onError(Exception e);
    }
}