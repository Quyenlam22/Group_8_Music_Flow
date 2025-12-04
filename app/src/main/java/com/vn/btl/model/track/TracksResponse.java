package com.vn.btl.model.track;

import com.google.gson.annotations.SerializedName;

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
