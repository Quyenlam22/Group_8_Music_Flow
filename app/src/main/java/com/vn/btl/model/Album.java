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
    private String tracklist;
    private String type;

    // Empty constructor
    public Album() {}

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getCover() { return cover != null ? cover : ""; }
    public void setCover(String cover) { this.cover = cover; }

    public String getCoverSmall() { return cover_small != null ? cover_small : ""; }
    public void setCoverSmall(String cover_small) { this.cover_small = cover_small; }

    public String getCoverMedium() { return cover_medium != null ? cover_medium : ""; }
    public void setCoverMedium(String cover_medium) { this.cover_medium = cover_medium; }

    public String getCoverBig() { return cover_big != null ? cover_big : ""; }
    public void setCoverBig(String cover_big) { this.cover_big = cover_big; }

    public String getCoverXl() { return cover_xl != null ? cover_xl : ""; }
    public void setCoverXl(String cover_xl) { this.cover_xl = cover_xl; }

    public String getMd5Image() { return md5_image != null ? md5_image : ""; }
    public void setMd5Image(String md5_image) { this.md5_image = md5_image; }

    public String getTracklist() { return tracklist != null ? tracklist : ""; }
    public void setTracklist(String tracklist) { this.tracklist = tracklist; }

    public String getType() { return type != null ? type : ""; }
    public void setType(String type) { this.type = type; }

    // Utility methods
    public String getBestAvailableCover() {
        if (cover_medium != null && !cover_medium.isEmpty()) return cover_medium;
        if (cover_big != null && !cover_big.isEmpty()) return cover_big;
        if (cover_small != null && !cover_small.isEmpty()) return cover_small;
        if (cover_xl != null && !cover_xl.isEmpty()) return cover_xl;
        if (cover != null && !cover.isEmpty()) return cover;
        return "";
    }
}