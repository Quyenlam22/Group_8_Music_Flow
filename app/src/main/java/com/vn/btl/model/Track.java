package com.vn.btl.model;

public class Track {
    private long id;
    private String title;
    private String title_short;
    private String title_version;
    private String preview;
    private int duration;
    private int rank;
    private boolean explicit_lyrics;
    private Artist artist;
    private Album album;

    // Empty constructor
    public Track() {}

    // Constructor with parameters
    public Track(long id, String title, String preview, int duration, Artist artist, Album album) {
        this.id = id;
        this.title = title;
        this.preview = preview;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getTitleShort() { return title_short != null ? title_short : ""; }
    public void setTitleShort(String title_short) { this.title_short = title_short; }

    public String getTitleVersion() { return title_version != null ? title_version : ""; }
    public void setTitleVersion(String title_version) { this.title_version = title_version; }

    public String getPreview() { return preview != null ? preview : ""; }
    public void setPreview(String preview) { this.preview = preview; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public boolean isExplicitLyrics() { return explicit_lyrics; }
    public void setExplicitLyrics(boolean explicit_lyrics) { this.explicit_lyrics = explicit_lyrics; }

    public Artist getArtist() { return artist != null ? artist : new Artist(); }
    public void setArtist(Artist artist) { this.artist = artist; }

    public Album getAlbum() { return album != null ? album : new Album(); }
    public void setAlbum(Album album) { this.album = album; }

    // Utility methods
    public String getArtistName() {
        return artist != null && artist.getName() != null ? artist.getName() : "Unknown Artist";
    }

    public String getAlbumCover() {
        return album != null && album.getCoverMedium() != null ? album.getCoverMedium() : "";
    }

    public String getFormattedDuration() {
        if (duration <= 0) return "0:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getFullTitle() {
        if (title_version != null && !title_version.isEmpty()) {
            return title + " " + title_version;
        }
        return title != null ? title : "";
    }

    public boolean hasPreview() {
        return preview != null && !preview.isEmpty();
    }
}