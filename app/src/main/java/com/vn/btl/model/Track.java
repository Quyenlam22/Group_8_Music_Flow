package com.vn.btl.model;

public class Track {
    private long id;
    private String title;
    private Artist artist;
    private Album album;

    public long getId() { return id; }
    public String getTitle() { return title; }
    public Artist getArtist() { return artist; }
    public Album getAlbum() { return album; }

    public static class Artist {
        private String name;
        public String getName() { return name; }
    }


}
