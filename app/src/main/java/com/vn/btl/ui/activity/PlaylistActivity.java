package com.vn.btl.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Chỉ cần set layout, chưa cần xử lý gì thêm
        setupBasicViews();
    }

    private void setupBasicViews() {
        // Chỉ setup RecyclerView cơ bản để không bị lỗi
        RecyclerView rvSongs = findViewById(R.id.rvSongs);
        RecyclerView rvArtists = findViewById(R.id.rvArtists);

        // Layout manager cơ bản
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // TODO: Sau này sẽ thêm Adapter và data từ API
        // rvSongs.setAdapter(songAdapter);
        // rvArtists.setAdapter(artistAdapter);
    }
}