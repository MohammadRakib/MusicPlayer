package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.App.albumId;
import static com.example.musicplayer.App.firsttimeOpen;
import static com.example.musicplayer.App.firsttimeOpenSongFragment;
import static com.example.musicplayer.App.musicImg;
import static com.example.musicplayer.App.musicimage;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.App.sname;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.Playlist.playlistAllsongs;


public class PlaylistsongsShower extends Fragment {

    PlaylistAdapter songAdapter;
    RecyclerView recyclerView;
    long albumID;
    Context context;
    static ArrayList<Bitmap> playlistsongImg;
    static ArrayList<songs> myPlaylistSongs;
    String songname, artistname, songDatapath, songDuration, songAlbumID;
    long Songid;
    static boolean fromplaylist=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_playlistsongs_shower, container, false);
        context=v.getContext();
        recyclerView = v.findViewById(R.id.playlistSongview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        playlistsongImg= new ArrayList<>();
        myPlaylistSongs=new ArrayList<>();
        for(int i=0;i<playlistAllsongs.size();i++){
            albumID= Long.parseLong(playlistAllsongs.get(i).getAlbumId());
            Bitmap image=getAlbumart(albumID,context);
            playlistsongImg.add(image);

            Songid=playlistAllsongs.get(i).getId();
            songname=playlistAllsongs.get(i).getTitle();
            artistname=playlistAllsongs.get(i).getArtist();
            songDatapath=playlistAllsongs.get(i).getDatapath();
            songDuration=playlistAllsongs.get(i).getDuration();
            songAlbumID=playlistAllsongs.get(i).getAlbumId();
            songs sng = new songs(Songid, songname, artistname, songDatapath, songDuration, songAlbumID);
            myPlaylistSongs.add(sng);
        }
        songsList=myPlaylistSongs;



        songAdapter = new PlaylistAdapter(playlistAllsongs, context, playlistsongImg);
        recyclerView.setAdapter(songAdapter);

        songAdapter.SetOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int Position) {

                fromplaylist=true;
                position=Position;
                if(firsttimeOpen){
                    startActivity(new Intent(context, PlayerActivity.class));

                }
                else {
                    currentsongplay();
                    firsttimeOpenSongFragment=false;
                    if(songsList!=myPlaylistSongs) {
                        String Duration = String.valueOf(myMediaPlayer.getDuration());
                        song_table lastsong = new song_table(playlistAllsongs.get(position).getTitle()
                                , playlistAllsongs.get(position).getArtist()
                                , playlistAllsongs.get(position).getDatapath()
                                , Duration, playlistAllsongs.get(position).getAlbumId()
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

            }
        });



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fromplaylist=false;
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

    public void currentsongplay() {

        myMediaPlayer.reset();


        sname = playlistAllsongs.get(position).getTitle();
        String songpath = playlistAllsongs.get(position).getDatapath();


        albumId = playlistAllsongs.get(position).getAlbumId();
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
