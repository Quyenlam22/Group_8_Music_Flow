package com.vn.btl.ui.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.model.Tracks;
import com.vn.btl.ui.adapter.TrackAdapter;

import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.util.List;

public class NowPlayingActivity extends AppCompatActivity {
    private static final String TAG = "NowPlayingActivity";
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private List<Tracks> trackList;
    private Tracks currentTrack;
    ImageView btnPlay,btnNext,btnPrev,imgAlbum;
    TextView tvTitle,tvArtist;
    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Giả định layout này được lưu trong res/layout/activity_now_playing.xml
        setContentView(R.layout.activity_now_playing);
        mapping();

        btnPlay.setOnClickListener(v -> {
            if (isPlaying) {
                pauseMusic();
            }
            else {
                resumeMusic();
            }
        });

        currentPosition = getIntent().getIntExtra("POSITION", 0);
        trackList = TrackAdapter.staticTrackList;
        currentTrack = trackList.get(currentPosition);
        String preview = currentTrack.getPreview();
        playPreview(preview);

        btnNext.setOnClickListener(v -> {
            if (currentPosition < trackList.size() - 1) {
                currentPosition++;
                playPreview(trackList.get(currentPosition).getPreview());
                Glide.with(this)
                        .load(trackList.get(currentPosition).getAlbumCover())
                        .into(imgAlbum);
                tvTitle.setText(trackList.get(currentPosition).getTitle());
                tvArtist.setText(trackList.get(currentPosition).getArtistName());
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPosition > 0) {
                currentPosition--;
                playPreview(trackList.get(currentPosition).getPreview());
                Glide.with(this)
                        .load(trackList.get(currentPosition).getAlbumCover())
                        .into(imgAlbum);
                tvTitle.setText(trackList.get(currentPosition).getTitle());
                tvArtist.setText(trackList.get(currentPosition).getArtistName());
            }
        });
        handleIntentData();
        setupBackButton();
    }

    private void mapping() {
        btnPlay = findViewById(R.id.imgPlayPause);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        imgAlbum = findViewById(R.id.imgAlbumCover);
        tvTitle = findViewById(R.id.tvSongTitle);
        tvArtist = findViewById(R.id.tvArtist);
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Đóng NowPlayingActivity
            });
        }
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Nhận dữ liệu bài hát được truyền từ Activity trước
            String songTitle = intent.getStringExtra("SONG_TITLE");
            String artistName = intent.getStringExtra("ARTIST_NAME");
            String albumArtUrl = intent.getStringExtra("ALBUM_ART_URL");

            TextView tvTitle = findViewById(R.id.tvSongTitle);
            TextView tvArtist = findViewById(R.id.tvArtist);
            ImageView imgAlbum = findViewById(R.id.imgAlbumCover);

            if (tvTitle != null && songTitle != null) {
                tvTitle.setText(songTitle);
            }
            if (tvArtist != null && artistName != null) {
                tvArtist.setText(artistName);
            }
            if (albumArtUrl != null && imgAlbum != null) {
                Glide.with(this)
                        .load(albumArtUrl)
                        .into(imgAlbum);
            }

            Log.d(TAG, "Đang phát: " + songTitle + " - " + artistName);
        }
    }
    private void playPreview(String previewUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(previewUrl);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                isPlaying = true;
                btnPlay.setImageResource(R.drawable.ic_pause);
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                btnPlay.setImageResource(R.drawable.ic_play_16);
                Toast.makeText(this, "Preview finished", Toast.LENGTH_SHORT).show();
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing preview", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            btnPlay.setImageResource(R.drawable.ic_play_16); // đổi lại ▶
        }
    }
    private void resumeMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            btnPlay.setImageResource(R.drawable.ic_pause); // ||
        }
    }
}