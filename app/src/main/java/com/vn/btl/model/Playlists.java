package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
@Entity
public class Playlists {
    @PrimaryKey
    @NonNull
    private long id;
    private String title;
    //picture
    @SerializedName("picture_medium")
    private String picture;
    private int nb_tracks;

    public Playlists() {
    }

    public Playlists(long id, String picture, String title, int nb_tracks) {
        this.id = id;
        this.picture = picture;
        this.title = title;
        this.nb_tracks = nb_tracks;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNb_tracks() {
        return nb_tracks;
    }

    public void setNb_tracks(int nb_tracks) {
        this.nb_tracks = nb_tracks;
    }
}
