package com.vn.btl.model;

import java.util.List;

public class PlaylistResponse {
    private List<Track> data;
    private int total;
    private String next;

    public List<Track> getData() { return data != null ? data : new java.util.ArrayList<>(); }
    public void setData(List<Track> data) { this.data = data; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public String getNext() { return next != null ? next : ""; }
    public void setNext(String next) { this.next = next; }

    public boolean hasNext() {
        return next != null && !next.isEmpty();
    }
}