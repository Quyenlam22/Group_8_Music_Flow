package com.vn.btl.ui.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;

import java.io.IOException;

public class NowPlayingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private ImageView btnPlayPause, imgAlbum;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    private Handler handler = new Handler();
    private RotateAnimation rotateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        initViews();
        initRotateAnimation();

        // Lấy dữ liệu từ Intent
        String title = getIntent().getStringExtra("SONG_TITLE");
        String artist = getIntent().getStringExtra("ARTIST_NAME");
        String coverUrl = getIntent().getStringExtra("ALBUM_ART_URL");
        String previewUrl = getIntent().getStringExtra("PREVIEW_URL");

        if (previewUrl == null || previewUrl.isEmpty()) {
            Toast.makeText(this, "Không thể phát bài hát này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle.setText(title != null ? title : "Unknown Title");
        tvArtist.setText(artist != null ? artist : "Unknown Artist");

        Glide.with(this)
                .load(coverUrl)
                .placeholder(R.drawable.music_placeholder)
                .into(imgAlbum);

        playPreview(previewUrl);

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) pauseMusic();
            else resumeMusic();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnPlayPause = findViewById(R.id.imgPlayPause);
        imgAlbum = findViewById(R.id.imgAlbumCover);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.seekBar);
    }

    private void initRotateAnimation() {
        rotateAnimation = new RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(9000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
    }

    private void playPreview(String url) {
        try {
            if (mediaPlayer != null) mediaPlayer.release();

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration());
                tvTotalTime.setText(formatTime(mp.getDuration()));
                mp.start();

                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.ic_pause);

                imgAlbum.startAnimation(rotateAnimation);
                updateSeekBar();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlayPause.setImageResource(R.drawable.ic_play);
                imgAlbum.clearAnimation();
                seekBar.setProgress(0);
                tvCurrentTime.setText("0:00");
            });

        } catch (IOException e) {
            Toast.makeText(this, "Lỗi phát nhạc", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer == null) return;

        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));

        if (isPlaying) handler.postDelayed(this::updateSeekBar, 300);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlayPause.setImageResource(R.drawable.ic_play);
            imgAlbum.clearAnimation();
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            imgAlbum.startAnimation(rotateAnimation);
            updateSeekBar();
        }
    }

    private String formatTime(int millis) {
        int sec = millis / 1000;
        int min = sec / 60;
        sec = sec % 60;
        return String.format("%d:%02d", min, sec);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {
            try { if (mediaPlayer.isPlaying()) mediaPlayer.stop(); } catch (Exception ignored) {}
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
