package com.vn.btl.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vn.btl.ui.fragment.AllSongsFragment;
import com.vn.btl.ui.fragment.PlaylistsFragment;
import com.vn.btl.ui.fragment.AlbumsFragment;
import com.vn.btl.ui.fragment.ArtistsFragment;

public class SongsPagerAdapter extends FragmentStateAdapter {

    public SongsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new AllSongsFragment();
            case 1: return new PlaylistsFragment();
            case 2: return new AlbumsFragment();
            case 3: return new ArtistsFragment();
            default: return new AllSongsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
