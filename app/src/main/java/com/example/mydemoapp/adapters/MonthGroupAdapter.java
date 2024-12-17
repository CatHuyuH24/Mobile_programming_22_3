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

public class MonthGroupAdapter extends RecyclerView.Adapter<MonthGroupAdapter.MonthGroupViewHolder> {
    private final List<DateGroup> monthGroups;
    private final Context context;
    private final DateGroupAdapter.OnImageClickListener imageClickListener;

    public MonthGroupAdapter(Context context, List<DateGroup> monthGroups, DateGroupAdapter.OnImageClickListener imageClickListener) {
        this.monthGroups = monthGroups;
        this.context = context;
        this.imageClickListener = imageClickListener;
    }

    @NonNull
    @Override
    public MonthGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_date_group, parent, false);
        return new MonthGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthGroupViewHolder holder, int position) {
        DateGroup monthGroup = monthGroups.get(position);
        holder.bind(monthGroup);
    }

    @Override
    public int getItemCount() {
        return monthGroups.size();
    }

    public class MonthGroupViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final RecyclerView recyclerView;

        public MonthGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
            recyclerView = itemView.findViewById(R.id.recycler_view);
        }

        public void bind(DateGroup monthGroup) {
            dateText.setText(monthGroup.getDate());

            int numberOfCol = 4;
            recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfCol));
            ImageAdapter imageAdapter = new ImageAdapter(context, monthGroup.getImages(), imageClickListener);
            recyclerView.setAdapter(imageAdapter);
        }
    }
}