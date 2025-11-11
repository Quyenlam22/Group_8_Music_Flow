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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.vn.btl.R;
import com.vn.btl.model.Albums;

import java.util.List;

public class AlbumsAdapterVer2 extends RecyclerView.Adapter<AlbumsAdapterVer2.AlbumViewHolder> {
    private final List<Albums> albumsList;
    private Context context;

    public AlbumsAdapterVer2(List<Albums> albumsList, Context context) {
        this.albumsList = albumsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumsAdapterVer2.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_circle, parent, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsAdapterVer2.AlbumViewHolder holder, int position) {
        Albums a = albumsList.get(position);
        holder.title.setText(a.getTitle());
        holder.artist.setText(a.getArtistName());

        // Load ảnh album từ URL
        Glide.with(holder.itemView.getContext())
                .load(a.getCover())
                .placeholder(R.drawable.mf_album_placeholder)
                .circleCrop()
                .into(holder.cover);
    }

    @Override
    public int getItemCount() {
        return albumsList.size();
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title, artist;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.imgCover);
            title = itemView.findViewById(R.id.tvAlbumTitle);
            artist = itemView.findViewById(R.id.tvArtist);
        }
    }
}
