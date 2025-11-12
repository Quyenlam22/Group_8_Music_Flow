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
import com.vn.btl.repository.PlaylistResponse;
import com.vn.btl.model.Playlists;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.PlaylistsAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistsFragment extends Fragment {
    private PlaylistsAdapter adapter;
    private List<Playlists> playlists = new ArrayList<>();
    private ApiService apiService;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.rv_playlists);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        adapter = new PlaylistsAdapter(playlists,requireContext());
        rv.setAdapter(adapter);

        apiService = RetrofitClient.getApiService();

        loadTopWorldPlaylist();
    }

    private void loadTopWorldPlaylist() {
        apiService.getPlaylists().enqueue(new Callback<PlaylistResponse>() {
            @Override
            public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    playlists.clear();
                    playlists.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
                else {
                    Log.e("API_ERROR", "Response unsuccessful or empty body"+ response.code());
                }
            }

            @Override
            public void onFailure(Call<PlaylistResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to load playlists: " + t.getMessage());
            }
        });
    }
}
