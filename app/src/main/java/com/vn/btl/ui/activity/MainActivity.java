package com.vn.btl.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.vn.btl.R;
//import com.vn.btl.ui.viewmodel.MainViewModel;
import android.util.Log;
import com.vn.btl.ui.viewmodel.HomeViewModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.LinearLayout;
import android.widget.ImageView;
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

        rvAlbums.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rvPopular.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

        int gap = getResources().getDimensionPixelSize(R.dimen.mf_item_gap);
        rvAlbums.addItemDecoration(new SpaceItemDecoration(gap));
        rvPopular.addItemDecoration(new SpaceItemDecoration(gap));

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loadTopTracks();

        viewModel.getTopTracks().observe(this, response -> {
            if (response != null && response.getData() != null) {
                // Chuyển dữ liệu thật sang UI model để reuse adapter
                List<UiSong> songs = new ArrayList<>();
                response.getData().forEach(track ->
                        songs.add(new UiSong(
                                track.getTitle(),
                                track.getArtist().getName(),
                                track.getAlbum().getCover_medium()
                        ))
                );

                rvPopular.setAdapter(new SongsAdapter(songs));
            }
        });

        // Giữ nguyên danh sách album mẫu
        viewModel.loadTopAlbums();
        viewModel.getTopAlbums().observe(this, response -> {
            if (response != null && response.getData() != null) {
                Log.d("DEBUG_ALBUM", "so luong album tra ve: " + response.getData().size());
                List<UiAlbum> albums = new ArrayList<>();
                response.getData().forEach(album ->
                        albums.add(new UiAlbum(
                                album.getTitle(),
                                album.getArtist().getName(),
                                album.getCover_medium()
                        ))
                );
                rvAlbums.setAdapter(new AlbumsAdapter(albums));
            }
            else {
                Log.d("DEBUG_ALBUM", "khong co album tu API");
            }
        });

    }

}
