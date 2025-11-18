package com.vn.btl.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.materialswitch.MaterialSwitch;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnToggle { void onChanged(boolean checked); }
    public interface OnClick { void onClick(); }

    public static class Item {
        public static final int TYPE_NORMAL = 0;
        public static final int TYPE_SWITCH = 1;

        public final int type;
        @DrawableRes public final int iconRes;
        public final String title;
        public final boolean checked;
        public final boolean showChevron;
        public final OnToggle onToggle;
        public final OnClick onClick;

        private Item(int type, @DrawableRes int icon, String title,
                     boolean checked, boolean showChevron,
                     OnToggle onToggle, OnClick onClick) {
            this.type = type; this.iconRes = icon; this.title = title;
            this.checked = checked; this.showChevron = showChevron;
            this.onToggle = onToggle; this.onClick = onClick;
        }

        public static Item normal(@DrawableRes int icon, String title, boolean showChevron, OnClick cb) {
            return new Item(TYPE_NORMAL, icon, title, false, showChevron, null, cb);
        }
        public static Item sw(@DrawableRes int icon, String title, boolean checked, OnToggle cb) {
            return new Item(TYPE_SWITCH, icon, title, checked, false, cb, null);
        }
    }

    private final List<Item> items;
    public SettingsAdapter(List<Item> items) { this.items = items; }

    @Override public int getItemViewType(int position) { return items.get(position).type; }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (type == Item.TYPE_SWITCH) {
            return new SwitchVH(inf.inflate(R.layout.row_setting_switch, parent, false));
        } else {
            return new NormalVH(inf.inflate(R.layout.row_setting_item, parent, false));
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        Item it = items.get(pos);
        if (h instanceof SwitchVH) {
            SwitchVH v = (SwitchVH) h;
            v.icon.setImageResource(it.iconRes);
            v.title.setText(it.title);
            v.sw.setChecked(it.checked);
            v.sw.setOnCheckedChangeListener((b, c) -> { if (it.onToggle != null) it.onToggle.onChanged(c); });
        } else {
            NormalVH v = (NormalVH) h;
            v.icon.setImageResource(it.iconRes);
            v.title.setText(it.title);
            v.chevron.setVisibility(it.showChevron ? View.VISIBLE : View.GONE);
            v.itemView.setOnClickListener(view -> { if (it.onClick != null) it.onClick.onClick(); });
        }
    }

    @Override public int getItemCount() { return items == null ? 0 : items.size(); }

    static class NormalVH extends RecyclerView.ViewHolder {
        ImageView icon, chevron; TextView title;
        NormalVH(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.im_icon);
            title = v.findViewById(R.id.tv_title);
            chevron = v.findViewById(R.id.im_chevron);
        }
    }

    static class SwitchVH extends RecyclerView.ViewHolder {
        ImageView icon; TextView title; MaterialSwitch sw;
        SwitchVH(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.im_icon);
            title = v.findViewById(R.id.tv_title);
            sw = v.findViewById(R.id.sw_toggle);
        }
    }
}
