package com.example.mydemoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mydemoapp.R;
import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.ImageItem;

import java.util.Comparator;
import java.util.List;

public class AlbumThumbnailAdapter extends RecyclerView.Adapter<AlbumThumbnailAdapter.AlbumViewHolder> {
    private List<Album> albums;
    private Context context;
    private OnAlbumClickListener listener;

    public AlbumThumbnailAdapter(Context context, List<Album> albums, OnAlbumClickListener listener) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.album_thumbnail, parent, false);
        return new AlbumViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);

        holder.albumName.setText(album.getName());

        int imageCount = album.getImages().size();
        holder.albumImageCount.setText(imageCount + " images");

        if (!album.getImages().isEmpty()) {
            ImageItem latestImage = album.getImages().stream()
                    .max(Comparator.comparingLong(ImageItem::getDateTaken))
                    .get();

            Glide.with(context)
                    .load(latestImage.getImagePath())
                    .centerCrop()
                    .into(holder.albumThumbnail);
        } else {
            holder.albumThumbnail.setImageResource(R.drawable.default_album_thumbnail);
        }

        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    // Interface to handle album clicks
    public interface OnAlbumClickListener {
        void onAlbumClick(Album album);
    }

    // ViewHolder for album thumbnail
    public static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumThumbnail;
        TextView albumName;
        TextView albumImageCount;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            albumThumbnail = itemView.findViewById(R.id.album_thumbnail);
            albumName = itemView.findViewById(R.id.album_name);
            albumImageCount = itemView.findViewById(R.id.album_image_count);
        }
    }
}