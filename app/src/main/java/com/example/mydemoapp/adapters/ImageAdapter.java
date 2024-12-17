package com.example.mydemoapp.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.example.mydemoapp.R;
import com.example.mydemoapp.models.ImageItem;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private final Context context;
    private final List<ImageItem> images;
    private final DateGroupAdapter.OnImageClickListener imageClickListener;
    private final DateGroupAdapter.OnImageLongClickListener imageLongClickListener;
    private int groupIndex;

    public ImageAdapter(Context context, List<ImageItem> images,
                        DateGroupAdapter.OnImageClickListener imageClickListener,
                        DateGroupAdapter.OnImageLongClickListener imageLongClickListener,
                        int parentPosition) {
        this.context = context;
        this.images = images;
        this.imageClickListener = imageClickListener; // Accept listener
        this.imageLongClickListener = imageLongClickListener;
        this.groupIndex = parentPosition;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageItem imageItem = images.get(position);
        holder.bind(imageItem);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final ImageView tickIcon;
        private ImageItem imageItem;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            tickIcon = itemView.findViewById(R.id.tick_icon);

            // Set onClickListener to handle image clicks
            itemView.setOnClickListener(view -> {
                if (imageClickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        imageClickListener.onImageClick(groupIndex, imageItem.getImagePath(), getBindingAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(view -> {
                if (imageLongClickListener != null) {
                    imageItem.toggleIsSelected();
                    notifyItemChanged(getBindingAdapterPosition());
                    imageLongClickListener.onImageLongClickListener(imageItem.getImagePath());
                }
                return false;
            });
        }

        public void bind(ImageItem imageItem) {
            this.imageItem = imageItem;
            Glide.with(context)
                    .load(imageItem.getImagePath())
                    .thumbnail(0.1f) // Load images first with low quality
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Reasonable image cache
                    .override(Target.SIZE_ORIGINAL) // Resize image if necessary
                    .into(imageView);
            tickIcon.setVisibility(imageItem.isSelected()? View.VISIBLE: View.INVISIBLE);
        }
    }

    public void onImageClick(int index){
        images.get(index).toggleIsSelected();
        notifyItemChanged(index);
    }
}
