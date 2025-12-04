package com.vn.btl.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.vn.btl.model.something.FavoriteSong;
import java.util.List;

@Dao
public interface FavoriteSongDAO {
    @Insert
    void insert(FavoriteSong song);

    @Query("DELETE FROM favorite_songs WHERE title = :title AND artist = :artist")
    void deleteByTitleAndArtist(String title, String artist);

    @Query("SELECT * FROM favorite_songs WHERE title = :title AND artist = :artist")
    FavoriteSong getByTitleAndArtist(String title, String artist);

    @Query("SELECT * FROM favorite_songs ORDER BY addedTime DESC")
    List<FavoriteSong> getAll();
    @Query("SELECT * FROM favorite_songs WHERE userUid = :uid")
    List<FavoriteSong> getFavoriteSongByUser(String uid);
    @Query("DELETE FROM favorite_songs WHERE userUid = :uid")
    void deleteFavoriteSongForUser(String uid);
}