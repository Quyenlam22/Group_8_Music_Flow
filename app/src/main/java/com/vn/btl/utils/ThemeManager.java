package com.vn.btl.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public final class ThemeManager {
    private ThemeManager() {}

    public static final String PREFS = "settings_prefs";
    public static final String K_DARK = "k_dark";

    public static void apply(Context ctx) {
        boolean dark = getDark(ctx);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static void setDarkEnabled(Activity act, boolean enabled) {
        SharedPreferences sp = act.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        sp.edit().putBoolean(K_DARK, enabled).apply();
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        act.recreate(); // áp dụng ngay
    }

    public static boolean getDark(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(K_DARK, false);
    }
}
