package com.vn.btl.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.SongsPagerAdapter;
import com.vn.btl.utils.BottomNavHelper;
import com.vn.btl.utils.LanguageManager;
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

        int selectedTab = getIntent().getIntExtra("SELECTED_TAB", 0);
        viewPager.setCurrentItem(selectedTab, false);
        btnSearch.setOnClickListener(v -> openSearchActivity());
        // chỉ dùng 1 listener của helper
        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_song);
    }
    private void openSearchActivity() {
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }
    private void updateTabTexts() {
        SharedPreferences sp = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        String lang = sp.getString(SettingsActivity.K_LANG, LanguageManager.LANG_EN);
        BottomNavigationView bn = findViewById(R.id.bnMain);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(LanguageManager.getText("label_songs", lang));
        if (tabLayout != null) {
            tabLayout.getTabAt(0).setText(LanguageManager.getText("tab_all_songs", lang));
            tabLayout.getTabAt(1).setText(LanguageManager.getText("tab_playlists", lang));
            tabLayout.getTabAt(2).setText(LanguageManager.getText("tab_albums", lang));
            tabLayout.getTabAt(3).setText(LanguageManager.getText("tab_artists", lang));


        }

    }
    private void updateBottomNavigationTexts() {
        SharedPreferences sp = getSharedPreferences(SettingsActivity.PREFS, MODE_PRIVATE);
        String lang = sp.getString(SettingsActivity.K_LANG, LanguageManager.LANG_EN);

        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) {
            Menu menu = bn.getMenu();
            menu.findItem(R.id.nav_home).setTitle(LanguageManager.getText("nav_home", lang));
            menu.findItem(R.id.nav_playlist).setTitle(LanguageManager.getText("nav_playlist", lang));
            menu.findItem(R.id.nav_song).setTitle(LanguageManager.getText("nav_songs", lang));
            menu.findItem(R.id.nav_settings).setTitle(LanguageManager.getText("nav_settings", lang));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateTabTexts();
        updateBottomNavigationTexts();

    }


}
