package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.SongsPagerAdapter;
import com.vn.btl.utils.BottomNavHelper;
import com.vn.btl.utils.ThemeManager;

public class SongsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    ImageView btnSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.vn.btl.utils.ThemeManager.apply(this);
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        tabLayout = findViewById(R.id.tab_menu);
        viewPager = findViewById(R.id.vp_songs);
        btnSearch = findViewById(R.id.btnSearch);

        viewPager.setAdapter(new SongsPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("All Songs"); break;
                case 1: tab.setText("Playlists"); break;
                case 2: tab.setText("Albums"); break;
                case 3: tab.setText("Artists"); break;
            }
        }).attach();

        btnSearch.setOnClickListener(v -> openSearchActivity());
        // chỉ dùng 1 listener của helper
        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_song);
    }
    private void openSearchActivity() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
}
