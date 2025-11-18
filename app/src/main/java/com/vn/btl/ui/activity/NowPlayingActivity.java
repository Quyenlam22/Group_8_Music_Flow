package com.vn.btl.ui.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;

import java.io.IOException;
import java.util.ArrayList;

public class NowPlayingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private ArrayList<UiSong> trackList;
    private int currentPosition;

    private ImageView btnPlay, btnNext, btnPrev, imgAlbum;
    private TextView tvTitle, tvArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        // Mapping view
        btnPlay = findViewById(R.id.imgPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        imgAlbum = findViewById(R.id.imgAlbumCover);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu danh sách bài hát từ intent
        trackList = getIntent().getParcelableArrayListExtra("TRACK_LIST");
        currentPosition = getIntent().getIntExtra("POSITION", 0);

        if (trackList == null || trackList.isEmpty()) {
            Toast.makeText(this, "No track data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load track hiện tại
        loadTrack(currentPosition);

        // Play/Pause
        btnPlay.setOnClickListener(v -> {
            if (isPlaying) pauseMusic();
            else resumeMusic();
        });

        // Next
        btnNext.setOnClickListener(v -> {
            if (currentPosition < trackList.size() - 1) {
                currentPosition++;
                loadTrack(currentPosition);
            } else {
                Toast.makeText(this, "This is the last track", Toast.LENGTH_SHORT).show();
            }
        });

        // Previous
        btnPrev.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                loadTrack(currentPosition);
            } else {
                Toast.makeText(this, "This is the first track", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTrack(int position) {
        if (trackList == null || position < 0 || position >= trackList.size()) return;

        UiSong song = trackList.get(position);
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        Glide.with(this).load(song.getCoverUrl()).into(imgAlbum);
        playPreview(song.getPreviewUrl());
    }

    private void playPreview(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "Preview URL is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer != null) mediaPlayer.release();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.ic_pause);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play_16);
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot play track", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.ic_play_16);
        }
    }

    private void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlay.setImageResource(R.drawable.ic_pause);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
