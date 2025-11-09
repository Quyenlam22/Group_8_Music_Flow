package com.vn.btl.repository;

import com.google.gson.annotations.SerializedName;
import com.vn.btl.model.Artist;

import java.util.List;

public class ArtistResponse {
    @SerializedName("artists")
    private List<Artist> data;

    public List<Artist> getData() {
        return data;
    }
}
