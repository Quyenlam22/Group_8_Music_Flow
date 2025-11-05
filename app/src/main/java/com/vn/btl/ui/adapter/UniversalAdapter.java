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
import com.vn.btl.model.Artist;
import com.vn.btl.model.Track;
import java.util.ArrayList;
import java.util.List;

public class UniversalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TRACK = 1;
    private static final int TYPE_ARTIST = 2;

    private List<Object> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onTrackClick(Track track);
        void onArtistClick(Artist artist);
    }

    public UniversalAdapter(List<Object> items, OnItemClickListener listener) {
        this.items = items != null ? items : new ArrayList<>();
        this.listener = listener;
    }

    public void updateItems(List<Object> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof Artist) return TYPE_ARTIST;
        if (item instanceof Track) return TYPE_TRACK;
        return TYPE_TRACK;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_ARTIST) {
            View view = inflater.inflate(R.layout.item_artist, parent, false);
            return new ArtistViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_song, parent, false);
            return new TrackViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof TrackViewHolder && item instanceof Track) {
            ((TrackViewHolder) holder).bind((Track) item, position);
        } else if (holder instanceof ArtistViewHolder && item instanceof Artist) {
            ((ArtistViewHolder) holder).bind((Artist) item);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // Track ViewHolder
    class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber, tvTitle, tvSubtitle, tvDuration;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tvNumber);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        public void bind(Track track, int position) {
            tvNumber.setText(String.valueOf(position + 1));
            tvTitle.setText(track.getTitle() != null ? track.getTitle() : "Unknown Title");
            tvSubtitle.setText(track.getArtistName() != null ? track.getArtistName() : "Unknown Artist");
            tvDuration.setText(track.getFormattedDuration());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTrackClick(track);
                }
            });
        }
    }

    // Artist ViewHolder
    class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView imgArtist;
        TextView tvArtistName;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgArtist = itemView.findViewById(R.id.imgArtist);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
        }

        public void bind(Artist artist) {
            tvArtistName.setText(artist.getName() != null ? artist.getName() : "Unknown Artist");

            // Load artist image với Glide
            if (artist.getPictureMedium() != null && !artist.getPictureMedium().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(artist.getPictureMedium())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imgArtist);
            } else {
                imgArtist.setImageResource(R.drawable.ic_launcher_foreground);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArtistClick(artist);
                }
            });
        }
    }
}