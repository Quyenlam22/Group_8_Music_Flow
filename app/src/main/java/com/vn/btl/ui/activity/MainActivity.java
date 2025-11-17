package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.vn.btl.R;

import com.vn.btl.model.Track;
import com.vn.btl.model.Album;

import com.vn.btl.ui.activity.SongsAdapter;
import com.vn.btl.ui.activity.AlbumsAdapter;

import com.vn.btl.ui.activity.UiSong;
import com.vn.btl.ui.activity.UiAlbum;

import com.vn.btl.ui.viewmodel.HomeViewModel;
import com.vn.btl.ui.activity.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAlbums, rvPopular;
    private ViewPager2 vpBanner;
    private LinearLayout indicatorContainer;

    private BannerTrackAdapter bannerAdapter;
    private final List<Track> bannerTracks = new ArrayList<>();

    private HomeViewModel viewModel;
    private static final String TAG = "MainActivity";

    // ---------------- AUTO SLIDE -------------
    private final Handler autoSlideHandler = new Handler();
    private final Runnable autoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerTracks.size() > 0) {
                int next = (vpBanner.getCurrentItem() + 1) % bannerTracks.size();
                vpBanner.setCurrentItem(next, true);
                autoSlideHandler.postDelayed(this, 5000); // tự chuyển mỗi 5 giây
            }
        }
    };
    // ------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadTopTracks();
        viewModel.loadTopAlbums();

        setupHeader();
        setupCarousel();
        setupLists();
    }

    private void setupHeader() {
        ImageView btnMenu = findViewById(R.id.btnMenu);
        ImageView btnSearch = findViewById(R.id.btnSearch);

        if (btnMenu != null) btnMenu.setOnClickListener(v -> {});
        if (btnSearch != null) btnSearch.setOnClickListener(v -> openSearchActivity());
    }

    private void setupCarousel() {
        vpBanner = findViewById(R.id.vpBanner);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        bannerAdapter = new BannerTrackAdapter(bannerTracks, track -> {
            openNowPlaying(
                    safe(track.getTitle()),
                    track.getArtist() != null ? safe(track.getArtist().getName()) : "",
                    track.getAlbum() != null ? safe(track.getAlbum().getCover_big()) : "",
                    safe(track.getPreview())
            );
        });

        vpBanner.setAdapter(bannerAdapter);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });

        ImageView fab = findViewById(R.id.fabPlay);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                int pos = vpBanner.getCurrentItem();
                if (pos >= 0 && pos < bannerTracks.size()) {
                    Track t = bannerTracks.get(pos);

                    openNowPlaying(
                            safe(t.getTitle()),
                            t.getArtist() != null ? safe(t.getArtist().getName()) : "",
                            t.getAlbum() != null ? safe(t.getAlbum().getCover_big()) : "",
                            safe(t.getPreview())
                    );
                }
            });
        }

        // ========== LẤY DỮ LIỆU TOP TRACK ==========
        viewModel.getTopTracks().observe(this, response -> {
            if (response != null && response.getData() != null) {

                bannerTracks.clear();

                // CHỈ LẤY 5 TRACK
                List<Track> src = response.getData();
                int limit = Math.min(5, src.size());
                bannerTracks.addAll(src.subList(0, limit));

                bannerAdapter.notifyDataSetChanged();

                buildIndicators(bannerTracks.size());
                setCurrentIndicator(0);

                // Auto slide sau khi có dữ liệu
                autoSlideHandler.postDelayed(autoSlideRunnable, 5000);
            }
        });
    }

    private void setupLists() {
        rvAlbums = findViewById(R.id.rvAlbums);
        rvPopular = findViewById(R.id.rvPopular);

        rvAlbums.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvPopular.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        int gap = getResources().getDimensionPixelSize(R.dimen.mf_item_gap);
        rvAlbums.addItemDecoration(new SpaceItemDecoration(gap));
        rvPopular.addItemDecoration(new SpaceItemDecoration(gap));

        // Popular Songs
        viewModel.getTopTracks().observe(this, res -> {
            if (res != null && res.getData() != null) {
                List<UiSong> list = new ArrayList<>();
                res.getData().forEach(track -> list.add(
                        new UiSong(
                                safe(track.getTitle()),
                                track.getArtist() != null ? safe(track.getArtist().getName()) : "",
                                track.getAlbum() != null ? safe(track.getAlbum().getCover_medium()) : "",
                                safe(track.getPreview())
                        )));

                rvPopular.setAdapter(new SongsAdapter(this, list, song ->
                        openNowPlaying(song.getTitle(), song.getArtist(), song.getCoverUrl(), song.getPreviewUrl())
                ));
            }
        });

        // Albums
        viewModel.getTopAlbums().observe(this, res -> {
            if (res != null && res.getData() != null) {
                List<UiAlbum> list = new ArrayList<>();
                res.getData().forEach(album -> list.add(
                        new UiAlbum(
                                safe(album.getTitle()),
                                album.getArtist() != null ? safe(album.getArtist().getName()) : "",
                                safe(album.getCover_medium())
                        )));

                rvAlbums.setAdapter(new AlbumsAdapter(list));
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

    private void openNowPlaying(String title, String artist, String coverUrl, String previewUrl) {
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", title);
        intent.putExtra("ARTIST_NAME", artist);
        intent.putExtra("ALBUM_ART_URL", coverUrl);
        intent.putExtra("PREVIEW_URL", previewUrl);
        startActivity(intent);
    }

    private void openSearchActivity() {
        startActivity(new Intent(this, Search.class));
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    // ---------------- Banner Adapter -------------------

    private static class BannerTrackAdapter extends RecyclerView.Adapter<BannerTrackAdapter.VH> {

        interface OnClick {
            void onClick(Track t);
        }

        private final List<Track> list;
        private final OnClick listener;

        BannerTrackAdapter(List<Track> list, OnClick listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_banner_track, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Track t = list.get(pos);

            String cover = t.getAlbum() != null ? safe(t.getAlbum().getCover_big()) : "";

            Glide.with(h.iv.getContext())
                    .load(cover)
                    .placeholder(R.drawable.mf_banner_placeholder)
                    .into(h.iv);

            h.title.setText(safe(t.getTitle()));
            h.artist.setText(t.getArtist() != null ? safe(t.getArtist().getName()) : "");

            h.itemView.setOnClickListener(v -> listener.onClick(t));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView iv;
            TextView title, artist;

            VH(View v) {
                super(v);
                iv = v.findViewById(R.id.ivBanner);
                title = v.findViewById(R.id.tvBannerTitle);
                artist = v.findViewById(R.id.tvBannerArtist);
            }
        }
    }

    // ----------- Lifecycle: dừng auto slide khi tắt app ----------
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
}
