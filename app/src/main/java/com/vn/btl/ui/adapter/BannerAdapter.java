package com.vn.btl.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vn.btl.R;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.VH> {
    private final List<Integer> images;

    public BannerAdapter(List<Integer> images) {
        this.images = images;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView iv = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_banner_image, parent, false);
        return new VH(iv);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        h.image.setImageResource(images.get(pos));
    }

    @Override public int getItemCount() { return images.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        VH(View itemView) {
            super(itemView);
            image = (ImageView) itemView;
        }
    }
}
