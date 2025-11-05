package com.vn.btl.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vn.btl.R;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {
    private final List<UiSong> data;
    private final Context context;

    public SongsAdapter(Context context, List<UiSong> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_square, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        UiSong s = data.get(pos);
        h.title.setText(s.title);
        h.artist.setText(s.artist);
        h.cover.setImageResource(s.coverRes);

        h.itemView.setOnClickListener(v -> {
            openNowPlaying(s);
        });
    }

    private void openNowPlaying(UiSong song) {
        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", song.title);
        intent.putExtra("ARTIST_NAME", song.artist);
        intent.putExtra("ALBUM_ART_RES_ID", song.coverRes);

        context.startActivity(intent);
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView cover; TextView title, artist;
        VH(View v) {
            super(v);
            cover = v.findViewById(R.id.imgSong);
            title = v.findViewById(R.id.tvSongTitle);
            artist = v.findViewById(R.id.tvSongArtist);
        }
    }
}

