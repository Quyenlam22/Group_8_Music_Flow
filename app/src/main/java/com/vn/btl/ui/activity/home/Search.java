package com.vn.btl.ui.activity.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.model.track.Tracks;
import com.vn.btl.model.track.TracksResponse;
import com.vn.btl.api.versionone.ApiService;
import com.vn.btl.api.versionone.RetrofitClient;
import com.vn.btl.ui.activity.track.NowPlayingActivity;
import com.vn.btl.ui.activity.setting.SettingsActivity;
import com.vn.btl.ui.adapter.track.TrackAdapter;
import com.vn.btl.utils.LanguageManager;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Search extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    // Giả định các resource ảnh và dữ liệu bài hát tĩnh
    private final String TITLE_1 = "Available (Nature Visual)";
    private final String ARTIST_1 = "Justin Bieber";
    private final int COVER_1 = R.drawable.search_placeholder1;
    private List<Tracks> songList = new ArrayList<>();
    private TrackAdapter trackAdapter;
    private ApiService apiService;
    LinearLayout viewTrending;
    RecyclerView rvSearch;
    SearchView edSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        edSearch = findViewById(R.id.etSearchInput);
        viewTrending = findViewById(R.id.view_trending);
        rvSearch = findViewById(R.id.rv_search);
        rvSearch.setLayoutManager(new LinearLayoutManager(this));

        trackAdapter = new TrackAdapter(songList, this);
        rvSearch.setAdapter(trackAdapter);
        apiService = RetrofitClient.getApiService();

        setupSearchViews();
        setupBackButton();
        setupTrendingItemClicks();
    }
    private void updateTexts() {
        SharedPreferences sp = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        String lang = sp.getString(SettingsActivity.K_LANG, LanguageManager.LANG_EN);

        TextView tvTrending = findViewById(R.id.tvTrendingSearch);
        if (tvTrending != null) {
            tvTrending.setText(LanguageManager.getText("label_trending_search", lang));
        }
    }

    private void setupSearchViews() {
        ImageView btnVoice = findViewById(R.id.btnVoiceSearch);

        if (btnVoice != null) {
            btnVoice.setOnClickListener(v -> {
                Log.d(TAG, "Voice Search clicked. Ready for implementation.");
            });
        }
        int id = edSearch.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText txt = edSearch.findViewById(id);
        txt.setHintTextColor(Color.BLACK);
        if (txt != null) {
            txt.setTextColor(Color.BLACK);
        }

        edSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) searchTracks(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    viewTrending.setVisibility(View.VISIBLE);
                    rvSearch.setVisibility(View.GONE);
                } else {
                    viewTrending.setVisibility(View.GONE);
                    rvSearch.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
    }

    private void searchTracks(String query) {
        apiService.searchTracks(query).enqueue(new Callback<TracksResponse>() {
            @Override
            public void onResponse(Call<TracksResponse> call, Response<TracksResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    songList.clear();
                    List<Tracks> tracks = response.body().getData();
                    if (tracks != null) {
                        for (Tracks t : tracks) t.normalize();
                        songList.addAll(tracks);
                    }
                    trackAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(Search.this, "Không tìm thấy bài hát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TracksResponse> call, Throwable t) {
                Toast.makeText(Search.this,"Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
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
    @Override
    protected void onResume() {
        super.onResume();
        updateTexts();
    }
}