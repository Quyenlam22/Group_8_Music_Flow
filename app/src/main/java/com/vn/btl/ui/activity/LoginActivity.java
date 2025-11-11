package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.vn.btl.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginId, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword; // imgEyeHide trong XML
    private TextView tvRegisterLink;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. KHỞI TẠO VIEWS
        etLoginId = findViewById(R.id.etLoginId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivTogglePassword = findViewById(R.id.imgEyeHide);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // 2. KHỞI TẠO FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3. XỬ LÝ SỰ KIỆN
        btnLogin.setOnClickListener(v -> handleLogin());

        // Xử lý ẩn/hiện mật khẩu
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Xử lý chuyển sang Register Activity (Giả định là RegisterActivity)
        tvRegisterLink.setOnClickListener(v -> openRegisterActivity());
    }

    // PHƯƠNG THỨC: Xử lý ẩn/hiện mật khẩu
    private void togglePasswordVisibility() {
        if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_show); // Cần có ic_eye_show
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_hide);
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    // PHƯƠNG THỨC: Xử lý Đăng nhập Chính
    private void handleLogin() {
        String loginId = etLoginId.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(loginId) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập Email/Username và Mật khẩu.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(loginId).matches()) {
            // Trường hợp 1: LoginId là EMAIL -> Đăng nhập trực tiếp bằng Auth
            signIn(loginId, password);
        } else {
            // Trường hợp 2: LoginId là USERNAME -> Cần tra cứu Email từ Firestore
            lookupEmailByUsername(loginId, password);
        }
    }

    // PHƯƠNG THỨC: Tra cứu Email từ Username
    private void lookupEmailByUsername(String username, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            // Tìm thấy Username, lấy Email
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String email = document.getString("email");
                            if (email != null) {
                                // Tiến hành đăng nhập bằng Email tìm được
                                signIn(email, password);
                            } else {
                                // Lỗi dữ liệu: Email bị thiếu trong Firestore
                                Toast.makeText(LoginActivity.this, "Lỗi dữ liệu hệ thống (Email bị thiếu).", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // Không tìm thấy Username
                            Toast.makeText(LoginActivity.this, "Username không tồn tại.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Lỗi truy vấn Firestore (Security Rules?)
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối hoặc truy vấn dữ liệu.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // PHƯƠNG THỨC: Đăng nhập bằng Email/Password (Firebase Auth)
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình chính (ví dụ: MainActivity)
                        Intent mainIntent = new Intent(LoginActivity.this, ChooseArtistActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        // Đăng nhập thất bại (sai Email hoặc Password)
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại Email/Username và Mật khẩu.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openRegisterActivity() {
        // Chuyển sang Register Activity
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
        // Không finish() để có thể quay lại Login sau khi đăng ký
    }
}