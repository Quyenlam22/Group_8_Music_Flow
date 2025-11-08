package com.vn.btl.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;

public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = "PlaylistActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Chỉ cần set layout, chưa cần xử lý gì thêm
        String source = getIntent().getStringExtra("PLAYLIST_SOURCE");
        if (source != null) {
            Log.d(TAG, "Đã nhận yêu cầu xem: " + source);
        }

        setupBasicViews();
        setupBackButton();
    }

    private void setupBasicViews() {
        RecyclerView rvSongs = findViewById(R.id.rvSongs);
        RecyclerView rvArtists = findViewById(R.id.rvArtists);

        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupBackButton() {
        ImageButton btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        } else {
            Log.e(TAG, "Lỗi: Không tìm thấy nút quay lại với ID R.id.btnBack. Vui lòng kiểm tra activity_playlist.xml.");
        }
    }
}