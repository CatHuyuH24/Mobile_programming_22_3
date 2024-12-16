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

public class YearGroupAdapter extends RecyclerView.Adapter<YearGroupAdapter.YearGroupViewHolder> {
    private final List<DateGroup> yearGroups;
    private final Context context;
    private final DateGroupAdapter.OnImageClickListener imageClickListener;

    public YearGroupAdapter(Context context, List<DateGroup> yearGroups, DateGroupAdapter.OnImageClickListener imageClickListener) {
        this.yearGroups = yearGroups;
        this.context = context;
        this.imageClickListener = imageClickListener;
    }

    @NonNull
    @Override
    public YearGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date_group, parent, false);
        return new YearGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YearGroupViewHolder holder, int position) {
        DateGroup yearGroup = yearGroups.get(position);
        holder.bind(yearGroup);
    }

    @Override
    public int getItemCount() {
        return yearGroups.size();
    }

    public class YearGroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final RecyclerView recyclerView;

        public YearGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }

        public void bind(DateGroup yearGroup) {
            dateText.setText(yearGroup.getDate());

            int numberOfCol = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfCol));
            ImageAdapter imageAdapter = new ImageAdapter(context, yearGroup.getImages(), imageClickListener);
            recyclerView.setAdapter(imageAdapter);
        }
    }
}