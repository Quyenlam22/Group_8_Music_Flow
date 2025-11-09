package com.vn.btl.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.model.Artist;
import com.vn.btl.model.Song;
import com.vn.btl.model.Track;
import com.vn.btl.repository.TrackResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendSongsActivity extends AppCompatActivity {
    private RecyclerView recycler;
    private TrackAdapter adapter;
    private List<Track> songList = new ArrayList<>();
    private ApiService apiService;
    private AppDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_songs);
        recycler = findViewById(R.id.recyclerSongsRecommend);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TrackAdapter(songList);
        recycler.setAdapter(adapter);

        db = AppDatabase.getInstance(this);
        apiService = RetrofitClient.getApiService();

        loadRecommendedSongs();
    }

    private void loadRecommendedSongs() {
        new Thread(() -> {
            List<Artist> savedArtists = db.artistDAO().getAll();
            // Danh sách chứa toàn bộ track từ tất cả artist
            List<Track> allTracks = Collections.synchronizedList(new ArrayList<>());

            for (Artist artist : savedArtists) {
                Log.d("DB_ARTIST", "ID: " + artist.getArtistId());
                apiService.getTopTracksByArtist(artist.getArtistId())
                        .enqueue(new Callback<TrackResponse>() {
                            @Override
                            public void onResponse(Call<TrackResponse> call, Response<TrackResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    TrackResponse songResponse = response.body();
                                    List<Track> tracks = songResponse.getData();
                                    if (tracks != null) {
                                        for (Track t : tracks) t.normalize();
                                        allTracks.addAll(tracks);
                                    } else {
                                        Log.e("API_NULL", "Track list is null");
                                    }
                                }
                                // Khi đã load xong tất cả artist → cập nhật adapter
                                if (allTracks.size() > 0 && allTracks.size() >= savedArtists.size()) {
                                    runOnUiThread(() -> adapter.setData(new ArrayList<>(allTracks)));
                                }
                                else {
                                    Log.e("API_ERROR", "Response unsuccessful or empty body");
                                }
                            }

                            @Override
                            public void onFailure(Call<TrackResponse> call, Throwable t) {
                                Log.e("API_ERROR", "Failed to fetch tracks: " + t.getMessage());
                            }
                        });
            }
        }).start();
    }
}