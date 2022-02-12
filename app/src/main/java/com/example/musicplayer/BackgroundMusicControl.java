package com.example.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.App.btn_loop;
import static com.example.musicplayer.App.loopOk;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.PlayerActivity.nextSong;
import static com.example.musicplayer.PlayerActivity.previousSong;
import static com.example.musicplayer.PlaylistsongsShower.myPlaylistSongs;


public class BackgroundMusicControl extends BroadcastReceiver {


    PlayerActivity pause = new PlayerActivity();

    @Override
    public void onReceive(Context context, Intent intent) {


        if ("com.example.musicplayer_next".equals(intent.getAction())) {
            nextSong();
            if(songsList!=myPlaylistSongs) {
                String Duration = String.valueOf(myMediaPlayer.getDuration());
                song_table lastsong = new song_table(songsList.get(position).getTitle()
                        , songsList.get(position).getArtist()
                        , songsList.get(position).getDatapath()
                        , Duration, songsList.get(position).getAlbum()
                        , LastSong
                        , position);
                SharedPreferences sharedPreferences = context.getSharedPreferences(LastSong, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(lastsong);
                editor.putString("taking last song", json);
                editor.apply();
            }

        } else if ("com.example.musicplayer_pause".equals(intent.getAction())) {


            pause.pauseSong();


        } else if ("com.example.musicplayer_previous".equals(intent.getAction())) {

            previousSong();
            if(songsList!=myPlaylistSongs) {
                String Duration = String.valueOf(myMediaPlayer.getDuration());
                song_table lastsong = new song_table(songsList.get(position).getTitle()
                        , songsList.get(position).getArtist()
                        , songsList.get(position).getDatapath()
                        , Duration, songsList.get(position).getAlbum()
                        , LastSong
                        , position);
                SharedPreferences sharedPreferences = context.getSharedPreferences(LastSong, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(lastsong);
                editor.putString("taking last song", json);
                editor.apply();

            }

        } else if ("com.example.musicplayer_close".equals(intent.getAction())) {

            Intent MusicserviceIntent = new Intent(context, MusicplayerService.class);
            context.stopService(MusicserviceIntent);

        } else if ("com.example.musicplayer_loop".equals(intent.getAction())) {


            if (loopOk) {
                loopOk = false;
                btn_loop.setBackgroundResource(R.drawable.ic_loop_red_24dp);
                Toast.makeText(context, "Loop song CANCLE", Toast.LENGTH_SHORT).show();
            } else {
                loopOk = true;
                btn_loop.setBackgroundResource(R.drawable.ic_loop_green_24dp);
                Toast.makeText(context, "Loop song OK", Toast.LENGTH_SHORT).show();
            }

        }


    }


}



