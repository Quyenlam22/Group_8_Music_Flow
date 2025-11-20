package com.vn.btl.ui.adapter;

import android.annotation.SuppressLint;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.vn.btl.R;
import com.vn.btl.model.Tracks;
import com.vn.btl.ui.activity.NowPlayingActivity;
import com.vn.btl.ui.activity.UiSong;

import java.util.ArrayList;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<Tracks> tracksList;
    private Context context;

    public TrackAdapter(List<Tracks> tracksList) {
        this.tracksList = tracksList;
    }

    public TrackAdapter(List<Tracks> tracksList, Context context) {
        this.tracksList = tracksList;
        this.context = context;
    }

    public void setData(List<Tracks> tracks) {
        if (this.tracksList == null) {
            this.tracksList = new ArrayList<>();
        } else {
            this.tracksList.clear();
        }
        if (tracks != null) {
            this.tracksList.addAll(tracks);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackAdapter.TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend_songs, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackAdapter.TrackViewHolder holder, int position) {
        Tracks track = tracksList.get(position);

        holder.txtTitle.setSelected(true);
        holder.txtTitle.setText(track.getTitle());
        holder.txtArtist.setText(track.getArtistName());

        Glide.with(holder.itemView.getContext())
                .load(track.getAlbumCover())
                .transform(new RoundedCorners(12))
                .placeholder(R.drawable.mf_song_placeholder1) // giữ placeholder hiện có trong project
                .into(holder.imgCover);

        // Click mở NowPlayingActivity — GỬI TOÀN BỘ DANH SÁCH dưới dạng ArrayList<UiSong>
        holder.itemView.setOnClickListener(v -> openNowPlaying(position));
    }

    private void openNowPlaying(int pos) {
        if (context == null) return;

        Intent intent = new Intent(context, NowPlayingActivity.class);

        // Chuyển List<Tracks> -> ArrayList<UiSong>
        ArrayList<UiSong> uiList = new ArrayList<>();
        if (tracksList != null) {
            for (Tracks t : tracksList) {
                // đảm bảo không bị null
                String title = t.getTitle() == null ? "" : t.getTitle();
                String artist = t.getArtistName() == null ? "" : t.getArtistName();
                String cover = t.getAlbumCover() == null ? "" : t.getAlbumCover();
                String preview = t.getPreview() == null ? "" : t.getPreview();

                uiList.add(new UiSong(title, artist, cover, preview));
            }
        }

        intent.putParcelableArrayListExtra("SONG_LIST", uiList);
        intent.putExtra("POSITION", pos);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return tracksList != null ? tracksList.size() : 0;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView txtTitle, txtArtist;
        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtArtist = itemView.findViewById(R.id.txtArtist);
        }
    }
}
