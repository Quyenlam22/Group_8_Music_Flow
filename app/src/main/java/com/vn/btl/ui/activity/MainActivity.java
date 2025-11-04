package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.vn.btl.R;
import com.vn.btl.ui.viewmodel.MainViewModel;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvAlbums, rvPopular;
    private ViewPager2 vpBanner;
    private LinearLayout indicatorContainer;

    private final List<Integer> bannerImages = new ArrayList<>();
    private BannerAdapter bannerAdapter;

    private MainViewModel viewModel;
    private static final String TAG = "FirebaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        checkFirebaseLogin();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getSongs().observe(this, songs -> {

        });

        viewModel.loadSongs();

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
        // Nếu cần bắt sự kiện:
        ImageView btnMenu = findViewById(R.id.btnMenu);
        ImageView btnSearch = findViewById(R.id.btnSearch);
        btnMenu.setOnClickListener(v -> { /* TODO: open drawer */ });
        btnSearch.setOnClickListener(v -> { /* TODO: open search */ });
    }

    private void setupCarousel() {
        vpBanner = findViewById(R.id.vpBanner);
        indicatorContainer = findViewById(R.id.indicatorContainer);

        // Ảnh banner 16:9. Có thể dùng cùng 1 ảnh RASAKING đã làm 16:9.
        bannerImages.clear();
        bannerImages.add(R.drawable.mf_banner_placeholder); // RASAKING 16:9
        bannerImages.add(R.drawable.mf_banner_placeholder); // thêm mẫu khác nếu có
        bannerImages.add(R.drawable.mf_banner_placeholder);

        bannerAdapter = new BannerAdapter(bannerImages);
        vpBanner.setAdapter(bannerAdapter);

        // tạo chấm
        buildIndicators(bannerImages.size());
        setCurrentIndicator(0);

        vpBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                setCurrentIndicator(position);
            }
        });
        ImageView fab = findViewById(R.id.fabPlay);
        fab.setOnClickListener(v -> {
            int page = vpBanner.getCurrentItem();
        });

    }

    private void buildIndicators(int count) {
        indicatorContainer.removeAllViews();
        int margin = (int) getResources().getDisplayMetrics().density * 4;
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

        LinearLayoutManager lmAlbums = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        LinearLayoutManager lmPopular = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);

        rvAlbums.setLayoutManager(lmAlbums);
        rvPopular.setLayoutManager(lmPopular);

        int gap = getResources().getDimensionPixelSize(R.dimen.mf_item_gap);
        rvAlbums.addItemDecoration(new SpaceItemDecoration(gap));
        rvPopular.addItemDecoration(new SpaceItemDecoration(gap));

        rvAlbums.setHasFixedSize(true);
        rvPopular.setHasFixedSize(true);

        TextView tvSeeAllAlbums = findViewById(R.id.tvSeeAllAlbums);
        TextView tvSeeAllPopular = findViewById(R.id.tvSeeAllPopular);

        // 2. Thiết lập Listener cho nút "All" của Albums
        if (tvSeeAllAlbums != null) {
            tvSeeAllAlbums.setOnClickListener(v -> {
                // Chuyển sang PlaylistActivity với nguồn là "Albums"
                openPlaylistActivity("Albums");
            });
        } else {
            Log.e(TAG, "Lỗi: Không tìm thấy TextView có ID R.id.tvSeeAllAlbums. Vui lòng kiểm tra activity_main.xml.");
        }

        if (tvSeeAllPopular != null) {
            tvSeeAllPopular.setOnClickListener(v -> {
                openPlaylistActivity("Popular Songs");
            });
        } else {
            Log.e(TAG, "Lỗi: Không tìm thấy TextView có ID R.id.tvSeeAllPopular. Vui lòng kiểm tra activity_main.xml.");
        }

        // Dummy data cân đối
        List<UiAlbum> albums = new ArrayList<>();
        albums.add(new UiAlbum("Cornelia Street","Taylor Swift", R.drawable.mf_album_placeholder1));
        albums.add(new UiAlbum("For You","Laura Melina", R.drawable.mf_album_placeholder2));
        albums.add(new UiAlbum("Blue Hour", "TXT", R.drawable.mf_album_placeholder3));
        albums.add(new UiAlbum("Midnights", "Taylor Swift", R.drawable.mf_album_placeholder));

        List<UiSong> songs = new ArrayList<>();
        songs.add(new UiSong("Available","Justin Bieber", R.drawable.mf_song_placeholder1));
        songs.add(new UiSong("Sucker","Jonas Brothers", R.drawable.mf_song_placeholder2));
        songs.add(new UiSong("Super Bass","Nicki Minaj", R.drawable.mf_song_placeholder3));
        songs.add(new UiSong("Levitating","Dua Lipa", R.drawable.mf_song_placeholder4));

        rvAlbums.setAdapter(new AlbumsAdapter(albums));
        rvPopular.setAdapter(new SongsAdapter(songs));
    }

    private void openPlaylistActivity(String source) {
        Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
        // Truyền dữ liệu để PlaylistActivity biết nó hiển thị danh sách nào
        intent.putExtra("PLAYLIST_SOURCE", source);
        startActivity(intent);
    }
}
