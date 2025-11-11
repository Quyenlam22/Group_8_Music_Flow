package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vn.btl.R;
import com.vn.btl.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAlbums, rvPopular;
    private ViewPager2 vpBanner;
    private LinearLayout indicatorContainer;
    private BannerAdapter bannerAdapter;
    private final List<Integer> bannerImages = new ArrayList<>();
    private HomeViewModel viewModel;
    private static final String TAG = "FirebaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        checkFirebaseLogin();

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadTopTracks();
        viewModel.loadTopAlbums();

        setupHeader();
        setupCarousel();
        setupLists();
    }

    private void checkFirebaseLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "Đã đăng nhập Firebase với UID: " + currentUser.getUid());
            return;
        }

        auth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "✅ Kết nối Firebase thành công! UID: " + user.getUid());
                    } else {
                        Log.e(TAG, "❌ Lỗi khi đăng nhập Firebase: ", task.getException());
                    }
                });
    }

    private void setupHeader() {
        ImageView btnMenu = findViewById(R.id.btnMenu);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        btnMenu.setOnClickListener(v -> { /* TODO: open drawer */ });
        btnSearch.setOnClickListener(v -> openSearchActivity());
    }

    private void setupCarousel() {
        vpBanner = findViewById(R.id.vpBanner);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        // Banner placeholder
        bannerImages.clear();
        bannerImages.add(R.drawable.mf_banner_placeholder);
        bannerImages.add(R.drawable.mf_banner_placeholder);
        bannerImages.add(R.drawable.mf_banner_placeholder);

        bannerAdapter = new BannerAdapter(bannerImages);
        vpBanner.setAdapter(bannerAdapter);

        buildIndicators(bannerImages.size());
        setCurrentIndicator(0);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        ImageView fab = findViewById(R.id.fabPlay);
        fab.setOnClickListener(v -> {
            int page = vpBanner.getCurrentItem();
            String title = "RASAKING (Discover Music)";
            String artist = "Drake";
            String coverUrl = ""; // Nếu bạn muốn load từ URL banner, gán URL vào đây
            openNowPlaying(title, artist, coverUrl,"");
        });
    }

    private void buildIndicators(int count) {
        indicatorContainer.removeAllViews();
        int margin = (int) (getResources().getDisplayMetrics().density * 4);

        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageResource(R.drawable.mf_indicator_inactive);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(lp);
            indicatorContainer.addView(dot);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = indicatorContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dot = (ImageView) indicatorContainer.getChildAt(i);
            dot.setImageResource(i == index ? R.drawable.mf_indicator_active : R.drawable.mf_indicator_inactive);
        }
    }

    private void setupLists() {
        rvAlbums = findViewById(R.id.rvAlbums);
        rvPopular = findViewById(R.id.rvPopular);

        rvAlbums.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvPopular.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        int gap = getResources().getDimensionPixelSize(R.dimen.mf_item_gap);
        rvAlbums.addItemDecoration(new SpaceItemDecoration(gap));
        rvPopular.addItemDecoration(new SpaceItemDecoration(gap));

        // Observe Top Tracks từ API
        viewModel.getTopTracks().observe(this, response -> {
            if (response != null && response.getData() != null) {
                List<UiSong> songs = new ArrayList<>();
                response.getData().forEach(track ->
                        songs.add(new UiSong(
                                track.getTitle(),
                                track.getArtist().getName(),
                                track.getAlbum().getCover_medium(),
                                track.getPreview()
                        ))
                );
                SongsAdapter adapter = new SongsAdapter(this, songs, song -> {
                    openNowPlaying(song.getTitle(), song.getArtist(), song.getCoverUrl(), song.getPreviewUrl());
                });

                rvPopular.setAdapter(adapter);

            } else {
                Log.d(TAG, "Không có dữ liệu Top Tracks từ API");
            }
        });

        // Observe Top Albums từ API
        viewModel.getTopAlbums().observe(this, response -> {
            if (response != null && response.getData() != null) {
                List<UiAlbum> albums = new ArrayList<>();
                response.getData().forEach(album ->
                        albums.add(new UiAlbum(
                                album.getTitle(),
                                album.getArtist().getName(),
                                album.getCover_medium() // URL từ API
                        ))
                );
                rvAlbums.setAdapter(new AlbumsAdapter(albums));
            } else {
                Log.d(TAG, "Không có dữ liệu Top Albums từ API");
            }
        });

        // Nút xem tất cả
        TextView tvSeeAllAlbums = findViewById(R.id.tvSeeAllAlbums);
        TextView tvSeeAllPopular = findViewById(R.id.tvSeeAllPopular);

        if (tvSeeAllAlbums != null) {
            tvSeeAllAlbums.setOnClickListener(v -> openPlaylistActivity("Albums"));
        }
        if (tvSeeAllPopular != null) {
            tvSeeAllPopular.setOnClickListener(v -> openPlaylistActivity("Popular Songs"));
        }
    }

    private void openPlaylistActivity(String source) {
        Intent intent = new Intent(this, PlaylistActivity.class);
        intent.putExtra("PLAYLIST_SOURCE", source);
        startActivity(intent);
    }

    private void openNowPlaying(String title, String artist, String coverUrl,String previewUrl) {
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", title);
        intent.putExtra("ARTIST_NAME", artist);
        intent.putExtra("ALBUM_ART_URL", coverUrl); // truyền URL thay vì coverRes
        intent.putExtra("PREVIEW_URL", previewUrl);
        startActivity(intent);
    }

    private void openSearchActivity() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
}
