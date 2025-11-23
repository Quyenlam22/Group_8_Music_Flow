package com.vn.btl.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.model.Artist;
import com.vn.btl.model.Tracks;
import com.vn.btl.repository.TracksResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.activity.SongsActivity;
import com.vn.btl.ui.activity.UiSong;
import com.vn.btl.ui.adapter.SongsAdapter;
import com.vn.btl.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllSongsFragment extends Fragment {
    private TrackAdapter adapter;
    private List<Tracks> songList = new ArrayList<>();
    private ApiService apiService;
    private AppDatabase db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recommend_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.recyclerSongsRecommend);
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rv.setHasFixedSize(true);

        // tránh bị BottomNav che
        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(0, 8, 0, bottomPad);

        adapter = new TrackAdapter(songList, requireContext());
        rv.setAdapter(adapter);

        db = AppDatabase.getInstance(requireContext());
        apiService = RetrofitClient.getApiService();

        loadRecommendedSongs();
    }
    private void loadRecommendedSongs() {
        new Thread(() -> {
            List<Artist> savedArtists = db.artistDAO().getAll();
            List<Tracks> allTracks = Collections.synchronizedList(new ArrayList<>());
            AtomicInteger pending = new AtomicInteger(savedArtists.size());

            for (Artist artist : savedArtists) {
                Log.d("DB_ARTIST", "ID: " + artist.getArtistId());

                apiService.getTopTracksByArtist(artist.getArtistId())
                        .enqueue(new Callback<TracksResponse>() {
                            @Override
                            public void onResponse(Call<TracksResponse> call, Response<TracksResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<Tracks> tracks = response.body().getData();
                                    if (tracks != null) {
                                        for (Tracks t : tracks) t.normalize();
                                        allTracks.addAll(tracks);
                                    }
                                    // Khi request cuối cùng xong thì update UI
                                    if (pending.decrementAndGet() == 0) {
                                        Activity activity = getActivity();
                                        if (activity != null && !activity.isDestroyed()) {
                                            activity.runOnUiThread(() ->
                                                    adapter.setData(new ArrayList<>(allTracks))
                                            );
                                        }
                                    }
                                } else {
                                    Log.e("API_ERROR", "Response unsuccessful or empty body");
                                }
                            }

                            @Override
                            public void onFailure(Call<TracksResponse> call, Throwable t) {
                                // Khi request cuối cùng xong thì update UI
                                if (pending.decrementAndGet() == 0) {
                                    Activity activity = getActivity();
                                    if (activity != null && !activity.isDestroyed()) {
                                        activity.runOnUiThread(() ->
                                                adapter.setData(new ArrayList<>(allTracks))
                                        );
                                    }
                                }
                                Log.e("API_ERROR", "Failed to fetch tracks: " + t.getMessage());
                            }
                        });
            }
        }).start();
    }
}
