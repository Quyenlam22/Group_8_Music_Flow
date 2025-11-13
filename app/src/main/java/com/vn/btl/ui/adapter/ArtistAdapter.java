package com.vn.btl.ui.adapter;

import android.content.Context;
import android.graphics.Color;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private List<Artist> artistList;
    private Set<Long> selectedIds = new HashSet<>();
    private OnArtistClickListener listener;
    private Context context;

    public interface OnArtistClickListener {
        void onArtistClick(Artist artist);
    }

    public ArtistAdapter(List<Artist> artistList, OnArtistClickListener listener) {
        this.artistList = artistList;
        this.listener = listener;
    }

    public ArtistAdapter(List<Artist> artistList, Context context) {
        this.artistList = artistList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_choose_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);
        holder.name.setText(artist.getArtistName());
        Glide.with(holder.itemView.getContext())
                .load(artist.getPicture())
                .circleCrop()
                .into(holder.image);

        // Cập nhật màu khi item được chọn/bỏ chọn
        if (artist.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_artist_item_selected);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_artist_item);
        }

        holder.itemView.setOnClickListener(v -> {
            artist.setSelected(!artist.isSelected()); // toggle chọn
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.artistImage);
            name = itemView.findViewById(R.id.artistName);
        }
    }
}
