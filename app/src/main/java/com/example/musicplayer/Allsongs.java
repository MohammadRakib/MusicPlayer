package com.example.musicplayer;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.App.SongsList;
import static com.example.musicplayer.App.albumID;
import static com.example.musicplayer.App.albumId;
import static com.example.musicplayer.App.firsttimeOpen;
import static com.example.musicplayer.App.firsttimeOpenSongFragment;
import static com.example.musicplayer.App.img;
import static com.example.musicplayer.App.musicImg;
import static com.example.musicplayer.App.musicimage;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.App.sname;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.Playlist.toPlaylist;

public class Allsongs extends Fragment {


    songAdapter songAdapter;
    RecyclerView recyclerView;
    long albumID;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_allsongs,container,false);
        context=v.getContext();

        recyclerView = v.findViewById(R.id.mySongListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        runtimepermission();
        //songsList = SongsList;
        return v;
    }


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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void runtimepermission() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        display();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();

                    }
                }).check();

    }

    public void display() {



        songAdapter = new songAdapter(SongsList, context, img);
        recyclerView.setAdapter(songAdapter);


        songAdapter.SetOnItemClickListener(new songAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int Position) {

                position=Position;
                if(firsttimeOpen){
                    songsList = SongsList;
                    toPlaylist=false;
                    startActivity(new Intent(context, PlayerActivity.class));

                }
                else {
                    songsList = SongsList;
                    currentsongplay();
                    firsttimeOpenSongFragment=false;
                    toPlaylist=false;
                    String Duration = String.valueOf(myMediaPlayer.getDuration());
                    song_table lastsong=new song_table(songsList.get(position).getTitle()
                            ,songsList.get(position).getArtist()
                            ,songsList.get(position).getDatapath()
                            ,Duration,songsList.get(position).getAlbum()
                            ,LastSong
                            ,position);
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(LastSong,MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(lastsong);
                    editor.putString("taking last song",json);
                    editor.apply();
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
