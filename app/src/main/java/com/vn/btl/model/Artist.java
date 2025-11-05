package com.vn.btl.model;

public class Artist {
    private long id;
    private String name;
    private String link;
    private String picture;
    private String picture_small;
    private String picture_medium;
    private String picture_big;
    private String picture_xl;
    private String tracklist;
    private String type;

    // Empty constructor
    public Artist() {}

    // Constructor with parameters
    public Artist(long id, String name, String picture_medium) {
        this.id = id;
        this.name = name;
        this.picture_medium = picture_medium;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name != null ? name : "Unknown Artist"; }
    public void setName(String name) { this.name = name; }

    public String getLink() { return link != null ? link : ""; }
    public void setLink(String link) { this.link = link; }

    public String getPicture() { return picture != null ? picture : ""; }
    public void setPicture(String picture) { this.picture = picture; }

    public String getPictureSmall() { return picture_small != null ? picture_small : ""; }
    public void setPictureSmall(String picture_small) { this.picture_small = picture_small; }

    public String getPictureMedium() { return picture_medium != null ? picture_medium : ""; }
    public void setPictureMedium(String picture_medium) { this.picture_medium = picture_medium; }

    public String getPictureBig() { return picture_big != null ? picture_big : ""; }
    public void setPictureBig(String picture_big) { this.picture_big = picture_big; }

    public String getPictureXl() { return picture_xl != null ? picture_xl : ""; }
    public void setPictureXl(String picture_xl) { this.picture_xl = picture_xl; }

    public String getTracklist() { return tracklist != null ? tracklist : ""; }
    public void setTracklist(String tracklist) { this.tracklist = tracklist; }

    public String getType() { return type != null ? type : ""; }
    public void setType(String type) { this.type = type; }

    // Utility methods
    public String getBestAvailablePicture() {
        if (picture_medium != null && !picture_medium.isEmpty()) return picture_medium;
        if (picture_big != null && !picture_big.isEmpty()) return picture_big;
        if (picture_small != null && !picture_small.isEmpty()) return picture_small;
        if (picture_xl != null && !picture_xl.isEmpty()) return picture_xl;
        if (picture != null && !picture.isEmpty()) return picture;
        return "";
    }

    public boolean hasPicture() {
        return (picture_medium != null && !picture_medium.isEmpty()) ||
                (picture_big != null && !picture_big.isEmpty()) ||
                (picture_small != null && !picture_small.isEmpty()) ||
                (picture_xl != null && !picture_xl.isEmpty()) ||
                (picture != null && !picture.isEmpty());
    }
}