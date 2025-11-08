package com.vn.btl.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vn.btl.R;
import com.vn.btl.model.Track;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {
    private List<Track> tracks;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Track track);
    }

    public TrackAdapter(List<Track> tracks, OnItemClickListener listener) {
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.bind(track, listener);
    }

    @Override
    public int getItemCount() {
        return tracks != null ? tracks.size() : 0;
    }

    public void updateData(List<Track> newTracks) {
        this.tracks = newTracks;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemTitle;
        private TextView itemArtist;
        private TextView itemDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemArtist = itemView.findViewById(R.id.itemArtist);
            itemDuration = itemView.findViewById(R.id.itemDuration);
        }

        public void bind(Track track, OnItemClickListener listener) {
            itemTitle.setText(track.getTitle());
            itemArtist.setText(track.getArtist().getName());
            itemDuration.setText(track.getFormattedDuration());

            // Load image từ album hoặc artist
            String imageUrl = track.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.search_placeholder1)
                        .into(itemImage);
            } else {
                // Nếu không có ảnh, dùng placeholder
                itemImage.setImageResource(R.drawable.search_placeholder1);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(track);
                }
            });
        }
    }
}