package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.vn.btl.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etPassword;
    private ImageView imgEyeHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);
        etPassword = findViewById(R.id.etPassword);
        imgEyeHide = findViewById(R.id.imgEyeHide);

        // Mặc định ẩn mật khẩu khi khởi tạo
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // 1. Xử lý sự kiện LOGIN: Chuyển sang MainActivity
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                // Chưa cần xử lý logic, chuyển Home luôn
                openMainActivity();
            });
        }

        // 2. Xử lý sự kiện chuyển sang Register
        if (tvRegisterLink != null) {
            tvRegisterLink.setOnClickListener(v -> openRegisterActivity());
        }

        // 3. Xử lý sự kiện Ẩn/Hiện mật khẩu
        if (imgEyeHide != null) {
            imgEyeHide.setOnClickListener(v -> togglePasswordVisibility());
        }
    }

    private void togglePasswordVisibility() {
        int cursorPosition = etPassword.getSelectionEnd();

        if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // imgEyeHide.setImageResource(R.drawable.ic_eye_show);
        } else {
            // Ẩn mật khẩu: chuyển sang inputType password
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // imgEyeHide.setImageResource(R.drawable.ic_eye_hide);
        }

        // Đặt lại con trỏ về vị trí cũ sau khi thay đổi inputType
        etPassword.setSelection(cursorPosition);
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}