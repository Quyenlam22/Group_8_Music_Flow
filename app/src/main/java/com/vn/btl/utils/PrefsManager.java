package com.vn.btl.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {
    private static final String PREF_NAME = "music_app_prefs";
    private static final String KEY_ONBOARDING = "onboarding_shown";
    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setOnboardingShown(boolean shown) {
        prefs.edit().putBoolean(KEY_ONBOARDING, shown).apply();
    }

    public boolean isOnboardingShown() {
        return prefs.getBoolean(KEY_ONBOARDING, false);
    }
}