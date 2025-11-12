package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.btl.R;
import com.vn.btl.model.Tracks;
import com.vn.btl.repository.AlbumDetailResponse;
import com.vn.btl.setupapi.ApiService;
import com.vn.btl.setupapi.RetrofitClient;
import com.vn.btl.ui.adapter.TrackAdapter;
import com.vn.btl.utils.BottomNavHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDetailActivity extends AppCompatActivity {
    private TrackAdapter trackAdapter;
    private RecyclerView rvTracks;
    private List<Tracks> trackList = new ArrayList<>();
    private ApiService apiService;
    int count = 0;
    TextView tvCount;
    ImageView btnSearch;
    private long albumId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        rvTracks = findViewById(R.id.rv_album_detail);
        tvCount = findViewById(R.id.tv_count);
        btnSearch = findViewById(R.id.btnSearchDetail);

        rvTracks.setLayoutManager(new LinearLayoutManager(this));
        trackAdapter = new TrackAdapter(trackList,this);
        rvTracks.setAdapter(trackAdapter);

        apiService = RetrofitClient.getApiService();
        albumId = getIntent().getLongExtra("ALBUM_ID", -1);
        Log.d("ID_ALBUM","ID: "+albumId);
        if (albumId != -1) loadTracksOfAlbum(albumId);

        handleIntentData();
        setupBackButton();
        btnSearch.setOnClickListener(v -> openSearchActivity());
        // chỉ dùng 1 listener của helper
        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_song);
    }
    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Nhận dữ liệu bài hát được truyền từ Activity trước
            String songTitle = intent.getStringExtra("TITLE_ALBUM");
            String artistName = intent.getStringExtra("ARTIST_NAME");
            String albumArtUrl = intent.getStringExtra("IMAGE_ALBUM");

            TextView tvTitle = findViewById(R.id.tv_title_album);
            TextView tvArtist = findViewById(R.id.tv_artist_name);
            ImageView imgAlbum = findViewById(R.id.img_album_details);

            if (tvTitle != null && songTitle != null) {
                tvTitle.setText(songTitle);
            }
            if (tvArtist != null && artistName != null) {
                tvArtist.setText(artistName);
            }
            if (albumArtUrl != null && imgAlbum != null) {
                Glide.with(this)
                        .load(albumArtUrl)
                        .into(imgAlbum);
            }
        }
    }
    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBackAlbum);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Đóng NowPlayingActivity
            });
        }
    }
    private void openSearchActivity() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
    private void loadTracksOfAlbum(long id) {
        apiService.getTracksOfAlbum(id).enqueue(new Callback<AlbumDetailResponse>() {
            @Override
            public void onResponse(Call<AlbumDetailResponse> call, Response<AlbumDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tracks> tracks = response.body().getTracks().getData();
                    count = response.body().getNb_tracks();
                    tvCount.setText(count+" songs");
                    if (tracks != null) {
                        trackList.clear();
                        for (Tracks t : tracks) t.normalize();
                        trackList.addAll(tracks);
                        trackAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.e("API_ERROR", "Empty or failed response");
                }
            }

            @Override
            public void onFailure(Call<AlbumDetailResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
            }
        });
    }
}