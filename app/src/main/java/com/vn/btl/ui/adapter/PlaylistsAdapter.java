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
import com.vn.btl.model.Playlists;
import com.vn.btl.ui.activity.NowPlayingActivity;
import com.vn.btl.ui.activity.UiSong;

import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHoder>{
    private final List<Playlists> list;
    private Context context;

    public PlaylistsAdapter(List<Playlists> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @NonNull
    @Override
    public PlaylistsAdapter.PlaylistViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_square, parent, false);
        return new PlaylistViewHoder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistsAdapter.PlaylistViewHoder holder, int position) {
        Playlists pl = list.get(position);

        holder.title.setText(pl.getTitle());
        holder.artist.setText(pl.getNb_tracks()+" songs");

        // Load ảnh từ URL API bằng Glide
        Glide.with(context)
                .load(pl.getPicture()) // coverUrl từ API
                .placeholder(R.drawable.mf_song_placeholder1) // ảnh tạm thời khi load
                .into(holder.cover);

        // Click mở NowPlayingActivity
        //holder.itemView.setOnClickListener(v -> openNowPlaying(song));
    }
//    private void openNowPlaying(UiSong song) {
//        Intent intent = new Intent(context, NowPlayingActivity.class);
//        intent.putExtra("SONG_TITLE", song.title);
//        intent.putExtra("ARTIST_NAME", song.artist);
//        intent.putExtra("ALBUM_ART_URL", song.coverUrl); // truyền URL thay vì coverRes
//        context.startActivity(intent);
//    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PlaylistViewHoder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, artist;
        public PlaylistViewHoder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgSong);
            title = itemView.findViewById(R.id.tvSongTitle);
            artist = itemView.findViewById(R.id.tvSongArtist);
        }
    }
}
