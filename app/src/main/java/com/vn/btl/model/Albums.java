package com.vn.btl.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Albums {
    @PrimaryKey
    @NonNull
    private long id;
    //picture
    private String cover;
    private String title;

    public Albums(long id, String cover, String title) {
        this.id = id;
        this.cover = cover;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
