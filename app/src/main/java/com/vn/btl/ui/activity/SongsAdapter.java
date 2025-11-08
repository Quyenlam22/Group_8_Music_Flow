package com.vn.btl.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vn.btl.R;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {
    private final List<UiSong> data;

    public SongsAdapter(List<UiSong> data) { this.data = data; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_square, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        UiSong s = data.get(pos);
        h.title.setText(s.title);
        h.artist.setText(s.artist);
        Glide.with(h.cover.getContext())
                .load(s.coverUrl)
                .placeholder(R.drawable.mf_song_placeholder1)
                .into(h.cover);
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
