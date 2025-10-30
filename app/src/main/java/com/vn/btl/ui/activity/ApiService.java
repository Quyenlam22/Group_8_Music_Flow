package com.vn.btl.ui.activity;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/musicflow/search")
    Call<JsonObject> searchTracks(@Query("q") String query);
    @GET("api/musicflow/featured")
    Call<JsonObject> getFeatured();
    @GET("api/musicflow/albums/random")
    Call<JsonObject> getRandomAlbums();
    @GET("api/musicflow/album/{id}/tracks")
    Call<JsonObject> getAlbumTracks(@Path("id") String albumId);
}
