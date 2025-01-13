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

import java.util.ArrayList;
import java.util.List;

public class DayGroupAdapter extends RecyclerView.Adapter<DayGroupAdapter.DateGroupViewHolder> implements IDateAdapter {
    private final List<DateGroup> dateGroups;
    private final Context context;
    private final OnImageClickListener imageClickListener;
    private final IDateAdapter.OnImageLongClickListener imageLongClickListener;
    private final List<ImageAdapter> imageAdapters = new ArrayList<>();
    public DayGroupAdapter(Context context, List<DateGroup> dateGroups,
                           OnImageClickListener imageClickListener,
                           OnImageLongClickListener imageLongClickListener) {
        this.dateGroups = dateGroups;
        this.context = context;
        this.imageClickListener = imageClickListener; // Accept listener
        this.imageLongClickListener = imageLongClickListener;
    }

    public List<DateGroup> getDateGroups(){
        return dateGroups;
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

    @Override
    public void notifyDatasetPositionalUpdates() {
        notifyItemRangeChanged(0,getItemCount());
    }

    @Override
    public void updateChildAdapter(int groupIndex, int index){
        imageAdapters.get(groupIndex).onImageClick(index);
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
            ImageAdapter imageAdapter = new ImageAdapter(context, dateGroup.getImages(), imageClickListener, imageLongClickListener, getBindingAdapterPosition()); // Pass listener to adapter
            imageAdapters.add(imageAdapter);
            recyclerView.setAdapter(imageAdapter);

        }
    }

//    public void removeImage(int dateGroupPosition, int imagePosition) {
//        if (dateGroupPosition >= 0 && dateGroupPosition < dateGroups.size()) {
//            DateGroup dateGroup = dateGroups.get(dateGroupPosition);
//
//            if (imagePosition >= 0 && imagePosition < dateGroup.getImages().size()) {
//                dateGroup.removeImageAt(imagePosition);
//
//                // If the DateGroup has no images left, remove the entire group
//                if (dateGroup.getImages().isEmpty()) {
//                    dateGroups.remove(dateGroupPosition);
//                }
//
//                // Notify the ImageAdapter of the change
//                notifyItemChanged(dateGroupPosition);
//            }
//        }
//    }
}
