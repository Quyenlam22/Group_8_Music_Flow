package com.vn.btl.model;

public class Album {
    private long id;
    private String title;
    private String cover;
    private String cover_small;
    private String cover_medium;
    private String cover_big;
    private String cover_xl;
    private String md5_image;

    public Album() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }
    public String getCover_small() { return cover_small; }
    public void setCover_small(String cover_small) { this.cover_small = cover_small; }
    public String getCover_medium() { return cover_medium; }
    public void setCover_medium(String cover_medium) { this.cover_medium = cover_medium; }
    public String getCover_big() { return cover_big; }
    public void setCover_big(String cover_big) { this.cover_big = cover_big; }
    public String getCover_xl() { return cover_xl; }
    public void setCover_xl(String cover_xl) { this.cover_xl = cover_xl; }
    public String getMd5_image() { return md5_image; }
    public void setMd5_image(String md5_image) { this.md5_image = md5_image; }
}