package com.vn.btl.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {

    @SerializedName("data")
    private List<Track> data;

    @SerializedName("total")
    private int total;

    @SerializedName("next")
    private String next;

    // Constructor
    public SearchResponse() {
    }

    public SearchResponse(List<Track> data, int total, String next) {
        this.data = data;
        this.total = total;
        this.next = next;
    }

    // Getters and Setters
    public List<Track> getData() {
        return data;
    }

    public void setData(List<Track> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    // Helper methods
    public boolean hasNext() {
        return next != null && !next.isEmpty();
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    public int getCount() {
        return data != null ? data.size() : 0;
    }
}