package com.vn.btl.repository;

import com.google.gson.annotations.SerializedName;
import com.vn.btl.model.Tracks;

import java.util.List;

public class TracksResponse {
    @SerializedName("data")
    private List<Tracks> data;
    public List<Tracks> getData(){
        return data;
    }
    public void setData(List<Tracks> data) {
        this.data = data;
    }
}
