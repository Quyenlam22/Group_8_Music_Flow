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
import com.vn.btl.database.AppDatabase;
import com.vn.btl.model.Albums;
import com.vn.btl.model.AlbumsResponse;
import com.vn.btl.model.Artist;
import com.vn.btl.model.Tracks;
import com.vn.btl.repository.TracksResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.AlbumsAdapterVer2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumsFragment extends Fragment {
    private AlbumsAdapterVer2 adapter;
    private List<Albums> albumList = new ArrayList<>();
    private ApiService apiService;
    private AppDatabase db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.rv_albums);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        adapter = new AlbumsAdapterVer2(albumList,requireContext());
        rv.setAdapter(adapter);

        db = AppDatabase.getInstance(requireContext());
        apiService = RetrofitClient.getApiService();

        loadRecommendAlbums();
    }

    private void loadRecommendAlbums() {
        new Thread(() -> {
        List<Artist> savedArtists = db.artistDAO().getAll();

        List<Albums> allAlbums = Collections.synchronizedList(new ArrayList<>());
        for (Artist artist : savedArtists) {
            Log.d("DB_ARTIST", "ID: " + artist.getArtistId());
            apiService.getAlbumsByArtist(artist.getArtistId()).enqueue(new Callback<AlbumsResponse>() {
                @Override
                public void onResponse(Call<AlbumsResponse> call, Response<AlbumsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Albums> alb = response.body().getData();
                        if (alb != null) {
                            for (Albums item : alb) {
                                item.normalize(); // lấy artistName nếu có
                                item.setArtistName(artist.getArtistName()); // override bằng artist hiện tại
                                allAlbums.add(item);
                            }
                            // Cập nhật adapter sau khi có dữ liệu
                            requireActivity().runOnUiThread(() -> {
                                albumList.clear();
                                albumList.addAll(allAlbums);
                                adapter.notifyDataSetChanged();
                            });
                        } else {
                            Log.e("API_ERROR", "Album list is null for artist " + artist.getArtistName());
                        }
                    } else {
                        Log.e("API_ERROR", "Response unsuccessful or empty body");
                    }
                }

                @Override
                public void onFailure(Call<AlbumsResponse> call, Throwable t) {
                    Log.e("API_ERROR", "Failed to fetch tracks: " + t.getMessage());
                }
            });
        }
        }).start();
    }
}
