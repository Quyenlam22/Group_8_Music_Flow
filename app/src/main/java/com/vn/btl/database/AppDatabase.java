package com.vn.btl.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.vn.btl.model.Artist;
import com.vn.btl.model.FavoriteSong; // THÊM IMPORT NÀY

@Database(entities = {Artist.class, FavoriteSong.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract ArtistDAO artistDAO();
    public abstract FavoriteSongDAO favoriteSongDAO();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "music_app_db"
                            )
                            .fallbackToDestructiveMigration() // THÊM DÒNG NÀY ĐỂ RESET DATABASE
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}