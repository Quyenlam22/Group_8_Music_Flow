package com.vn.btl.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.ui.activity.NowPlayingActivity;
import com.vn.btl.ui.activity.UiSong;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongsAdapter extends RecyclerView.Adapter<PlaylistSongsAdapter.ViewHolder> {

    private final List<UiSong> data;
    private final Context context;

    public PlaylistSongsAdapter(Context context, List<UiSong> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UiSong song = data.get(position);

        // Hiển thị số thứ tự (bắt đầu từ 1)
        holder.number.setText(String.valueOf(position + 1));

        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());

        holder.itemView.setOnClickListener(v -> {
            playSong(position);
        });
    }

    private void playSong(int position) {
        Log.d("PLAYLIST_ADAPTER", "Truyền cả playlist và position");
        Log.d("PLAYLIST_ADAPTER", "Data size: " + data.size());
        Log.d("PLAYLIST_ADAPTER", "Position: " + position);

        Intent intent = new Intent(context, NowPlayingActivity.class);
        intent.putParcelableArrayListExtra("SONG_LIST", new ArrayList<>(data)); // QUAN TRỌNG: THÊM LẠI DÒNG NÀY
        intent.putExtra("POSITION", position);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<UiSong> newSongs) {
        this.data.clear();
        this.data.addAll(newSongs);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView number, title, artist;

        ViewHolder(View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.tvNumber);
            title = itemView.findViewById(R.id.tvSongTitle);
            artist = itemView.findViewById(R.id.tvArtist);
        }
    }
}