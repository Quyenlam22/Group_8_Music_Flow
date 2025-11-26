package com.vn.btl.api;

import com.vn.btl.model.DeezerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search")
    Call<DeezerResponse> searchSong(@Query("q") String query);
}
