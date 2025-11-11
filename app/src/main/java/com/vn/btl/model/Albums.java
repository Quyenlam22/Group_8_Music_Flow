package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Albums {
    @PrimaryKey
    @NonNull
    private long id;
    //picture
    @SerializedName("cover_medium")
    private String cover;
    private String title;
    private String artistName;
    @Ignore
    @SerializedName("artist")
    private Artist artist;
    public void normalize() {
        if (artist != null) artistName = artist.getArtistName();
    }

    public Albums() {
    }

    public Albums(long id, String cover, String title) {
        this.id = id;
        this.cover = cover;
        this.title = title;
    }

    public Albums(long id, String cover, String title, String artistName, Artist artist) {
        this.id = id;
        this.cover = cover;
        this.title = title;
        this.artistName = artistName;
        this.artist = artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
