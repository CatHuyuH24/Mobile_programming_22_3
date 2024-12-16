package com.example.mydemoapp.utilities;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.List;

public class ImageDeletion {

    /**
     * Delete the image(s) using the ContentResolver.
     *
     * @param imageUris The Uris of the images.
     * @param requestCode The request code for the activity
     * @param activity The activity that is requesting the deletion.
     * @return true if the image was deleted successfully, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean deleteImage(List<Uri> imageUris, int requestCode, Activity activity) throws IntentSender.SendIntentException {
        try {
            //handle gracefully
            if(imageUris.isEmpty()){
                Log.e(activity.toString(), "No image URI to delete");
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Create the request and also handling deletion, read the docs of MediaStore.createDeleteRequest
                PendingIntent intent = MediaStore.createDeleteRequest(activity.getContentResolver(), imageUris);
                activity.startIntentSenderForResult(intent.getIntentSender(), requestCode, null, 0, 0, 0);
                return true;
            }

            // Handle the old way of deleting the image
            // Check if the URI is a content URI or file URI
            if ("content".equals(imageUris.get(0).getScheme())) {
                // Use ContentResolver to delete the content
                int rowsDeleted = 0;
                for (Uri imageUri:imageUris) {
                    rowsDeleted += activity.getContentResolver().delete(imageUri, null, null);
                }
                return rowsDeleted > 0;
            }
            if ("file".equals(imageUris.get(0).getScheme())) {
                // Directly delete the file
                File file;
                for(Uri imageUri: imageUris){
                    file = new File(imageUri.getPath());
                    return file.exists() && file.delete();
                }

            }
        } catch(RecoverableSecurityException e){
            Log.e(activity.toString()+"RecoverableSecurityException", "Error deleting image with image URI, an exception occurred: ", e);
            throw e;
        } catch (Exception e) {
            Log.e(activity.toString(), "Error deleting image with image URI, an exception occurred: ", e);
            throw e;
        }
        return false;
    }
}