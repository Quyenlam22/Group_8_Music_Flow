package com.vn.btl.repository;

import com.vn.btl.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    public List<Song> getAllSongs() {
        List<Song> list = new ArrayList<>();
        list.add(new Song("Shape of You", "Ed Sheeran"));
        list.add(new Song("Blinding Lights", "The Weeknd"));
        list.add(new Song("Attention", "Charlie Puth"));
        return list;
    }
}