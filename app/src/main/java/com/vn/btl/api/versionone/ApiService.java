package com.vn.btl.api.versionone;

import com.vn.btl.model.album.AlbumDetailResponse;
import com.vn.btl.model.album.AlbumsResponse;
import com.vn.btl.model.playlist.PlaylistResponse;
import com.vn.btl.model.artist.ArtistResponse;
import com.vn.btl.model.track.TracksResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/musicflow/artists/random")
    Call<ArtistResponse> getRandomArtists();

    @GET("api/musicflow/search/artists")
    Call<ArtistResponse> searchArtists(@Query("q") String query);

    @GET("api/musicflow/artist/{id}/top")
    Call<TracksResponse> getTopTracksByArtist(@Path("id") long artistId);

    @GET("api/musicflow/playlists")
    Call<PlaylistResponse> getPlaylists();

    @GET("api/musicflow/artist/{id}/albums")
    Call<AlbumsResponse> getAlbumsByArtist(@Path("id") long artistId);

    @GET("api/musicflow/albums/{id}")
    Call<AlbumDetailResponse> getTracksOfAlbum(@Path("id") long albumId);

    @GET("api/musicflow/playlists/{id}/tracks")
    Call<TracksResponse> getPlaylistDetail(@Path("id") long playlistId);
    @GET("api/musicflow/search")
    Call<TracksResponse> searchTracks(@Query("q") String query);
}
