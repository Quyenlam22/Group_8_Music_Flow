package com.vn.btl.setupapi;
import com.vn.btl.repository.ArtistResponse;
import com.vn.btl.repository.TracksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/musicflow/artists/random")
    Call<ArtistResponse> getRandomArtists();
    @GET("api/musicflow/search/artists")
    Call<ArtistResponse> searchArtists(@Query("q") String query);
    @GET("api/musicflow/artist/{id}/top")
    Call<TracksResponse> getTopTracksByArtist(@Path("id") long artistId);
}
