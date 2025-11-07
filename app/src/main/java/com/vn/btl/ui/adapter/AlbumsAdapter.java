package com.vn.btl.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vn.btl.R;
import com.vn.btl.ui.activity.UiAlbum;

import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.VH> {
    private final List<UiAlbum> data;
    public AlbumsAdapter(List<UiAlbum> data) { this.data = data; }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_circle, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        UiAlbum a = data.get(pos);
        h.title.setText(a.title);
        h.artist.setText(a.artist);
        h.cover.setImageResource(a.coverRes);
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView cover; TextView title, artist;
        VH(View v) {
            super(v);
            cover = v.findViewById(R.id.imgCover);
            title = v.findViewById(R.id.tvAlbumTitle);
            artist = v.findViewById(R.id.tvArtist);
        }
    }
}
