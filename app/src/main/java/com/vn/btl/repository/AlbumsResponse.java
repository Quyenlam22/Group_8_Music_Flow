package com.vn.btl.repository;

import com.google.gson.annotations.SerializedName;
import com.vn.btl.model.Albums;

import java.util.List;

public class AlbumsResponse {
    private List<Albums> data;

    public List<Albums> getData() {
        return data;
    }
}
