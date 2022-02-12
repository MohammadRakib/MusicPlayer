package com.example.musicplayer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class song_table {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String artist;
    private String datapath;
    private String duration;
    private String albumId;
    private String playlist;
    private int position;

    public song_table( String title, String artist, String datapath, String duration, String albumId,String playlist,int position) {
        this.title = title;
        this.artist = artist;
        this.datapath = datapath;
        this.duration = duration;
        this.albumId = albumId;
        this.playlist=playlist;
        this.position=position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public String getPlaylist() {
        return playlist;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDatapath() {
        return datapath;
    }

    public String getDuration() {
        return duration;
    }

    public String getAlbumId() {
        return albumId;
    }
}
