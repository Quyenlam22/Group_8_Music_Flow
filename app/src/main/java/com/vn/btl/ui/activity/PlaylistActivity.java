package com.vn.btl.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.database.AppDatabase;
import com.vn.btl.model.Artist;
import com.vn.btl.model.FavoriteSong;
import com.vn.btl.ui.adapter.ArtistsAdapter;
import com.vn.btl.ui.adapter.PlaylistSongsAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistActivity extends AppCompatActivity {

    private RecyclerView rvSongs, rvArtists;
    private ImageButton btnBack;
    private LinearLayout btnPlayAll;
    private ImageView imgPlaylist;
    private TextView tvPlaylistName, tvTrackCount, btnSeeAll;
    private PlaylistSongsAdapter songsAdapter;
    private ArtistsAdapter artistsAdapter;
    private List<FavoriteSong> favoriteSongs = new ArrayList<>();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        initViews();
        setupClickListeners();
        setupBackPressedHandler();
        loadFavoriteSongs();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayAll = findViewById(R.id.btnPlayAll); // THÊM NÚT PLAY ALL
        imgPlaylist = findViewById(R.id.img_playlist);
        tvPlaylistName = findViewById(R.id.tvPlaylistName);
        tvTrackCount = findViewById(R.id.tvTrackCount);
        btnSeeAll = findViewById(R.id.btnSeeAll);

        rvSongs = findViewById(R.id.rvSongs);
        rvArtists = findViewById(R.id.rvArtists);

        db = AppDatabase.getInstance(this);

        // Setup RecyclerView cho bài hát
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        songsAdapter = new PlaylistSongsAdapter(this, new ArrayList<>());
        rvSongs.setAdapter(songsAdapter);

        // Setup RecyclerView cho artists (ngang)
        rvArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        artistsAdapter = new ArtistsAdapter(this);
        rvArtists.setAdapter(artistsAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> navigateToMainActivity());
        btnPlayAll.setOnClickListener(v -> playAllSongs()); // THÊM SỰ KIỆN PLAY ALL
    }

    private void playAllSongs() {
        if (favoriteSongs.isEmpty()) {
            // Có thể hiển thị Toast nếu playlist trống
            return;
        }

        // Chuyển FavoriteSong sang UiSong
        List<UiSong> uiSongs = new ArrayList<>();
        for (FavoriteSong favSong : favoriteSongs) {
            uiSongs.add(new UiSong(
                    favSong.getTitle(),
                    favSong.getArtist(),
                    favSong.getCoverUrl(),
                    favSong.getPreviewUrl()
            ));
        }

        // Mở NowPlayingActivity với bài hát đầu tiên
        Intent intent = new Intent(this, NowPlayingActivity.class);
        intent.putParcelableArrayListExtra("SONG_LIST", new ArrayList<>(uiSongs));
        intent.putExtra("POSITION", 0); // Bắt đầu từ bài đầu tiên
        startActivity(intent);
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToMainActivity();
            }
        });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void loadFavoriteSongs() {
        new Thread(() -> {
            List<FavoriteSong> songs = db.favoriteSongDAO().getAll();

            runOnUiThread(() -> {
                favoriteSongs.clear();
                favoriteSongs.addAll(songs);
                updatePlaylistInfo();
                updateSongsList();
                updateArtistsList();
            });
        }).start();
    }

    private void updatePlaylistInfo() {
        tvPlaylistName.setText("My Favorite Songs");
        tvTrackCount.setText(favoriteSongs.size() + " tracks");

        if (!favoriteSongs.isEmpty()) {
            FavoriteSong firstSong = favoriteSongs.get(0);
            Glide.with(this)
                    .load(firstSong.getCoverUrl())
                    .placeholder(R.drawable.playlist_placeholder)
                    .into(imgPlaylist);
        } else {
            // Nếu không có bài hát nào, dùng ảnh mặc định
            imgPlaylist.setImageResource(R.drawable.playlist_placeholder);
        }
    }

    private void updateSongsList() {
        List<UiSong> uiSongs = new ArrayList<>();
        for (FavoriteSong favSong : favoriteSongs) {
            uiSongs.add(new UiSong(
                    favSong.getTitle(),
                    favSong.getArtist(),
                    favSong.getCoverUrl(),
                    favSong.getPreviewUrl()
            ));
        }

        songsAdapter.setData(uiSongs);
    }

    private void updateArtistsList() {
        // Chuyển danh sách artist name thành List<Artist>
        List<Artist> artistObjects = new ArrayList<>();
        Set<String> uniqueArtists = new HashSet<>();

        for (FavoriteSong song : favoriteSongs) {
            if (song.getArtist() != null && !song.getArtist().isEmpty()) {
                if (uniqueArtists.add(song.getArtist())) {
                    Artist artist = new Artist();
                    artist.setArtistName(song.getArtist());
                    // LẤY ẢNH TỪ BÀI HÁT ĐẦU TIÊN CỦA ARTIST
                    artist.setPicture(getArtistPicture(song.getArtist()));
                    artistObjects.add(artist);
                }
            }
        }

        // Cập nhật adapter artist
        artistsAdapter.setData(artistObjects);
    }

    private String getArtistPicture(String artistName) {
        // Tìm bài hát đầu tiên của artist này để lấy ảnh
        for (FavoriteSong song : favoriteSongs) {
            if (artistName.equals(song.getArtist()) &&
                    song.getCoverUrl() != null && !song.getCoverUrl().isEmpty()) {
                return song.getCoverUrl();
            }
        }
        // Nếu không tìm thấy, trả về ảnh mặc định hoặc rỗng
        return "";
    }
}