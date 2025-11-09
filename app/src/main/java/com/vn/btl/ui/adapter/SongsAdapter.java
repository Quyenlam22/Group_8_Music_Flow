package com.vn.btl.ui.adapter;

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
import com.vn.btl.ui.activity.NowPlayingActivity;
import com.vn.btl.ui.activity.UiSong;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.VH> {
    private final List<UiSong> data;
    private final Context context;

    public SongsAdapter(Context context, List<UiSong> data) {
        this.context = context;
        this.data = data;
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

        holder.title.setText(song.title);
        holder.artist.setText(song.artist);

        // Load ảnh từ URL API bằng Glide
        Glide.with(context)
                .load(song.coverUrl) // coverUrl từ API
                .placeholder(R.drawable.mf_song_placeholder1) // ảnh tạm thời khi load
                .into(holder.cover);

        // Click mở NowPlayingActivity
        holder.itemView.setOnClickListener(v -> openNowPlaying(song));
    }

    private void openNowPlaying(UiSong song) {
        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putExtra("SONG_TITLE", song.title);
        intent.putExtra("ARTIST_NAME", song.artist);
        intent.putExtra("ALBUM_ART_URL", song.coverUrl); // truyền URL thay vì coverRes
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

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
}

