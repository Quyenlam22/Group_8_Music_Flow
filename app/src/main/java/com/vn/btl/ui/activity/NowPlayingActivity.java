package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.vn.btl.R;
import android.util.Log;

public class NowPlayingActivity extends AppCompatActivity {
    private static final String TAG = "NowPlayingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giả định layout này được lưu trong res/layout/activity_now_playing.xml
        setContentView(R.layout.activity_now_playing);

        handleIntentData();
        setupBackButton();
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Đóng NowPlayingActivity
            });
        }
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Nhận dữ liệu bài hát được truyền từ Activity trước
            String songTitle = intent.getStringExtra("SONG_TITLE");
            String artistName = intent.getStringExtra("ARTIST_NAME");
            String albumArtUrl = intent.getStringExtra("ALBUM_ART_URL");

            TextView tvTitle = findViewById(R.id.tvSongTitle);
            TextView tvArtist = findViewById(R.id.tvArtist);
            ImageView imgAlbum = findViewById(R.id.imgAlbumCover);

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

            Log.d(TAG, "Đang phát: " + songTitle + " - " + artistName);
        }
    }
}