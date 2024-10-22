package com.example.mydemoapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mydemoapp.models.DateGroup;
import com.example.mydemoapp.utilities.ImageGrouping;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.models.ImageItemInterface;
import com.example.mydemoapp.R;
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.databinding.FragmentPictureBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureFragment extends Fragment {
    private FragmentPictureBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPictureBinding.inflate(inflater, container, false);

        // Setup RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sample data for RecyclerView
        List<ImageItemInterface> imageList = new ArrayList<>();
        imageList.add(new ImageItem(R.drawable.flower_1, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_3, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_2, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_4, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_5, "2023-10-03"));
        //imageList.add(new ImageUrlItem(0, "2023-10-04", "https://www.google.com/url?sa=i&url=https%3A%2F%2Funsplash.com%2Fimages%2Fnature%2Fflower&psig=AOvVaw3uJKVJ5SFrVIReB3lKAUPz&ust=1729064236532000&source=images&cd=vfe&opi=89978449&ved=0CBEQjRxqFwoTCMiP567wj4kDFQAAAAAdAAAAABAE"));

        // Group images by date
        Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(imageList);
        List<DateGroup> dateGroups = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            dateGroups.add(new DateGroup(date, groupedMap.get(date)));
        }

        DateGroupAdapter dateGroupAdapter = new DateGroupAdapter(getContext(), dateGroups);
        recyclerView.setAdapter(dateGroupAdapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}