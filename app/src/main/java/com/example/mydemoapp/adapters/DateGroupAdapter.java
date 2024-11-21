package com.example.mydemoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.R;

import java.util.List;

public class DateGroupAdapter extends RecyclerView.Adapter<DateGroupAdapter.DateGroupViewHolder> {
    private final List<DateGroup> dateGroups;
    private final Context context;
    private final OnImageClickListener imageClickListener;

    public DateGroupAdapter(Context context, List<DateGroup> dateGroups, OnImageClickListener imageClickListener) {
        this.dateGroups = dateGroups;
        this.context = context;
        this.imageClickListener = imageClickListener; // Accept listener
    }

    @NonNull
    @Override
    public DateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date_group, parent, false);
        return new DateGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateGroupViewHolder holder, int position) {
        DateGroup dateGroup = dateGroups.get(position);
        holder.bind(dateGroup);
    }

    @Override
    public int getItemCount() {
        return dateGroups.size();
    }

    public class DateGroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final RecyclerView recyclerView;

        public DateGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }

        public void bind(DateGroup dateGroup) {
            dateText.setText(dateGroup.getDate());
            recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ImageAdapter imageAdapter = new ImageAdapter(context, dateGroup.getImages(), imageClickListener); // Pass listener to adapter
            recyclerView.setAdapter(imageAdapter);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(String imagePath);
    }
}
