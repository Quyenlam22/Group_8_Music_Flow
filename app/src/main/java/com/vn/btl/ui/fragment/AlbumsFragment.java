package com.vn.btl.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vn.btl.R;
import com.vn.btl.ui.activity.UiSong;
import com.vn.btl.ui.adapter.SongsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlbumsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = root.findViewById(R.id.rv_albums);
        rv.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        rv.setHasFixedSize(true);

        int bottomPad = (int) (requireContext().getResources().getDisplayMetrics().density * 72);
        rv.setClipToPadding(false);
        rv.setPadding(8, 8, 8, bottomPad);

        // title = tên album, artist = nghệ sĩ
        List<UiSong> data = new ArrayList<>();
//        data.add(new UiSong("Cornelia Street", "Taylor Swift", R.drawable.mf_album_placeholder1));
//        data.add(new UiSong("For You", "Laura Melina", R.drawable.mf_album_placeholder2));
//        data.add(new UiSong("Blue Hour", "TXT", R.drawable.mf_album_placeholder3));
//        data.add(new UiSong("Midnights", "Taylor Swift", R.drawable.mf_album_placeholder));

        rv.setAdapter(new SongsAdapter(requireContext(), data));
    }
}
