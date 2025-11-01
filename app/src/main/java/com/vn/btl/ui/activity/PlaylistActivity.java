package com.vn.btl.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

import com.vn.btl.R;

public class PlaylistActivity extends AppCompatActivity {

    private RecyclerView rvSongs, rvArtists;
    private ImageView btnBack;
    private TextView btnSeeAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        // Ánh xạ view
        initViews();

        // Xử lý sự kiện click
        setupClickListeners();

        // Thiết lập dữ liệu
        setupSampleData();
    }

    private void initViews() {
        rvSongs = findViewById(R.id.rvSongs);
        rvArtists = findViewById(R.id.rvArtists);
        btnBack = findViewById(R.id.btnBack);
        btnSeeAll = findViewById(R.id.btnSeeAll);
    }

    private void setupClickListeners() {
        // Xử lý nút back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý nút See All
        btnSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlaylistActivity.this, "Showing all artists", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút Play All
        TextView btnPlayAll = findViewById(R.id.btnPlayAll);
        btnPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PlaylistActivity.this, "Playing all songs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSampleData() {
        // Dữ liệu mẫu bài hát
        List<SongItem> songs = Arrays.asList(
                new SongItem("The Crunge", "7:59"),
                new SongItem("Cornelia Street", "4:15"),
                new SongItem("D'yer Mak'er", "3:48"),
                new SongItem("No Quarter", "6:15"),
                new SongItem("The Ocean", "4:30"),
                new SongItem("The Rain Song", "5:12")
        );

        // Dữ liệu mẫu nghệ sĩ
        List<ArtistItem> artists = Arrays.asList(
                new ArtistItem("Drake", R.drawable.ic_launcher_foreground),
                new ArtistItem("Ariana Grande", R.drawable.ic_launcher_foreground),
                new ArtistItem("Taylor Swift", R.drawable.ic_launcher_foreground),
                new ArtistItem("The Weeknd", R.drawable.ic_launcher_foreground),
                new ArtistItem("Billie Eilish", R.drawable.ic_launcher_foreground)
        );

        // Setup RecyclerView cho bài hát
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.setAdapter(new SongAdapter(songs));

        // Setup RecyclerView cho nghệ sĩ
        rvArtists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvArtists.setAdapter(new ArtistAdapter(artists));
    }

    // Model cho bài hát
    static class SongItem {
        String title;
        String duration;

        SongItem(String title, String duration) {
            this.title = title;
            this.duration = duration;
        }
    }

    // Model cho nghệ sĩ
    static class ArtistItem {
        String name;
        int imageRes;

        ArtistItem(String name, int imageRes) {
            this.name = name;
            this.imageRes = imageRes;
        }
    }

    // Adapter cho bài hát
    static class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
        private List<SongItem> songList;

        SongAdapter(List<SongItem> songList) {
            this.songList = songList;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new SongViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            SongItem song = songList.get(position);
            holder.tvNumber.setText(String.valueOf(position + 1));
            holder.tvTitle.setText(song.title);
            holder.tvSubtitle.setText("Artist");
            holder.tvDuration.setText(song.duration);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(holder.itemView.getContext(), "Playing: " + song.title, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return songList.size();
        }

        static class SongViewHolder extends RecyclerView.ViewHolder {
            TextView tvNumber, tvTitle, tvSubtitle, tvDuration;

            SongViewHolder(View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tvNumber);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
                tvDuration = itemView.findViewById(R.id.tvDuration);
            }
        }
    }

    // Adapter cho nghệ sĩ
    static class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
        private List<ArtistItem> artistList;

        ArtistAdapter(List<ArtistItem> artistList) {
            this.artistList = artistList;
        }

        @NonNull
        @Override
        public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_artist, parent, false);
            return new ArtistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
            ArtistItem artist = artistList.get(position);
            holder.tvArtistName.setText(artist.name);
            holder.imgArtist.setImageResource(artist.imageRes);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(holder.itemView.getContext(), "Artist: " + artist.name, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return artistList.size();
        }

        static class ArtistViewHolder extends RecyclerView.ViewHolder {
            ImageView imgArtist;
            TextView tvArtistName;

            ArtistViewHolder(View itemView) {
                super(itemView);
                imgArtist = itemView.findViewById(R.id.imgArtist);
                tvArtistName = itemView.findViewById(R.id.tvArtistName);
            }
        }
    }
}