package com.vn.btl.ui.adapter;

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
    private OnClearClickListener onClearClickListener;

    public interface OnClearClickListener {
        void onClearClick(String query);
    }

    public SearchHistoryAdapter(android.content.Context context, List<String> historyList) {
        this.historyList = historyList;
        this.inflater = LayoutInflater.from(context);
    }

    public void setOnClearClickListener(OnClearClickListener listener) {
        this.onClearClickListener = listener;
    }

    @Override
    public int getCount() {
        return historyList.size();
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

        // Clear button click
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

        ViewHolder(View view) {
            historyText = view.findViewById(R.id.historyText);
            clearHistoryItem = view.findViewById(R.id.clearHistoryItem);
        }
    }
}