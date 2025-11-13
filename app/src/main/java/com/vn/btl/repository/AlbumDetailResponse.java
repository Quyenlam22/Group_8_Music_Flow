package com.vn.btl.repository;

import com.google.gson.annotations.SerializedName;

public class AlbumDetailResponse {
    private int nb_tracks;

    public int getNb_tracks() {
        return nb_tracks;
    }
    @SerializedName("tracks")
    private TracksResponse tracks;

    public TracksResponse getTracks() { return tracks; }
}
