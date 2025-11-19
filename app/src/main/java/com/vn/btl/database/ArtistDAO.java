package com.vn.btl.database;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.vn.btl.model.Artist;

import java.util.List;
@Dao
public interface ArtistDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Artist artist);
    @Delete
    void delete(Artist artist);

    @Query("SELECT * FROM artists WHERE artistId = :id LIMIT 1")
    Artist findByArtistId(long id);

    @Query("SELECT * FROM artists")
    List<Artist> getAll();

    @Query("DELETE FROM artists")
    void deleteAll();
}
