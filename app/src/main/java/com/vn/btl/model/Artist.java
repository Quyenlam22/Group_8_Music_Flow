package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "artists")
public class Artist {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private long artistId;

    @SerializedName("name")
    private String name;

    @SerializedName("picture")
    private String picture;

    private boolean selected;

    // ----- Getter & Setter -----
    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    // Giữ tương thích với code cũ
    public String getArtistName() {
        return name;
    }

    public void setArtistName(String artistName) {
        this.name = artistName;
    }
}
