package com.vn.btl.ui.activity;

public class UiAlbum {
    public String title;
    public String artist;
    public String coverUrl; // đổi từ int sang String

    public UiAlbum(String title, String artist, String coverUrl) {
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
    }
}
