package com.vn.btl.ui.activity;

public class UiSong {
    private String title;
    private String artist;
    private String coverUrl;
    private String previewUrl; // thêm trường này

    public UiSong(String title, String artist, String coverUrl, String previewUrl) {
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
        this.previewUrl = previewUrl;
    }

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getCoverUrl() { return coverUrl; }
    public String getPreviewUrl() { return previewUrl; }
}

