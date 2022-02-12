package com.example.musicplayer;

import android.app.Application;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import static com.example.musicplayer.App.lastsong;

public class shareViewModel extends AndroidViewModel {

    private MutableLiveData<String> songname=new MutableLiveData<>();
    private MutableLiveData<String> albumId=new MutableLiveData<>();
    private MusicRepository musicRepository;
    private song_table Lastplaysong;

    public shareViewModel(@NonNull Application application) {
        super(application);
        musicRepository=new MusicRepository(application);
        Lastplaysong=lastsong;

    }


    public void insert(song_table song) {
        musicRepository.insert(song);
    }

    public void update(song_table song) {
        musicRepository.update(song);
    }

    public void delete(String playlistname) {
        musicRepository.delete(playlistname);
    }

    public void findlist(String playlistname){
        musicRepository.findlist(playlistname);
    }


    public song_table getLastplaysong() {
        return Lastplaysong;
    }

    public void setSongname(String songname){
        this.songname.setValue(songname);
    }

    public LiveData<String> getSongname(){
        return songname;
    }

    public void setAlbumId(String albumId){
        this.albumId.setValue(albumId);
    }

    public LiveData<String> getAlbumId(){
        return albumId;
    }


}
