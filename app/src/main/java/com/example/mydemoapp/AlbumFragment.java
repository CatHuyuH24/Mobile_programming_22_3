package com.example.mydemoapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import com.example.mydemoapp.databinding.FragmentAlbumBinding; // Import đúng binding class

public class AlbumFragment extends Fragment {
    private FragmentAlbumBinding binding; // Sửa thành FragmentAlbumBinding
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAlbumBinding.inflate(inflater, container, false);
        return binding.getRoot();}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}