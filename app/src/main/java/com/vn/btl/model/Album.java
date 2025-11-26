package com.vn.btl.model;

public class Album {
    private long id;
    private String title;
    private String cover_medium;
    private String cover_big; // thêm từ nhánh now-playing
    private Artist artist;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getCover_medium() { return cover_medium; }
    public String getCover_big() { return cover_big; } // getter mới
    public Artist getArtist() { return artist; }

    public static class Artist {
        private String name;
        public String getName() { return name; }
    }
}
