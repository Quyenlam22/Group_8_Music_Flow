package com.vn.btl.ui.activity;

import android.os.Parcel;
import android.os.Parcelable;

public class UiSong implements Parcelable {
    private String title;
    private String artist;
    private String coverUrl;
    private String previewUrl;

    public UiSong(String title, String artist, String coverUrl, String previewUrl) {
        this.title = title;
        this.artist = artist;
        this.coverUrl = coverUrl;
        this.previewUrl = previewUrl;
    }

    protected UiSong(Parcel in) {
        title = in.readString();
        artist = in.readString();
        coverUrl = in.readString();
        previewUrl = in.readString();
    }

    public static final Creator<UiSong> CREATOR = new Creator<UiSong>() {
        @Override
        public UiSong createFromParcel(Parcel in) {
            return new UiSong(in);
        }

        @Override
        public UiSong[] newArray(int size) {
            return new UiSong[size];
        }
    };

    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public String getCoverUrl() { return coverUrl; }
    public String getPreviewUrl() { return previewUrl; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(artist);
        parcel.writeString(coverUrl);
        parcel.writeString(previewUrl);
    }
}
