package com.vn.btl.ui.activity;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;

import java.io.IOException;

public class NowPlayingActivity extends AppCompatActivity {

    private ImageView imgAlbum, btnPlayPause;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler = new Handler();
    private Runnable updateSeekBar;

    private ObjectAnimator rotateAnimator;

    private static final int PREVIEW_DURATION = 30; // Deezer preview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

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

        String title = getIntent().getStringExtra("SONG_TITLE");
        String artist = getIntent().getStringExtra("ARTIST_NAME");
        String coverUrl = getIntent().getStringExtra("ALBUM_ART_URL");
        String previewUrl = getIntent().getStringExtra("PREVIEW_URL");

        tvTitle.setText(title);
        tvArtist.setText(artist);

        Glide.with(this)
                .load(coverUrl)
                .circleCrop() // ✅ Hình tròn
                .into(imgAlbum);

        // Animation xoay album
        rotateAnimator = ObjectAnimator.ofFloat(imgAlbum, "rotation", 0f, 360f);
        rotateAnimator.setDuration(8000);
        rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimator.setInterpolator(new LinearInterpolator());

        if (previewUrl != null && !previewUrl.isEmpty()) {
            setupPlayer(previewUrl);
        }
    }

    private void setupPlayer(String url) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(mp -> {
            btnPlayPause.setEnabled(true);
            btnPlayPause.setOnClickListener(v -> {
                if (isPlaying) pauseMusic();
                else startMusic();
            });
        });

        mediaPlayer.setOnCompletionListener(mp -> stopMusic());
    }

    private void startMusic() {
        if (mediaPlayer == null) return;

        mediaPlayer.start();
        isPlaying = true;
        btnPlayPause.setImageResource(R.drawable.ic_pause);

        rotateAnimator.start();

        startSeekBarUpdate();
    }

    private void pauseMusic() {
        if (mediaPlayer == null) return;

        mediaPlayer.pause();
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play);

        rotateAnimator.pause();

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

        rotateAnimator.cancel();
        imgAlbum.setRotation(0f);

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
