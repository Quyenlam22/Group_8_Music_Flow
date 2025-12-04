package com.vn.btl.model.something;

import com.vn.btl.model.track.Track;

import java.util.List;

public class DeezerResponse {
    private List<Track> data;

    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }
}
