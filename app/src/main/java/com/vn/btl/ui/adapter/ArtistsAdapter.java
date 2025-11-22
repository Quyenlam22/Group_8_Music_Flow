package com.vn.btl.ui.adapter;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ArtistViewHolder> {
    private List<Artist> artistList;
    private Context context;

    public ArtistsAdapter(Context context) {
        this.context = context;
        this.artistList = new ArrayList<>();
    }

    public void setData(List<Artist> artists) {
        this.artistList.clear();
        this.artistList.addAll(artists);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        Artist artist = artistList.get(position);

        holder.name.setText(artist.getArtistName());

        Glide.with(holder.itemView.getContext())
                .load(artist.getPicture())
                .placeholder(R.drawable.playlist_placeholder)
                .circleCrop()
                .into(holder.image);
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
            image = itemView.findViewById(R.id.imgArtist);
            name = itemView.findViewById(R.id.tvArtistName);
        }
    }
}