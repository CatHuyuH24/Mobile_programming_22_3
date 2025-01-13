package com.example.mydemoapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mydemoapp.models.Album;
import com.example.mydemoapp.models.ImageItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AlbumManager {
    private static final String PREFS_NAME = "AlbumPrefs";
    private static final String ALBUMS_KEY = "albums";
    private SharedPreferences sharedPreferences;
    private Gson gson;

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

    public void addImageToAlbum(String albumName, String imagePath) {
        List<Album> albums = loadAlbums();

        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                for (ImageItem image : album.getImages()) {
                    if (image.getImagePath().equals(imagePath)) {
                        throw new IllegalArgumentException("Image already exists in the album");
                    }
                }

                long dateTaken = getDateTaken(imagePath);
                album.addImage(new ImageItem(imagePath, dateTaken));
                break;
            }
        }

        saveAlbums(albums);
    }

    private long getDateTaken(String imagePath) {
        Album albumAll = getAlbumByName("All");

        for (ImageItem image : albumAll.getImages()) {
            if (image.getImagePath().equals(imagePath)) {
                return image.getDateTaken();
            }
        }

        return 0;
    }

    public void removeImageFromAlbum(String albumName, String imagePath) {
        List<Album> albums = loadAlbums();

        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                for (ImageItem image : album.getImages()) {
                    if (image.getImagePath().equals(imagePath)) {
                        album.removeImage(image);
                        break;
                    }
                }

                break;
            }
        }

        saveAlbums(albums);
    }

    public List<String> getAlbumNames(String imagePath) {
        List<Album> albums = loadAlbums();
        List<String> albumNames = new ArrayList<>();

        for (Album album : albums) {
            if (album.getName().equals("All")) {
                continue;
            }

            for (ImageItem image : album.getImages()) {
                if (image.getImagePath().equals(imagePath)) {
                    albumNames.add(album.getName());
                    break;
                }
            }
        }

        return albumNames;
    }
}
