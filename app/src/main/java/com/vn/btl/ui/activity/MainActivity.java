package com.vn.btl.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.vn.btl.R;
import com.vn.btl.ui.viewmodel.MainViewModel;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;
    private static final String TAG = "FirebaseTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        checkFirebaseLogin();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getSongs().observe(this, songs -> {

        });

        viewModel.loadSongs();
    }

    private void checkFirebaseLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Đã đăng nhập Firebase với UID: " + currentUser.getUid());
            return;
        }

        auth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "✅ Kết nối Firebase thành công! UID: " + user.getUid());
                    } else {
                        Log.e(TAG, "❌ Lỗi khi đăng nhập Firebase: ", task.getException());
                    }
                });
    }
}
