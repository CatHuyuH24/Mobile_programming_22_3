package com.example.mydemoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mydemoapp.R;

import java.util.List;

public class LocalImageAdapter extends RecyclerView.Adapter<LocalImageAdapter.LocalImageViewHolder> {
    private final Context context;
    private final List<String> imagePaths;

    public LocalImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public LocalImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new LocalImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalImageViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        Glide.with(context).load(imagePath).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public static class LocalImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public LocalImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
