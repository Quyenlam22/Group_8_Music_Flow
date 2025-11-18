package com.vn.btl.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.btl.R;
import com.vn.btl.ui.adapter.SettingsAdapter;
import com.vn.btl.utils.BottomNavHelper;
import com.vn.btl.utils.ThemeManager;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFS = "settings_prefs";
    public static final String K_DARK = "k_dark";
    public static final String K_LANG = "k_lang"; // "en" / "vi"

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.vn.btl.utils.ThemeManager.apply(this);
        ThemeManager.apply(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ThemeManager.apply(this);
        RecyclerView rv = findViewById(R.id.rv_settings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);

        SettingsAdapter adapter = new SettingsAdapter(
                java.util.Arrays.asList(
                        SettingsAdapter.Item.sw(
                                R.drawable.ic_dark_mode_24,
                                getString(R.string.settings_dark_theme),
                                ThemeManager.getDark(this),
                                checked -> ThemeManager.setDarkEnabled(this, checked)
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_language_24,
                                getString(R.string.settings_language),
                                true,
                                () -> startActivity(new Intent(this, LanguageSettingsActivity.class))
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_account_24, // hoặc R.drawable.ic_account_24 nếu bạn đã có
                                getString(R.string.settings_account),
                                true,
                                () -> startActivity(new Intent(this, AccountSettingsActivity.class))
                        ),
                        SettingsAdapter.Item.normal(
                                R.drawable.ic_logout_24,
                                getString(R.string.settings_logout),
                                false,
                                this::confirmLogout
                        )
                )
        );
        rv.setAdapter(adapter);

        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_settings);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to log out ?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Log out", (dialog, which) -> {
                    Intent i = new Intent(this, LoginActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                })
                .show();
    }
}
