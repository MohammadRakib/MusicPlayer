package com.example.musicplayer;

import java.io.Serializable;

public class songs implements Serializable {


    private long id;
    private String title;
    private String artist;
    private String datapath;
    private String duration;
    private String albumId;

    public songs(long songID, String songTitle, String songArtist, String songDatapath, String songDuration, String songAlbumId) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        datapath = songDatapath;
        duration = songDuration;
        albumId = songAlbumId;
    }

    public String getAlbum() {
        return albumId;
    }

    public String getDuration() {
        return duration;
    }

    public String getDatapath() {
        return datapath;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
