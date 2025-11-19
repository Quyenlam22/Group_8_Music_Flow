package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.AlbumsAdapter;
import com.vn.btl.ui.adapter.SongsAdapter;
import com.vn.btl.ui.viewmodel.HomeViewModel;
import com.vn.btl.utils.BottomNavHelper;
import com.vn.btl.utils.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAlbums, rvPopular;
    private ViewPager2 vpBanner;
    private LinearLayout indicatorContainer;
    private BannerAdapter bannerAdapter;
    private final List<UiSong> bannerTracks = new ArrayList<>();
    private HomeViewModel viewModel;

    private final Handler autoSlideHandler = new Handler();
    private final Runnable autoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            if (!bannerTracks.isEmpty()) {
                int next = (vpBanner.getCurrentItem() + 1) % bannerTracks.size();
                vpBanner.setCurrentItem(next, true);
                autoSlideHandler.postDelayed(this, 5000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadTopTracks();
        viewModel.loadTopAlbums();

        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_home);

        setupHeader();
        setupBanner();
        setupLists();
    }

    private void setupHeader() {
        ImageView btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> startActivity(new Intent(this, Search.class)));
        }
    }

    private void setupBanner() {
        vpBanner = findViewById(R.id.vpBanner);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        bannerAdapter = new BannerAdapter(bannerTracks, song -> openNowPlaying(song));
        vpBanner.setAdapter(bannerAdapter);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabPlay);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                int pos = vpBanner.getCurrentItem();
                if (!bannerTracks.isEmpty()) {
                    openNowPlaying(bannerTracks.get(pos));
                }
            });
        }

        viewModel.getTopTracks().observe(this, response -> {
            if (response != null && response.getData() != null) {
                bannerTracks.clear();
                int limit = Math.min(5, response.getData().size());
                response.getData().subList(0, limit).forEach(track -> {
                    bannerTracks.add(new UiSong(
                            safe(track.getTitle()),
                            track.getArtist() != null ? safe(track.getArtist().getName()) : "",
                            track.getAlbum() != null ? safe(track.getAlbum().getCover_big()) : "",
                            safe(track.getPreview())
                    ));
                });
                bannerAdapter.notifyDataSetChanged();
                buildIndicators(bannerTracks.size());
                setCurrentIndicator(0);
                autoSlideHandler.postDelayed(autoSlideRunnable, 5000);
            }
        });
    }

    private void setupLists() {
        rvAlbums = findViewById(R.id.rvAlbums);
        rvPopular = findViewById(R.id.rvPopular);

        rvAlbums.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvPopular.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));


        int albumGap = getResources().getDimensionPixelSize(R.dimen.mf_album_gap);
        rvAlbums.addItemDecoration(new SpaceItemDecoration(albumGap));


        int popularGap = getResources().getDimensionPixelSize(R.dimen.mf_popular_gap);
        rvPopular.addItemDecoration(new SpaceItemDecoration(popularGap));

        // Popular songs
        viewModel.getTopTracks().observe(this, response -> {
            if (response != null && response.getData() != null) {
                List<UiSong> list = new ArrayList<>();
                response.getData().forEach(track -> list.add(
                        new UiSong(
                                safe(track.getTitle()),
                                track.getArtist() != null ? safe(track.getArtist().getName()) : "",
                                track.getAlbum() != null ? safe(track.getAlbum().getCover_medium()) : "",
                                safe(track.getPreview())
                        )
                ));
                rvPopular.setAdapter(new SongsAdapter(this, list, song -> openNowPlaying(song)));
            }
        });

        // Albums
        viewModel.getTopAlbums().observe(this, response -> {
            if (response != null && response.getData() != null) {
                List<UiAlbum> albumList = new ArrayList<>();
                response.getData().forEach(album -> albumList.add(
                        new UiAlbum(
                                safe(album.getTitle()),
                                album.getArtist() != null ? safe(album.getArtist().getName()) : "",
                                safe(album.getCover_medium())
                        )
                ));
                rvAlbums.setAdapter(new AlbumsAdapter(albumList));
            }
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
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(lp);
            indicatorContainer.addView(dot);
        }
    }

    private void setCurrentIndicator(int index) {
        int count = indicatorContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView dot = (ImageView) indicatorContainer.getChildAt(i);
            dot.setImageResource(i == index ? R.drawable.mf_indicator_active : R.drawable.mf_indicator_inactive);
        }
    }

    private void openNowPlaying(UiSong song) {
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", song.getTitle());
        intent.putExtra("ARTIST_NAME", song.getArtist());
        intent.putExtra("ALBUM_ART_URL", song.getCoverUrl());
        intent.putExtra("PREVIEW_URL", song.getPreviewUrl());
        startActivity(intent);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoSlideHandler.removeCallbacks(autoSlideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoSlideHandler.postDelayed(autoSlideRunnable, 5000);
    }

    // Banner Adapter
    private static class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {

        interface OnClickListener {
            void onClick(UiSong song);
        }

        private final List<UiSong> list;
        private final OnClickListener listener;

        BannerAdapter(List<UiSong> list, OnClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner_track, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            UiSong song = list.get(position);
            Glide.with(holder.iv.getContext()).load(song.getCoverUrl()).into(holder.iv);
            holder.title.setText(song.getTitle());
            holder.artist.setText(song.getArtist());
            holder.itemView.setOnClickListener(v -> listener.onClick(song));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView title, artist;

            VH(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.ivBanner);
                title = itemView.findViewById(R.id.tvBannerTitle);
                artist = itemView.findViewById(R.id.tvBannerArtist);
            }
        }
    }
}