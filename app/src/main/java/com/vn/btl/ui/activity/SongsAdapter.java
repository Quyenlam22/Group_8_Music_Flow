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

import com.bumptech.glide.Glide;
import com.vn.btl.R;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {

    private final List<UiSong> data;
    private final Context context;
    private final OnSongClickListener listener;

    // Interface để callback khi click
    public interface OnSongClickListener {
        void onSongClick(UiSong song);
    }

    // Constructor
    public SongsAdapter(Context context, List<UiSong> data, OnSongClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_square, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        UiSong song = data.get(position);

        // Sử dụng getter để truy cập các trường private
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());

        Glide.with(context)
                .load(song.getCoverUrl())
                .placeholder(R.drawable.mf_song_placeholder1)
                .into(holder.cover);

        // Click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song); // callback lên MainActivity
            } else {
                openNowPlaying(song); // fallback nếu không có listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // ViewHolder
    static class VH extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, artist;

        VH(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgSong);
            title = itemView.findViewById(R.id.tvSongTitle);
            artist = itemView.findViewById(R.id.tvSongArtist);
        }
    }

    // Mở NowPlayingActivity nếu cần
    private void openNowPlaying(UiSong song) {
        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", song.getTitle());
        intent.putExtra("ARTIST_NAME", song.getArtist());
        intent.putExtra("ALBUM_ART_URL", song.getCoverUrl());
        intent.putExtra("PREVIEW_URL", song.getPreviewUrl()); // preview URL từ API
        context.startActivity(intent);
    }
}
