package com.vn.btl.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.vn.btl.model.AlbumResponse;
import com.vn.btl.api.MusicApi;
import com.vn.btl.api.RetrofitClient;
import com.vn.btl.model.TrackResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MusicRepository {
    private final MusicApi api;

    public MusicRepository() {
        api = RetrofitClient.getClient().create(MusicApi.class);
    }

    public LiveData<TrackResponse> getTopTracks(int limit) {
        MutableLiveData<TrackResponse> data = new MutableLiveData<>();
        api.getTopTracks(limit).enqueue(new Callback<TrackResponse>() {
            @Override
            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TrackResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
    // ✅ Lấy danh sách album phổ biến
    public LiveData<AlbumResponse> getTopAlbums(int limit) {
        MutableLiveData<AlbumResponse> data = new MutableLiveData<>();
        api.getTopAlbums(limit).enqueue(new Callback<AlbumResponse>() {
            @Override
            public void onResponse(Call<AlbumResponse> call, Response<AlbumResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<AlbumResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

}
