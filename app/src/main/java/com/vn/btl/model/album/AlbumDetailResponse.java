package com.vn.btl.model.album;

import com.google.gson.annotations.SerializedName;
import com.vn.btl.model.track.TracksResponse;

public class AlbumDetailResponse {
    private int nb_tracks;

    public int getNb_tracks() {
        return nb_tracks;
    }
    @SerializedName("tracks")
    private TracksResponse tracks;

    public TracksResponse getTracks() { return tracks; }
}
