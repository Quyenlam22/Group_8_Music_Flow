package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Track {
    @PrimaryKey
    @NonNull
    private long id;
    private String title;
    private String preview;
    private String artistName;
    private String albumCover;
    @Ignore
    @SerializedName("artist")
    private Artist artist;
    @Ignore
    @SerializedName("album")
    private Album album;

    public void normalize() {
        if (artist != null) artistName = artist.getArtistName();
        if (album != null) albumCover = album.getCover();
    }

    public Track(long id, String title, String preview, String artistName, String albumCover, Artist artist, Album album) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.artistName = artistName;
        this.albumCover = albumCover;
        this.artist = artist;
        this.album = album;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
