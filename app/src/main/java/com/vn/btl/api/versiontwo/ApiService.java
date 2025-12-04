package com.vn.btl.api.versiontwo;

import com.vn.btl.model.something.DeezerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("search")
    Call<DeezerResponse> searchSong(@Query("q") String query);
}
