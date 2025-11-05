package com.vn.btl.ui.activity;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacePx;
    public SpaceItemDecoration(int spacePx) { this.spacePx = spacePx; }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        outRect.right = spacePx;
        if (pos == 0) outRect.left = spacePx;
    }
}
