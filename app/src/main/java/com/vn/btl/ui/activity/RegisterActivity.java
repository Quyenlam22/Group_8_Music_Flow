package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Auth Imports
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Firestore Imports
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot; // Cần import QuerySnapshot

import com.vn.btl.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    // ... (Khai báo biến giữ nguyên)
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // ... (onCreate và togglePasswordVisibility giữ nguyên)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 1. KHỞI TẠO CÁC VIEWS
        etUsername = findViewById(R.id.etUsernameRegister);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etPasswordRegister);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordRegister);

        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Views cho Toggle Password
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);

        // 2. KHỞI TẠO FIREBASE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 3. XỬ LÝ SỰ KIỆN NÚT REGISTER
        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> {
                // Thay vì gọi registerUser() trực tiếp, ta gọi hàm kiểm tra
                validateInputAndCheckUsername();
            });
        }

        // 4. XỬ LÝ ẨN/HIỆN MẬT KHẨU
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility(etPassword, ivTogglePassword));
        ivToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(etConfirmPassword, ivToggleConfirmPassword));


        // Xử lý sự kiện chuyển sang Login
        if (tvLoginLink != null) {
            tvLoginLink.setOnClickListener(v -> openLoginActivity());
        }
    }

    // PHƯƠNG THỨC: Xử lý ẩn/hiện mật khẩu (giữ nguyên)
    private void togglePasswordVisibility(EditText editText, ImageView imageView) {
        if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye_show);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.ic_eye_hide);
        }
        editText.setSelection(editText.getText().length());
    }

    // PHƯƠNG THỨC: Kiểm tra đầu vào và gọi kiểm tra Username
    private void validateInputAndCheckUsername() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // 1. Kiểm tra tính hợp lệ cơ bản
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Gọi hàm kiểm tra Username
        checkUsernameExists(username, email, password);
    }

    // PHƯƠNG THỨC MỚI: Kiểm tra Username đã tồn tại trong Firestore chưa
    private void checkUsernameExists(String username, String email, String password) {
        // Tìm kiếm document trong collection 'users' có trường 'username' bằng giá trị nhập vào
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Username CHƯA tồn tại -> Tiến hành đăng ký Auth
                            registerUserAuth(username, email, password);
                        } else {
                            // Username ĐÃ tồn tại
                            Toast.makeText(RegisterActivity.this, "Username này đã được sử dụng.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Lỗi truy vấn Firestore
                        Toast.makeText(RegisterActivity.this, "Lỗi kiểm tra Username.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // PHƯƠNG THỨC MỚI: Chỉ tiến hành đăng ký Auth sau khi đã kiểm tra Username
    private void registerUserAuth(String username, String email, String password) {
        // 1. TẠO TÀI KHOẢN TRÊN FIREBASE AUTH
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // 2. LƯU USERNAME VÀO FIRESTORE
                            saveUserDataToFirestore(user.getUid(), username, email);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Đã đăng ký Auth, nhưng lỗi xử lý dữ liệu người dùng.", Toast.LENGTH_LONG).show();
                            openLoginActivity();
                        }
                    } else {
                        // Đăng ký Auth thất bại (Email đã tồn tại, định dạng sai,...)
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi đăng ký Auth: " + task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Lỗi đăng ký: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // PHƯƠNG THỨC: Lưu dữ liệu người dùng vào Firestore
    private void saveUserDataToFirestore(String userId, String username, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("userId", userId);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(userId)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đã đăng ký Auth, nhưng lỗi lưu thông tin bổ sung.", Toast.LENGTH_LONG).show();
                    }
                    openLoginActivity();
                });
    }

    private void openLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}