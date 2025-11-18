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
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// NEW: Imports cần thiết để tạo Key Hash
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Firebase & Google Imports
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.android.gms.common.SignInButton;
import com.facebook.login.widget.LoginButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FacebookAuthProvider;
import java.util.Arrays;


import com.vn.btl.R;

// IMPORT MỚI: Cần thiết để gọi Activity Quên Mật khẩu
// import com.vn.btl.ui.activity.ForgotPasswordActivity;
// (Đã được IDE tự động thêm nếu bạn đã tạo file)

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "KeyHashGen"; // Tag để lọc Logcat

    private EditText etLoginId, etPassword;
    private Button btnLogin;
    private ImageView ivTogglePassword;
    private TextView tvRegisterLink;

    // NEW: Khai báo cho Quên Mật khẩu
    private TextView tvForgotPassword;

    // ĐÃ SỬA LỖI: Cập nhật kiểu dữ liệu
    private SignInButton ivGoogleSignIn;
    private LoginButton ivFacebookSignIn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // *************************************************************
        // CODE ĐỂ IN KEY HASH RA LOGCAT (Dùng cho cấu hình Facebook)
        // *************************************************************
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d(TAG, "KeyHash: " + keyHash);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Package name not found", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "SHA algorithm not found", e);
        }
        // *************************************************************

        // Khởi tạo Facebook SDK (Khắc phục lỗi khởi tạo)
        FacebookSdk.sdkInitialize(getApplicationContext());

        // 1. KHỞI TẠO VIEWS
        etLoginId = findViewById(R.id.etLoginId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ivTogglePassword = findViewById(R.id.imgEyeHide);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        // NEW: Khởi tạo cho Quên Mật khẩu
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        // ĐÃ SỬA LỖI: findViewById đúng kiểu
        ivGoogleSignIn = findViewById(R.id.ivGoogleSignIn);
        ivFacebookSignIn = findViewById(R.id.ivFacebookSignIn);

        // 2. KHỞI TẠO FIREBASE & GOOGLE SIGN-IN & FACEBOOK
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Thiết lập Callback cho Facebook Login
        setupFacebookLogin();

        // 3. XỬ LÝ SỰ KIỆN
        btnLogin.setOnClickListener(v -> handleLogin());
        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
        tvRegisterLink.setOnClickListener(v -> openRegisterActivity());

        // SỬA ĐỔI: Chuyển hướng sang Activity mới
        tvForgotPassword.setOnClickListener(v -> openForgotPasswordActivity());

        // Xử lý Google Sign-In
        ivGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        // Facebook LoginButton tự xử lý sự kiện click qua setupFacebookLogin()
        // KHÔNG CẦN: ivFacebookSignIn.setOnClickListener(v -> signInWithFacebook());
    }

    // --- FACEBOOK LOGIN METHODS ---

    private void setupFacebookLogin() {
        // Thiết lập quyền và callback
        ivFacebookSignIn.setReadPermissions(Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Đăng nhập Facebook bị hủy.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                // Thường là do lỗi 190 (Key Hash)
                Toast.makeText(LoginActivity.this, "Lỗi đăng nhập Facebook: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công -> Chuyển Activity
                        Log.d(TAG, "Facebook Auth SUCCESS. Navigating to ChooseArtistActivity.");
                        Toast.makeText(LoginActivity.this, "Đăng nhập bằng Facebook thành công!", Toast.LENGTH_SHORT).show();
                        Intent mainIntent = new Intent(LoginActivity.this, ChooseArtistActivity.class);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        if (task.getException() != null) {
                            Log.e(TAG, "Firebase Facebook Auth Failed: " + task.getException().getMessage());
                            // Cân nhắc hiển thị lỗi chi tiết hơn trên màn hình
                            Toast.makeText(LoginActivity.this, "Xác thực Firebase thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Xác thực Firebase thất bại. Vui lòng kiểm tra cấu hình.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

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

    // --- EMAIL/USERNAME LOGIN METHODS ---

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

    // --- NEW: FORGOT PASSWORD METHOD ---

    private void openForgotPasswordActivity() {
        // Chuyển sang Activity mới để xử lý logic quên mật khẩu
        Intent resetIntent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(resetIntent);
    }
}