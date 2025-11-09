package com.vn.btl.repository;

import com.google.gson.annotations.SerializedName;
import com.vn.btl.model.Song;
import com.vn.btl.model.Track;

import java.util.List;

public class TrackResponse {
    @SerializedName("data")
    private List<Track> data;
    public List<Track> getData(){
        return data;
    }
    public void setData(List<Track> data) {
        this.data = data;
    }
}
