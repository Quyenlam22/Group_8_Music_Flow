package com.vn.btl.ui.adapter;

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

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {
    private List<Tracks> tracksList;

    public TrackAdapter(List<Tracks> tracksList) {
        this.tracksList = tracksList;
    }
    public void setData(List<Tracks> tracks) {
        this.tracksList.clear();
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
        Tracks tracks = tracksList.get(position);
        holder.txtTitle.setText(tracks.getTitle());
        holder.txtArtist.setText(tracks.getArtistName());
        Glide.with(holder.itemView.getContext())
                .load(tracks.getAlbumCover())
                .transform(new RoundedCorners(12))
                .into(holder.imgCover);
    }

    @Override
    public int getItemCount() {
        return tracksList.size();
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
