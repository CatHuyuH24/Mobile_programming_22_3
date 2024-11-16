package com.example.mydemoapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mydemoapp.models.Album;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AlbumManager {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private static final String PREFS_NAME = "AlbumPrefs";
    private static final String ALBUMS_KEY = "albums";

    public AlbumManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    private void saveAlbums(List<Album> albums) {
        String json = gson.toJson(albums);
        sharedPreferences.edit().putString(ALBUMS_KEY, json).apply();
    }

    public List<Album> loadAlbums() {
        String json = sharedPreferences.getString(ALBUMS_KEY, null);

        if (json == null) {
            return new ArrayList<>();
        }

        return gson.fromJson(json, new TypeToken<List<Album>>() {
        }.getType());
    }

    public Album getAlbumByName(String name) {
        List<Album> albums = loadAlbums();

        for (Album album : albums) {
            if (album.getName().equals(name)) {
                return album;
            }
        }

        return null;
    }

    public void addAlbum(Album album) {
        List<Album> albums = loadAlbums();
        albums.add(album);
        saveAlbums(albums);
    }

    public void removeAlbum(String albumName) {
        List<Album> albums = loadAlbums();

        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                albums.remove(album);
                break;
            }
        }

        saveAlbums(albums);
    }
}
