package com.vn.btl.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.SettingsAdapter;
import com.vn.btl.utils.BottomNavHelper;
import com.vn.btl.utils.LanguageManager;
import com.vn.btl.utils.ThemeManager;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "settings_prefs";
    public static final String K_DARK = "k_dark";
    public static final String K_LANG = "k_lang"; // "en" / "vi"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.apply(this); // Áp dụng theme
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        String lang = sp.getString(K_LANG, LanguageManager.LANG_EN);

        RecyclerView rv = findViewById(R.id.rv_settings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        SettingsAdapter adapter = new SettingsAdapter(
                java.util.Arrays.asList(
                        SettingsAdapter.Item.sw(
                                R.drawable.ic_dark_mode_24,
                                LanguageManager.getText("settings_dark_theme", lang),
                                ThemeManager.getDark(this),
                                checked -> ThemeManager.setDarkEnabled(this, checked)
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_language_24,
                                LanguageManager.getText("settings_language", lang),
                                true,
                                () -> startActivity(new Intent(this, LanguageSettingsActivity.class))
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_account_24,
                                LanguageManager.getText("settings_account", lang),
                                true,
                                () -> startActivity(new Intent(this, AccountSettingsActivity.class))
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_logout_24,
                                LanguageManager.getText("settings_logout", lang),
                                false,
                                this::confirmLogout
                        )
                )
        );

        rv.setAdapter(adapter);

        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_settings);
    }
    private void updateTexts() {
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

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(LanguageManager.getText("app_name",
                        getSharedPreferences(PREFS, MODE_PRIVATE)
                                .getString(K_LANG, LanguageManager.LANG_EN)))
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Log out", (dialog, which) -> {
                    Intent i = new Intent(this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                })
                .show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateTexts();
    }
}
