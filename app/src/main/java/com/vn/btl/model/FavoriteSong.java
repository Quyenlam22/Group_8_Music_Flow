package com.vn.btl.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_songs")
public class FavoriteSong {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String artist;
    private String coverUrl;
    private String previewUrl;


    private long addedTime;

    public FavoriteSong(String title, String artist, String coverUrl, String previewUrl, long addedTime) {
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
        this.previewUrl = previewUrl;
        this.addedTime = addedTime;

    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
    public long getAddedTime() { return addedTime; }
    public void setAddedTime(long addedTime) { this.addedTime = addedTime; }
}