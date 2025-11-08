package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Cần import TextView
import androidx.appcompat.app.AppCompatActivity;
import com.vn.btl.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        // Xử lý sự kiện Register (tạm thời quay lại Login)
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                // TODO: Xử lý logic đăng ký sau
                openLoginActivity();
            });
        }

        // Xử lý sự kiện chuyển sang Login (Sử dụng Lambda Expression)
        if (tvLoginLink != null) {
            tvLoginLink.setOnClickListener(v -> openLoginActivity());
        }
    }

    // PHƯƠNG THỨC MỚI: Không cần đối số View, chỉ đơn giản đóng màn hình
    private void openLoginActivity() {
        // Trở về Login Activity, vì nó là Activity trước đó hoặc đã được khai báo là SingleTop
        finish();
    }
}