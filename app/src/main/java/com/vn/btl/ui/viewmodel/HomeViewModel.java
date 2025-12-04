package com.vn.btl.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.vn.btl.model.track.TrackResponse;
import com.vn.btl.repository.MusicRepository;
import com.vn.btl.model.album.AlbumResponse;

public class HomeViewModel extends ViewModel {
    private final MusicRepository repository = new MusicRepository();
    private LiveData<TrackResponse> topTracks;
    private LiveData<AlbumResponse> topAlbums;

    public void loadTopTracks() {
        topTracks = repository.getTopTracks(10);
    }
    public void loadTopAlbums() {
        topAlbums = repository.getTopAlbums(10);
    }

    public LiveData<TrackResponse> getTopTracks() {
        return topTracks;
    }
    public LiveData<AlbumResponse> getTopAlbums() {
        return topAlbums;
    }
}
