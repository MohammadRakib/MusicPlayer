package com.example.musicplayer;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface songDao {

    @Insert
    void Insert(song_table song);

    @Query("DELETE FROM song_table WHERE playlist LIKE :playlistname")
    void Delete(String playlistname);

    @Update
    void Update(song_table song);

    @Query("SELECT * FROM song_table WHERE playlist LIKE :playlistname")
    List<song_table> findlist(String playlistname);


}
