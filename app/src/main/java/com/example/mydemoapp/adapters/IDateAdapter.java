package com.example.mydemoapp.adapters;

import androidx.recyclerview.widget.RecyclerView;

public interface IDateAdapter {
    interface OnImageClickListener {
        void onImageClick(int groupIndex, String imagePath, int adapterPosition);
    }

    interface OnImageLongClickListener{
        void onImageLongClickListener(String imagePath);
    }

    void notifyDatasetPositionalUpdates();
    void updateChildAdapter(int groupIndex, int imagePosition);
}
