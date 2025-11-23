package com.vn.btl.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.vn.btl.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailOrUsername;
    private Button btnResetPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final String TAG = "ForgotPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Cần tạo layout này

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Khởi tạo Views
        etEmailOrUsername = findViewById(R.id.etEmailOrUsername);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        btnResetPassword.setOnClickListener(v -> handlePasswordReset());
    }

    private void handlePasswordReset() {
        String loginId = etEmailOrUsername.getText().toString().trim();

        if (TextUtils.isEmpty(loginId)) {
            Toast.makeText(this, "Vui lòng nhập Email hoặc Username.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Patterns.EMAIL_ADDRESS.matcher(loginId).matches()) {
            // Trường hợp 1: Nếu là Email, chuyển thẳng sang Firebase Reset
            sendPasswordResetEmail(loginId);
        } else {
            // Trường hợp 2: Nếu là Username, tra cứu Email trong Firestore
            lookupEmailByUsername(loginId);
        }
    }

    private void lookupEmailByUsername(String username) {
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String email = document.getString("email");
                            if (email != null) {
                                // Tìm thấy Email, gửi yêu cầu đặt lại mật khẩu
                                sendPasswordResetEmail(email);
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Lỗi dữ liệu hệ thống (Email bị thiếu).", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Username không tồn tại.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối hoặc truy vấn dữ liệu.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Email đặt lại mật khẩu đã được gửi tới " + email + ". Vui lòng kiểm tra hộp thư đến của bạn.",
                                Toast.LENGTH_LONG).show();
                        finish(); // Đóng màn hình này sau khi gửi
                    } else {
                        String errorMessage = "Lỗi khi gửi email đặt lại mật khẩu.";
                        if (task.getException() != null) {
                            errorMessage = "Lỗi: " + task.getException().getMessage();
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Lỗi Firebase Reset: " + errorMessage);
                    }
                });
    }
}