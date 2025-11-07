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

public class ArtistsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.rv_artists);
        rv.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        // title = tên nghệ sĩ, artist = thống kê
        List<UiSong> data = new ArrayList<>();
        data.add(new UiSong("Beyonce", "4 albums • 38 songs", R.drawable.artist1));
        data.add(new UiSong("Bebe Rexha", "2 albums • 17 songs", R.drawable.artist2));
        data.add(new UiSong("Maroon 5", "5 albums • 46 songs", R.drawable.artist3));

        rv.setAdapter(new SongsAdapter(requireContext(), data));
    }
}
