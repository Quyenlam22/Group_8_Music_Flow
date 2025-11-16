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

// Google Sign-In Imports
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


import com.vn.btl.R;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private EditText etLoginId, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword; // imgEyeHide trong XML
    private TextView tvRegisterLink;
    private ImageView ivGoogleSignIn; // NEW: Google Sign-In Button

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient; // NEW: Google Sign-In Client

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
        ivGoogleSignIn = findViewById(R.id.ivGoogleSignIn); // NEW: Find Google Icon

        // 2. KHỞI TẠO FIREBASE & GOOGLE SIGN-IN
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 3. XỬ LÝ SỰ KIỆN
        btnLogin.setOnClickListener(v -> handleLogin());

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        tvRegisterLink.setOnClickListener(v -> openRegisterActivity());

        // NEW: Handle Google Sign-In
        ivGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    // NEW: Start Google Sign-In flow
    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    // NEW: Handle Google result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // NEW: Authenticate with Firebase using Google Token
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Google login successful!", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(LoginActivity.this, ChooseArtistActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Firebase authentication failed.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // PHƯƠNG THỨC: Xử lý ẩn/hiện mật khẩu
    private void togglePasswordVisibility() {
        int selection = etPassword.getText().length();
        if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_show);
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            ivTogglePassword.setImageResource(R.drawable.ic_eye_hide);
        }

        etPassword.setTypeface(null);
        etPassword.setSelection(selection);
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
            signIn(loginId, password);
        } else {
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
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String email = document.getString("email");
                            if (email != null) {
                                signIn(email, password);
                            } else {
                                Toast.makeText(LoginActivity.this, "Lỗi dữ liệu hệ thống (Email bị thiếu).", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Username không tồn tại.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối hoặc truy vấn dữ liệu.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // PHƯƠNG THỨC: Đăng nhập bằng Email/Password (Firebase Auth)
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(LoginActivity.this, ChooseArtistActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại Email/Username và Mật khẩu.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void openRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
}