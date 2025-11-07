package com.vn.btl.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.ui.activity.UiSong;
import com.vn.btl.ui.adapter.SongsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllSongsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.rv_all_songs);
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rv.setHasFixedSize(true);

        // tránh bị BottomNav che
        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        List<UiSong> data = new ArrayList<>();
        data.add(new UiSong("Available", "Justin Bieber", R.drawable.mf_song_placeholder1));
        data.add(new UiSong("Sucker", "Jonas Brothers", R.drawable.mf_song_placeholder2));
        data.add(new UiSong("Super Bass", "Nicki Minaj", R.drawable.mf_song_placeholder3));
        data.add(new UiSong("Levitating", "Dua Lipa", R.drawable.mf_song_placeholder4));

        rv.setAdapter(new SongsAdapter(requireContext(), data));
    }
}
