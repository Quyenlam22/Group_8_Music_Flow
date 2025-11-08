package com.vn.btl.repository;

import com.vn.btl.model.SearchResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class SearchRepository {
    private static final String BASE_URL = "https://api.deezer.com/";
    private DeezerApi deezerApi;

    public SearchRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        deezerApi = retrofit.create(DeezerApi.class);
    }

    public Call<SearchResponse> searchTracks(String query) {
        return deezerApi.searchTracks(query);
    }

    public Call<SearchResponse> getTrendingTracks(String genre) {
        return deezerApi.searchTracks(genre);
    }

    private interface DeezerApi {
        @GET("search")
        Call<SearchResponse> searchTracks(@Query("q") String query);
    }
}