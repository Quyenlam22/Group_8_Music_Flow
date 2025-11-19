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
import java.util.ArrayList;

public class NowPlayingActivity extends AppCompatActivity {

    private ArrayList<UiSong> playlist;
    private int currentIndex = 0;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isRepeat = false;   // repeat flag

    private ImageView btnPrev, btnNext, btnPlayPause, imgAlbum, btnRepeat;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    private Handler handler = new Handler();

    RotateAnimation rotateAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        initViews();

        playlist = getIntent().getParcelableArrayListExtra("SONG_LIST");
        currentIndex = getIntent().getIntExtra("POSITION", 0);

        if (playlist == null || playlist.isEmpty()) {
            Toast.makeText(this, "Danh sách rỗng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initRotationAnimation();
        loadSong(currentIndex);

        btnPlayPause.setOnClickListener(v -> {
            if (isPlaying) pauseMusic();
            else resumeMusic();
        });

        btnNext.setOnClickListener(v -> playNext());
        btnPrev.setOnClickListener(v -> playPrevious());

        btnRepeat.setOnClickListener(v -> toggleRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnPlayPause = findViewById(R.id.imgPlayPause);
        btnRepeat = findViewById(R.id.btnRepeat);

        imgAlbum = findViewById(R.id.imgAlbumCover);

        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);

        seekBar = findViewById(R.id.seekBar);
    }

    private void initRotationAnimation() {
        rotateAnim = new RotateAnimation(
                0f, 360f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnim.setDuration(8000);
        rotateAnim.setRepeatCount(RotateAnimation.INFINITE);
        rotateAnim.setInterpolator(new LinearInterpolator());
    }

    private void loadSong(int index) {
        UiSong song = playlist.get(index);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        Glide.with(this)
                .load(song.getCoverUrl())
                .placeholder(R.drawable.music_placeholder)
                .into(imgAlbum);

        playPreview(song.getPreviewUrl());
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

                imgAlbum.startAnimation(rotateAnim);
                updateSeekBar();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeat) {
                    loadSong(currentIndex);
                } else {
                    playNext();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Không thể phát bài hát!", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNext() {
        currentIndex++;
        if (currentIndex >= playlist.size()) currentIndex = 0;
        loadSong(currentIndex);
    }

    private void playPrevious() {
        currentIndex--;
        if (currentIndex < 0) currentIndex = playlist.size() - 1;
        loadSong(currentIndex);
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
            imgAlbum.startAnimation(rotateAnim);
            updateSeekBar();
        }
    }

    private void toggleRepeat() {
        isRepeat = !isRepeat;

        if (isRepeat) {
            btnRepeat.setColorFilter(getColor(R.color.hot_pink));
            Toast.makeText(this, "Repeat bật", Toast.LENGTH_SHORT).show();
        } else {
            btnRepeat.setColorFilter(getColor(android.R.color.white));
            Toast.makeText(this, "Repeat tắt", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer == null) return;

        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        tvCurrentTime.setText(formatTime(mediaPlayer.getCurrentPosition()));

        if (isPlaying)
            handler.postDelayed(this::updateSeekBar, 500);
    }

    private String formatTime(int ms) {
        int s = ms / 1000;
        int m = s / 60;
        s = s % 60;
        return String.format("%d:%02d", m, s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            } catch (Exception ignored) {}
            mediaPlayer.release();
        }
    }
}
