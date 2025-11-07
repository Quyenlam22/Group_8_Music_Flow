package com.vn.btl.setupapi;
import com.vn.btl.model.Artist;
import com.vn.btl.repository.ArtistResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;
public interface ApiService {
    @GET("api/musicflow/artists/random")
    Call<ArtistResponse> getRandomArtists();
}
