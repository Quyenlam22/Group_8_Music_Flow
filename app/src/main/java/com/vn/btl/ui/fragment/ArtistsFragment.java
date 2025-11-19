package com.vn.btl.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.model.Artist;
import com.vn.btl.repository.ArtistResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.ArtistAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistsFragment extends Fragment {

    private ArtistAdapter adapter;
    private final List<Artist> artistList = new ArrayList<>();
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root,
                              @Nullable Bundle savedInstanceState) {

        RecyclerView rv = root.findViewById(R.id.rv_artists);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        adapter = new ArtistAdapter(artistList, requireContext());
        rv.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        loadRecommendArtist();
    }

    private void loadRecommendArtist() {

        apiService.getRandomArtists().enqueue(new Callback<ArtistResponse>() {
            @Override
            public void onResponse(Call<ArtistResponse> call, Response<ArtistResponse> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("API_ERROR", "Response NULL or failed");
                    return;
                }

                ArtistResponse artistResponse = response.body();

                // Lấy danh sách đúng theo ArtistResponse
                if (artistResponse.getArtists() != null) {
                    artistList.clear();
                    artistList.addAll(artistResponse.getArtists());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "artistResponse.getArtists() == null");
                }
            }

            @Override
            public void onFailure(Call<ArtistResponse> call, Throwable t) {
                Log.e("API_ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}
