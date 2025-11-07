package com.vn.btl.utils;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.btl.R;
import com.vn.btl.ui.activity.AccountActivity;
import com.vn.btl.ui.activity.MainActivity;
import com.vn.btl.ui.activity.SongsActivity;

public final class BottomNavHelper {
    private BottomNavHelper() {}

    public static void setup(@NonNull Activity activity,
                             @NonNull BottomNavigationView nav,
                             @IdRes int currentItemId) {

        // đưa nav nổi lên để nhận click & set tab hiện tại
        nav.bringToFront();
        nav.getMenu().findItem(currentItemId).setChecked(true);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // chọn lại chính tab đang ở -> bỏ qua
            if (id == currentItemId) return true;

            Class<?> target = null;
            if (id == R.id.nav_home) {
                target = MainActivity.class;
            } else if (id == R.id.nav_song) {
                target = SongsActivity.class;
            } else if (id == R.id.nav_account) {
                target = AccountActivity.class;
            }

            if (target == null) return false;

            // nếu vì lý do gì đó target == activity hiện tại -> bỏ qua
            if (activity.getClass().equals(target)) return true;

            Intent i = new Intent(activity, target);
            // không để stack dài; chuyển tức thời, không giật
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(i);
            activity.overridePendingTransition(0, 0);
            activity.finish();
            return true;
        });

        nav.setOnItemReselectedListener(item -> {
            // optional: scroll to top nếu muốn
        });
    }
}
