package com.example.mydemoapp;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Context context;
    private final List<ImageItem> imageItems;

    public ImageAdapter(Context context, List<ImageItem> imageItems) {
        this.context = context;
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItemInterface imageItem = imageItems.get(position);
        if (imageItem.getImageUrl() != null && !imageItem.getImageUrl().isEmpty()) {
            Glide.with(context).load(imageItem.getImageUrl()).into(holder.imageView);
        } else {
            Glide.with(context).load(imageItem.getImageId()).into(holder.imageView);
        }

        // Huynh: set onClickListener for the small images (the images displayed initially using the adapters...)
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //change activity to 'SoloImageActivity'
                Intent soloImageActivityIntent = new Intent(context, SoloImageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("com.example.mydemoapp.IMAGE_RESOURCE_ID", imageItem.getImageId());
                bundle.putString("com.example.mydemoapp.IMAGE_URL", imageItem.getImageUrl());
                soloImageActivityIntent.putExtras(bundle);

                context.startActivity(soloImageActivityIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}