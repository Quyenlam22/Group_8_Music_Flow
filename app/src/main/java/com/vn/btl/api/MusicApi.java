package com.vn.btl.api;

import com.vn.btl.model.TrackResponse;
import retrofit2.Call;
import com.vn.btl.model.AlbumResponse;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicApi {
    // API Deezer: https://api.deezer.com/chart/0/tracks
    @GET("chart/0/tracks")
    Call<TrackResponse> getTopTracks(@Query("limit") int limit);

    @GET("chart/0/albums")
    Call<AlbumResponse> getTopAlbums(@Query("limit") int limit);
}
