package com.vn.btl.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.model.FavoriteSong;

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

    private ImageView btnLike;
    private boolean isFavorite = false;

    private AppDatabase db;

    private Handler handler = new Handler();

    RotateAnimation rotateAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null) {
            playlist = intent.getParcelableArrayListExtra("SONG_LIST");
            currentIndex = intent.getIntExtra("POSITION", 0);
        }

        if (playlist == null || playlist.isEmpty()) {
            Toast.makeText(this, "Danh sách rỗng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();

        db = AppDatabase.getInstance(this);

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
        btnLike.setOnClickListener(v -> toggleFavorite());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) mediaPlayer.seekTo(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            btnLike.setColorFilter(getColor(R.color.hot_pink));
        } else {
            btnLike.setColorFilter(getColor(android.R.color.darker_gray));
        }
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

        btnLike = findViewById(R.id.btnLike);
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
    //Thêm
    private void checkFavoriteStatus() {
        if (playlist == null || playlist.isEmpty()) return;

        new Thread(() -> {
            UiSong currentSong = playlist.get(currentIndex);
            FavoriteSong existing = db.favoriteSongDAO().getByTitleAndArtist(
                    currentSong.getTitle(),
                    currentSong.getArtist()
            );

            runOnUiThread(() -> {
                isFavorite = (existing != null);
                updateFavoriteButton();
            });
        }).start();
    }
    private void loadSong(int index) {
        UiSong song = playlist.get(index);

        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());

        Glide.with(this)
                .load(song.getCoverUrl())
                .placeholder(R.drawable.music_placeholder)
                .into(imgAlbum);

        // Kiểm tra trạng thái tim cho bài hát mới
        checkFavoriteStatus();
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
    private void toggleFavorite() {
        UiSong currentSong = playlist.get(currentIndex);

        // THÊM LOG
        Log.d("NOWPLAYING_DEBUG", "Thích bài hát: " + currentSong.getTitle());

        new Thread(() -> {
            if (isFavorite) {
                db.favoriteSongDAO().deleteByTitleAndArtist(currentSong.getTitle(), currentSong.getArtist());
            } else {
                FavoriteSong favorite = new FavoriteSong(
                        currentSong.getTitle(),
                        currentSong.getArtist(),
                        currentSong.getCoverUrl(),
                        currentSong.getPreviewUrl(),
                        System.currentTimeMillis()
                );
                db.favoriteSongDAO().insert(favorite);
            }

            runOnUiThread(() -> {
                isFavorite = !isFavorite;
                updateFavoriteButton();

                String message = isFavorite ? "Đã thêm vào My Playlist" : "Đã xóa khỏi My Playlist";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        }).start();
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
