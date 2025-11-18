package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class Tracks {
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
    private Albums albums;

    public void normalize() {
        if (artist != null) artistName = artist.getArtistName();
        if (albums != null) albumCover = albums.getCover();
    }

    public Tracks(long id, String title, String preview, String artistName, String albumCover, Artist artist, Albums albums) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.artistName = artistName;
        this.albumCover = albumCover;
        this.artist = artist;
        this.albums = albums;
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

    public Albums getAlbum() {
        return albums;
    }

    public void setAlbum(Albums albums) {
        this.albums = albums;
    }
}
