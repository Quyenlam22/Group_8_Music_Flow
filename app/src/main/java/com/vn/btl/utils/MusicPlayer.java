package com.vn.btl.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;

public class MusicPlayer {
    private static MusicPlayer instance;
    private ExoPlayer player;
    private Context context;
    private PlayerStateListener listener;

    public interface PlayerStateListener {
        void onPlaying();
        void onPaused();
        void onStopped();
        void onError(String message);
    }

    private MusicPlayer(Context context) {
        this.context = context.getApplicationContext();
        initializePlayer();
    }

    public static synchronized MusicPlayer getInstance(Context context) {
        if (instance == null) {
            instance = new MusicPlayer(context);
        }
        return instance;
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(context).build();

        // Listen player events
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        Log.d("MusicPlayer", "Player ready");
                        break;
                    case Player.STATE_BUFFERING:
                        Log.d("MusicPlayer", "Player buffering");
                        break;
                    case Player.STATE_ENDED:
                        Log.d("MusicPlayer", "Player ended");
                        if (listener != null) listener.onStopped();
                        break;
                }
            }

            @Override
            public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                Log.e("MusicPlayer", "Playback error: " + error.getMessage());
                if (listener != null) listener.onError(error.getMessage());
            }
        });
    }

    public void playPreview(String previewUrl) {
        if (player == null) initializePlayer();

        try {
            // Stop current playback
            if (player.isPlaying()) {
                player.stop();
            }

            // Create media item from preview URL
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(previewUrl));
            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);

            if (listener != null) listener.onPlaying();

        } catch (Exception e) {
            Log.e("MusicPlayer", "Error playing preview: " + e.getMessage());
            if (listener != null) listener.onError(e.getMessage());
        }
    }

    public void pause() {
        if (player != null && player.isPlaying()) {
            player.setPlayWhenReady(false);
            if (listener != null) listener.onPaused();
        }
    }

    public void resume() {
        if (player != null && !player.isPlaying()) {
            player.setPlayWhenReady(true);
            if (listener != null) listener.onPlaying();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
            if (listener != null) listener.onStopped();
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        instance = null;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public long getCurrentPosition() {
        return player != null ? player.getCurrentPosition() : 0;
    }

    public long getDuration() {
        return player != null ? player.getDuration() : 0;
    }

    public void setPlayerStateListener(PlayerStateListener listener) {
        this.listener = listener;
    }

    public ExoPlayer getExoPlayer() {
        return player;
    }
}