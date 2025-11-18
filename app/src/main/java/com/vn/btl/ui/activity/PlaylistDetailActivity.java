package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.model.Artist;
import com.vn.btl.model.Tracks;
import com.vn.btl.repository.ArtistResponse;
import com.vn.btl.repository.TracksResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.ArtistAdapter;
import com.vn.btl.ui.adapter.TrackAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistDetailActivity extends AppCompatActivity {
    private TrackAdapter trackAdapter;
    private ArtistAdapter artistAdapter;
    private RecyclerView rvArtists;
    private RecyclerView rvSongs;
    private List<Tracks> trackList = new ArrayList<>();
    private List<Artist> artistList = new ArrayList<>();
    private ApiService apiService;
    private long playlistId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        rvSongs = findViewById(R.id.rvSongs);
        rvArtists = findViewById(R.id.rvArtists);

        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(trackList,this);
        rvSongs.setAdapter(trackAdapter);

        rvArtists.setLayoutManager(new GridLayoutManager(this, 3));
        artistAdapter = new ArtistAdapter(artistList,this);
        rvArtists.setAdapter(artistAdapter);

        apiService = RetrofitClient.getApiService();
        playlistId = getIntent().getLongExtra("PLAYLIST_ID", -1);
        Log.d("ID_ALBUM","ID: "+playlistId);
        if (playlistId != -1) loadTracksOfPlaylist(playlistId);
        setupBackButton();
        handleIntentData();
    }
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Nhận dữ liệu bài hát được truyền từ Activity trước
            String songTitle = intent.getStringExtra("PLAYLIST_TITLE");
            int trackCount = intent.getIntExtra("PLAYLIST_NB",-1);
            String albumArtUrl = intent.getStringExtra("IMAGE_PLAYLIST");

            TextView tvTitle = findViewById(R.id.tvPlaylistName);
            TextView tvTrackCount = findViewById(R.id.tvTrackCount);
            ImageView imgAlbum = findViewById(R.id.img_playlist);

            if (tvTitle != null && songTitle != null) {
                tvTitle.setText(songTitle);
            }
            if (trackCount != -1 && tvTrackCount != null) {
                tvTrackCount.setText(trackCount+" songs");
            }
            if (albumArtUrl != null && imgAlbum != null) {
                Glide.with(this)
                        .load(albumArtUrl)
                        .into(imgAlbum);
            }
        }
    }
    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        } else {
            Log.e("TAG", "Lỗi: Không tìm thấy nút quay lại với ID R.id.btnBack. Vui lòng kiểm tra activity_playlist.xml.");
        }
    }
    private void loadTracksOfPlaylist(long playlistId) {
        apiService.getPlaylistDetail(playlistId).enqueue(new Callback<TracksResponse>() {
            @Override
            public void onResponse(Call<TracksResponse> call, Response<TracksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tracks> tracks = response.body().getData();
                    if (tracks != null) {
                        trackList.clear();
                        for (Tracks t : tracks){
                            t.normalize();
                            if (t.getArtist() != null) {
                                artistList.add(t.getArtist());
                            }
                        }
                        trackList.addAll(tracks);
                        trackAdapter.notifyDataSetChanged();
                        artistAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API_ERROR", "Empty or failed response");
                }
            }

            @Override
            public void onFailure(Call<TracksResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
            }
        });
    }
}