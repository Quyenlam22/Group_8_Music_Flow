package com.vn.btl.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.vn.btl.R;

import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter {
    private List<String> historyList;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;
    private OnClearClickListener onClearClickListener;

    public interface OnItemClickListener {
        void onItemClick(String query);
    }

    public interface OnClearClickListener {
        void onClearClick(String query);
    }

    public SearchHistoryAdapter(Context context, List<String> historyList) {
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnClearClickListener(OnClearClickListener listener) {
        this.onClearClickListener = listener;
    }

    @Override
    public int getCount() {
        return historyList != null ? historyList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_search_history, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String historyItem = historyList.get(position);
        holder.historyText.setText(historyItem);

        // Xử lý click vào toàn bộ item
        convertView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(historyItem);
            }
        });

        // Xử lý click vào nút clear
        holder.clearHistoryItem.setOnClickListener(v -> {
            if (onClearClickListener != null) {
                onClearClickListener.onClearClick(historyItem);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView historyText;
        ImageView clearHistoryItem;
        View rootView; // Thêm rootView để xử lý click toàn bộ item

        ViewHolder(View view) {
            rootView = view;
            historyText = view.findViewById(R.id.historyText);
            clearHistoryItem = view.findViewById(R.id.clearHistoryItem);
        }
    }
}