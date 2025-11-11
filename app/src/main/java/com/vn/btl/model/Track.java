package com.vn.btl.model;

public class Track {
    private long id;        // từ nhánh home
    private String title;
    private String preview; // từ nhánh now-playing
    private Artist artist;
    private Album album;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getPreview() { return preview; } // getter mới
    public Artist getArtist() { return artist; }
    public Album getAlbum() { return album; }

    public static class Artist {
        private String name;
        public String getName() { return name; }
    }
}
