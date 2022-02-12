package com.example.musicplayer;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileDescriptor;
import java.io.IOException;


import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.App.SongsList;
import static com.example.musicplayer.App.albumID;
import static com.example.musicplayer.App.albumId;
import static com.example.musicplayer.App.btn_pause;
import static com.example.musicplayer.App.firsttimeOpen;
import static com.example.musicplayer.App.firsttimeOpenSongFragment;
import static com.example.musicplayer.App.musicImg;
import static com.example.musicplayer.App.musicimage;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.sname;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.PlayerActivity.nextSong;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.PlayerActivity.previousSong;
import static com.example.musicplayer.Playlist.toPlaylist;
import static com.example.musicplayer.PlaylistsongsShower.fromplaylist;
import static com.example.musicplayer.PlaylistsongsShower.myPlaylistSongs;


public class Song_fragment extends Fragment {

    ImageView musicimage;
    TextView musicname;
    static Button previous,next,play;
    static shareViewModel viewModel;
    Context context;
     song_table lastplaysong;


    public Bitmap getAlbumart(Long album_id, Context context) {
        Bitmap bm = null;
        try {
            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);

            ParcelFileDescriptor pfd = context.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                bm = BitmapFactory.decodeFileDescriptor(fd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragement_song,container,false);
        musicimage=v.findViewById(R.id.musicImage);
        musicname=v.findViewById(R.id.Musicname);
        previous=v.findViewById(R.id.previous);
        next=v.findViewById(R.id.next);
        play=v.findViewById(R.id.play);
        context=v.getContext();
        final PlayerActivity pause = new PlayerActivity();

        if(myMediaPlayer!=null){
            if (myMediaPlayer.isPlaying()) {
                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            } else {
                btn_pause.setBackgroundResource(R.drawable.icon_play);
                play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            }
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!firsttimeOpenSongFragment){

                    startActivity(new Intent(context, PlayerActivity.class));
                }
                else {
                    Toast.makeText(context, "play the song first", Toast.LENGTH_SHORT).show();
                }

            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firsttimeOpen){ //for first time app open
                    startActivity(new Intent(context, PlayerActivity.class));
                    firsttimeOpenSongFragment=false;
                }
                else if(firsttimeOpenSongFragment){


                    currentsongplay();
                    firsttimeOpenSongFragment=false;

                }
                else {
                    pause.pauseSong();
                }


            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                previousSong();
                if(songsList!=myPlaylistSongs) {
                    String Duration = String.valueOf(myMediaPlayer.getDuration());
                    song_table lastsong = new song_table(songsList.get(position).getTitle()
                            , songsList.get(position).getArtist()
                            , songsList.get(position).getDatapath()
                            , Duration, songsList.get(position).getAlbum()
                            , LastSong
                            , position);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LastSong, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(lastsong);
                    editor.putString("taking last song", json);
                    editor.apply();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                nextSong();
                if(songsList!=myPlaylistSongs) {
                    String Duration = String.valueOf(myMediaPlayer.getDuration());
                    song_table lastsong = new song_table(songsList.get(position).getTitle()
                            , songsList.get(position).getArtist()
                            , songsList.get(position).getDatapath()
                            , Duration, songsList.get(position).getAlbum()
                            , LastSong
                            , position);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LastSong, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(lastsong);
                    editor.putString("taking last song", json);
                    editor.apply();
                }
            }
        });

        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel= ViewModelProviders.of(getActivity()).get(shareViewModel.class);
        lastplaysong=viewModel.getLastplaysong();





        if(lastplaysong!=null && toPlaylist==false){
            if(myMediaPlayer!=null && myMediaPlayer.isPlaying()){
                myMediaPlayer.stop();
            }
            viewModel.setSongname(lastplaysong.getTitle());
            viewModel.setAlbumId(lastplaysong.getAlbumId());
            position=lastplaysong.getPosition();
            songsList=SongsList;
            play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            firsttimeOpen=true;
            firsttimeOpenSongFragment=true;
            toPlaylist=true;
        }
        else if(toPlaylist){
            if(myMediaPlayer!=null  &&  myMediaPlayer.isPlaying()){
                myMediaPlayer.stop();
            }
            viewModel.setSongname(myPlaylistSongs.get(0).getTitle());
            viewModel.setAlbumId(myPlaylistSongs.get(0).getAlbum());
            position = 0;
            songsList=myPlaylistSongs;
            play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            firsttimeOpen=true;
            firsttimeOpenSongFragment=true;
            toPlaylist=false;
        }

        else {
            viewModel.setSongname(SongsList.get(0).getTitle());
            viewModel.setAlbumId(SongsList.get(0).getAlbum());
            position = 0;
            songsList=SongsList;
            firsttimeOpen=true;
            toPlaylist=true;

        }

        viewModel.getSongname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                musicname.setText(s);
            }
        });

        viewModel.getAlbumId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                String albumId = s;
                long albumID = Long.parseLong(albumId);
                Bitmap musicImg = getAlbumart(albumID,context);
                if (musicImg!=null){
                    musicimage.setImageBitmap(musicImg);
                }
                else {
                    musicimage.setImageResource(R.drawable.coverart);
                }

            }
        });

    }

    public void currentsongplay() {

        myMediaPlayer.reset();


        sname = songsList.get(position).getTitle();
        String songpath = songsList.get(position).getDatapath();


        albumId = songsList.get(position).getAlbum();
        albumID = Long.parseLong(albumId);
        musicImg = getAlbumart(albumID, context);
        if (musicImg != null) {
            musicimage.setImageBitmap(musicImg);
        } else {
            musicimage.setImageResource(R.drawable.coverart);
        }


        try {
            myMediaPlayer.setDataSource(songpath);
            myMediaPlayer.prepare();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalStateException e1) {
            e1.printStackTrace();
        }


    }


}


