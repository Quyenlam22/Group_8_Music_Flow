package com.vn.btl.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vn.btl.R;
import com.vn.btl.utils.BottomNavHelper;

public class AccountActivity extends AppCompatActivity {

    private static final String PREFS = "user_profile";
    private static final String K_NAME = "name";

    private TextView tvName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        tvName = findViewById(R.id.tv_account_name);

        Button btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditAccountActivity.class)));

        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> showLogoutConfirm());

        // chỉ dùng helper
        BottomNavigationView bn = findViewById(R.id.bnMain);
        if (bn != null) BottomNavHelper.setup(this, bn, R.id.nav_account);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        tvName.setText(sp.getString(K_NAME, "Mee Hà"));
    }

    private void showLogoutConfirm() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .setPositiveButton("Đăng xuất", (d, w) -> {
                    Intent i = new Intent(this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                })
                .show();
    }
}
