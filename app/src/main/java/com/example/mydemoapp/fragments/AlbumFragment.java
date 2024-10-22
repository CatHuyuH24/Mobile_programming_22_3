package com.example.mydemoapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
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
import com.example.mydemoapp.adapters.DateGroupAdapter;
import com.example.mydemoapp.utilities.ImageGrouping;
import com.example.mydemoapp.models.ImageItem;
import com.example.mydemoapp.models.ImageItemInterface;
import com.example.mydemoapp.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.HashMap;
import java.util.Set;

import android.widget.Toast;

public class AlbumFragment extends Fragment {
    private Spinner categorySpinner;
    private Button addCategoryButton;
    private Button removeCategoryButton;
    private RecyclerView recyclerView;

    private ArrayAdapter<String> spinnerAdapter;
    private DateGroupAdapter dateGroupAdapter;
    private HashMap<String, List<DateGroup>> categoryImages = new HashMap<>();
    private List<String> categories = new ArrayList<>();

    private static final String PREFS_NAME = "PhotoAlbumPrefs";
    private static final String KEY_CATEGORIES = "categories";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        categorySpinner = view.findViewById(R.id.category_spinner);
        addCategoryButton = view.findViewById(R.id.add_category_button);
        removeCategoryButton = view.findViewById(R.id.remove_category_button);
        recyclerView = view.findViewById(R.id.recycler_view);

        setupRecyclerView();
        setupCategorySpinner();

        loadCategoriesFromPreferences();

        addCategoryButton.setOnClickListener(v -> showAddCategoryDialog());
        removeCategoryButton.setOnClickListener(v -> removeCategory());

        return view;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dateGroupAdapter = new DateGroupAdapter(requireContext(), new ArrayList<>());
        recyclerView.setAdapter(dateGroupAdapter);
    }

    private void setupCategorySpinner() {
        categories.add("All");

        spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String currentCategory = categories.get(position);
                displayImagesForCategory(currentCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void loadCategoriesFromPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> categorySet = preferences.getStringSet(KEY_CATEGORIES, new HashSet<>());

        categories.clear();
        categories.addAll(categorySet);

        if (!categories.contains("A")) {
            if (!categories.contains("All")) {
                categories.add("All");
            }
            initializeSampleData();
            spinnerAdapter.notifyDataSetChanged();
            saveCategoriesToPreferences();
        } else {
            displayImagesForCategory("All");
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    private void displayImagesForCategory(String category) {
        List<DateGroup> dateGroupList = categoryImages.get(category);

        if (dateGroupList != null && !dateGroupList.isEmpty()) {
            dateGroupAdapter.updateDateGroups(dateGroupList);
        } else {
            dateGroupAdapter.updateDateGroups(new ArrayList<>());
        }
    }

    private void initializeSampleData() {
        List<ImageItemInterface> imageList = new ArrayList<>();
        imageList.add(new ImageItem(R.drawable.flower_1, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_3, "2023-10-01"));
        imageList.add(new ImageItem(R.drawable.flower_2, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_4, "2023-10-02"));
        imageList.add(new ImageItem(R.drawable.flower_5, "2023-10-03"));

        Map<String, List<ImageItem>> groupedMap = ImageGrouping.groupByDate(imageList);
        List<DateGroup> dateGroups = new ArrayList<>();
        for (String date : groupedMap.keySet()) {
            dateGroups.add(new DateGroup(date, groupedMap.get(date)));
        }

        categoryImages.put("All", dateGroups);
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add new category");

        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Ok", (dialog, which) -> {
            String newCategory = input.getText().toString();
            if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
                categories.add(newCategory);
                spinnerAdapter.notifyDataSetChanged();
                saveCategoriesToPreferences();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void removeCategory() {
        String currentCategory = categorySpinner.getSelectedItem().toString();

        if (currentCategory.equals("All")) {
            Toast.makeText(requireContext(), "Cannot delete category 'All'", Toast.LENGTH_SHORT).show();
            return;
        }

        categories.remove(currentCategory);
        categoryImages.remove(currentCategory);

        spinnerAdapter.notifyDataSetChanged();
        saveCategoriesToPreferences();

        Toast.makeText(requireContext(), "Deleted category: " + currentCategory, Toast.LENGTH_SHORT).show();
    }

    private void saveCategoriesToPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Set<String> categorySet = new HashSet<>(categories);
        editor.putStringSet(KEY_CATEGORIES, categorySet);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}