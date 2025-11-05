package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.vn.btl.R;

public class Search extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    // Giả định các resource ảnh và dữ liệu bài hát tĩnh
    private final String TITLE_1 = "Available (Nature Visual)";
    private final String ARTIST_1 = "Justin Bieber";
    private final int COVER_1 = R.drawable.search_placeholder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupSearchViews();
        setupBackButton();
        setupTrendingItemClicks();
    }

    private void setupSearchViews() {
        ImageView btnVoice = findViewById(R.id.btnVoiceSearch);

        if (btnVoice != null) {
            btnVoice.setOnClickListener(v -> {
                Log.d(TAG, "Voice Search clicked. Ready for implementation.");
            });
        }
    }

    private void setupBackButton() {
        // Nút quay lại (biểu tượng tìm kiếm đầu tiên trong Search Bar)
        ImageView btnBack = findViewById(R.id.btnBackSearch);

        if (btnBack != null) {
            // Khi nhấn, đóng Activity hiện tại
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupTrendingItemClicks() {
        // Khởi tạo và gán sự kiện cho từng item (YÊU CẦU ID TRONG XML)

        // Item 1
        findViewById(R.id.itemTrending1).setOnClickListener(v -> {
            openNowPlaying(TITLE_1, ARTIST_1, COVER_1);
        });

        // Item 2
        findViewById(R.id.itemTrending2).setOnClickListener(v -> {
            openNowPlaying("GOOODA (Official Music)", "BASIQUE", R.drawable.search_placeholder2);
        });

        // Item 3
        findViewById(R.id.itemTrending3).setOnClickListener(v -> {
            openNowPlaying("Best Music 2020 | I New Popular Songs", "DMinet Music", R.drawable.search_placeholder3);
        });

        // Item 4
        findViewById(R.id.itemTrending4).setOnClickListener(v -> {
            openNowPlaying("On Ma Ma Ma English New Song 2019", "DMinet Music", R.drawable.search_placeholder4);
        });

        // Item 5
        findViewById(R.id.itemTrending5).setOnClickListener(v -> {
            openNowPlaying("Memories", "DMinet Music", R.drawable.search_placeholder5);
        });
    }

    private void openNowPlaying(String title, String artist, int coverResId) {
        Intent intent = new Intent(Search.this, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", title);
        intent.putExtra("ARTIST_NAME", artist);
        intent.putExtra("ALBUM_ART_RES_ID", coverResId);
        startActivity(intent);
    }
}