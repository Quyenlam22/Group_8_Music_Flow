package com.vn.btl.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.model.OnboardingItem;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {

    private final List<OnboardingItem> onboardingItems;
    private final OnNextClickListener listener;

    public interface OnNextClickListener {
        void onNextClicked(int position);
    }

    public OnboardingAdapter(List<OnboardingItem> onboardingItems, OnNextClickListener listener) {
        this.onboardingItems = onboardingItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_onboarding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OnboardingItem item = onboardingItems.get(position);
        holder.imageView.setImageResource(item.getImageRes());
        holder.textTitle.setText(item.getTitle());
        holder.textDescription.setText(item.getDescription());

        holder.buttonNext.setOnClickListener(v -> listener.onNextClicked(position));

        // Đổi text nút khi tới trang cuối
        if (position == onboardingItems.size() - 1) {
            holder.buttonNext.setText("Bắt đầu");
        } else {
            holder.buttonNext.setText("Tiếp tục");
        }
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle, textDescription;
        Button buttonNext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageOnboard);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            buttonNext = itemView.findViewById(R.id.buttonNext);
        }
    }
}