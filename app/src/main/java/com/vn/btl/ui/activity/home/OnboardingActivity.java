package com.vn.btl.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.database.ArtistDAO;
import com.vn.btl.model.artist.Artist;
import com.vn.btl.model.something.OnboardingItem;
import com.vn.btl.ui.activity.auth.LoginActivity;
import com.vn.btl.ui.adapter.something.OnboardingAdapter;
import com.vn.btl.ui.viewmodel.OnboardingViewModel;
import com.vn.btl.utils.PrefsManager;

import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OnboardingViewModel viewModel;
    private PrefsManager prefsManager;
    private AppDatabase appDatabase;
    private ArtistDAO dao ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        recyclerView = findViewById(R.id.recyclerView);
        prefsManager = new PrefsManager(this);

        viewModel = new ViewModelProvider(this).get(OnboardingViewModel.class);

        viewModel.getOnboardingItems().observe(this, this::setupAdapter);

        appDatabase = AppDatabase.getInstance(OnboardingActivity.this);
        dao = appDatabase.artistDAO();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d("TEST_USER", "User = " + user);
            if (user != null) {
                String uid = user.getUid();
                // chạy background
                new Thread(() -> {
                    List<Artist> list = dao.getArtistsByUser(uid);

                    runOnUiThread(() -> {
                        if (list != null && !list.isEmpty()) {
                            startActivity(new Intent(this, MainActivity.class));
                        } else {
                            // Nếu chưa có artist thì tiếp tục Onboarding
                        }
                    });
                }).start();
            }
        }, 1500);
    }

    private void setupAdapter(List<OnboardingItem> items) {
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        OnboardingAdapter adapter = new OnboardingAdapter(items, position -> {
            if (position == items.size() - 1) {
                prefsManager.setOnboardingShown(true);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                recyclerView.smoothScrollToPosition(position + 1);
            }
        });

        recyclerView.setAdapter(adapter);
    }
}