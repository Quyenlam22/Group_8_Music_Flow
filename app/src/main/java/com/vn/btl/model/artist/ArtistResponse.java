package com.vn.btl.model.artist;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ArtistResponse {

    @SerializedName("artists")
    private List<Artist> artists;

    @SerializedName("artist_search")
    private List<Artist> artistSearch;

    public List<Artist> getArtists() {
        return artists;
    }

    public List<Artist> getArtistSearch() {
        return artistSearch;
    }
}
