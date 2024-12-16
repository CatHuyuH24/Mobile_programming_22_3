package com.example.mydemoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.R;

import java.util.List;

public class DateGroupAdapter extends RecyclerView.Adapter<DateGroupAdapter.DateGroupViewHolder> {
    private final List<DateGroup> dateGroups;
    private final Context context;
    private final OnImageClickListener imageClickListener;
    private final OnImageLongClickListener imageLongClickListener;
    private ImageAdapter imageAdapter;
    public DateGroupAdapter(Context context, List<DateGroup> dateGroups,
                            OnImageClickListener imageClickListener,
                            OnImageLongClickListener imageLongClickListener) {
        this.dateGroups = dateGroups;
        this.context = context;
        this.imageClickListener = imageClickListener; // Accept listener
        this.imageLongClickListener = imageLongClickListener;
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

            int numberOfCol = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfCol));
            imageAdapter = new ImageAdapter(context, dateGroup.getImages(), imageClickListener, imageLongClickListener); // Pass listener to adapter
            recyclerView.setAdapter(imageAdapter);
        }
    }

    public interface OnImageClickListener {
        void onImageClick(int imageIndex);
    }

    public interface OnImageLongClickListener{
        void onImageLongClickListener(int imageIndex);
    }

    public void onLongImageClick(int index){
        imageAdapter.onLongImageClick(index);
    }

    public void removeImageOnDisplay(int imageIndexWithinWholeList){
        imageAdapter.removeImageOnDisplay(imageIndexWithinWholeList);

        dateGroups.removeIf(dateGroup -> dateGroup.getImages().isEmpty());
        notifyItemRemoved(imageIndexWithinWholeList);
    }
}
