package com.vn.btl.ui.activity;

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
import com.vn.btl.model.Track;

import java.util.List;

public class BannerTrackAdapter extends RecyclerView.Adapter<BannerTrackAdapter.VH> {

    public interface OnItemClick {
        void onClick(Track track);
    }

    private final List<Track> tracks;
    private final Context ctx;
    private final OnItemClick listener;

    public BannerTrackAdapter(Context ctx, List<Track> tracks, OnItemClick listener) {
        this.ctx = ctx;
        this.tracks = tracks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_banner_track, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Track t = tracks.get(position);

        String cover = null;
        if (t.getAlbum() != null) {
            cover = t.getAlbum().getCover_big();
            if (cover == null || cover.isEmpty()) cover = t.getAlbum().getCover_medium();
        }
        if (cover != null && !cover.isEmpty()) {
            Glide.with(ctx).load(cover).into(holder.ivBanner);
        } else {
            holder.ivBanner.setImageResource(R.drawable.mf_banner_placeholder); // thay bằng drawable placeholder của bạn
        }

        holder.tvTitle.setText(t.getTitle() != null ? t.getTitle() : "");
        String artist = (t.getArtist() != null) ? t.getArtist().getName() : "";
        holder.tvArtist.setText(artist);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(t);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle, tvArtist;
        VH(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            tvTitle = itemView.findViewById(R.id.tvBannerTitle);
            tvArtist = itemView.findViewById(R.id.tvBannerArtist);
        }
    }
}
