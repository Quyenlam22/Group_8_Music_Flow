package com.vn.btl.ui.activity;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.api.ApiService;
import com.vn.btl.api.RetrofitClient;
import com.vn.btl.model.DeezerResponse;
import com.vn.btl.model.Track;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NowPlayingActivity extends AppCompatActivity {

    private ImageView imgAlbum, btnPlayPause;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    private static final int PREVIEW_DURATION = 30; // Deezer chỉ cho 30s


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_now_playing);

        // Gắn view
        imgAlbum = findViewById(R.id.imgCover);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        seekBar = findViewById(R.id.seekBar);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);

        seekBar.setMax(PREVIEW_DURATION);
        seekBar.setProgress(0);
        tvCurrentTime.setText(formatTime(0));
        tvTotalTime.setText(formatTime(PREVIEW_DURATION));

        // Gọi API
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Log.d("API_DEBUG", "Gọi API Deezer...");

        apiService.searchSong("BlackPink").enqueue(new Callback<DeezerResponse>() {
            @Override
            public void onResponse(Call<DeezerResponse> call, Response<DeezerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Track> tracks = response.body().getData();
                    if (!tracks.isEmpty()) {
                        Track track = tracks.get(0);
                        tvTitle.setText(track.getTitle());
                        tvArtist.setText(track.getArtist().getName());
                        Glide.with(NowPlayingActivity.this)
                                .load(track.getAlbum().getCover_big())
                                .into(imgAlbum);
                        setupPlayer(track.getPreview());
                    }
                }
            }

            @Override
            public void onFailure(Call<DeezerResponse> call, Throwable t) {
                Log.e("API_DEBUG", "Lỗi API: " + t.getMessage());
            }
        });
    }

    private void setupPlayer(String url) {
        Log.d("PLAYER_DEBUG", "setupPlayer: " + url);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("PLAYER_DEBUG", "Lỗi setDataSource: " + e.getMessage());
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            btnPlayPause.setEnabled(true);
            btnPlayPause.setOnClickListener(v -> {
                if (isPlaying) {
                    pauseMusic();
                } else {
                    startMusic();
                }
            });
        });

        mediaPlayer.setOnCompletionListener(mp -> stopMusic());
    }

    private void startMusic() {
        if (mediaPlayer == null) return;

        mediaPlayer.start();
        isPlaying = true;
        btnPlayPause.setImageResource(R.drawable.ic_pause);
        startSeekBarUpdate();
    }

    private void pauseMusic() {
        if (mediaPlayer == null) return;

        mediaPlayer.pause();
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
        handler.removeCallbacks(updateSeekBar);
    }

    private void stopMusic() {
        if (mediaPlayer == null) return;

        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);
        seekBar.setProgress(0);
        tvCurrentTime.setText(formatTime(0));
        handler.removeCallbacks(updateSeekBar);
    }

    private void startSeekBarUpdate() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentSec = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentSec);
                    tvCurrentTime.setText(formatTime(currentSec));

                    if (currentSec >= PREVIEW_DURATION) {
                        stopMusic();
                        return;
                    }
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(updateSeekBar);
    }

    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%d:%02d", min, sec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            handler.removeCallbacks(updateSeekBar);
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
