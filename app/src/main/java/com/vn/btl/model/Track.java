package com.vn.btl.model;

public class Track {
    private long id;
    private String title;
    private String title_short;
    private int duration;
    private String preview;
    private String md5_image;
    private Artist artist;
    private Album album;

    public Track() {}

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTitle_short() { return title_short; }
    public void setTitle_short(String title_short) { this.title_short = title_short; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public String getPreview() { return preview; }
    public void setPreview(String preview) { this.preview = preview; }
    public String getMd5_image() { return md5_image; }
    public void setMd5_image(String md5_image) { this.md5_image = md5_image; }
    public Artist getArtist() { return artist; }
    public void setArtist(Artist artist) { this.artist = artist; }
    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    // Lấy ảnh từ album (ưu tiên) hoặc artist
    public String getImageUrl() {
        if (album != null && album.getCover_medium() != null) {
            return album.getCover_medium();
        } else if (artist != null && artist.getPicture_medium() != null) {
            return artist.getPicture_medium();
        }
        return null; // hoặc return URL ảnh mặc định
    }
}