package com.example.musicplayer;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileDescriptor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import android.net.Uri;
import android.database.Cursor;
import android.view.MenuItem;


import static com.example.musicplayer.App.albumID;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.App.SongsList;
import static com.example.musicplayer.App.img;
import static com.example.musicplayer.App.lastsong;
import static com.example.musicplayer.Playlist.playlistname;
import static com.example.musicplayer.Playlist.toPlaylist;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //all static variable are declared in App class

    private DrawerLayout drawerLayout;
    Song_fragment song_fragment;
    Allsongs allsongs;
    Playlist playlist;
    public static final String playlistfoldername = "playlistfoldername";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = new ArrayList<>();
        SongsList = new ArrayList<>();
        playlistname=new ArrayList<>();
        loadAllsongs();
        loadplaylistfolder();

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout=findViewById(R.id.drawer);
        NavigationView navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        allsongs=new Allsongs();
        getSupportFragmentManager().beginTransaction().replace(R.id.allsonglistcontainer,allsongs).commit();
        LoadLastPlayingSong();
        song_fragment=new Song_fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragement_song_container,song_fragment).commit();
        navigationView.setCheckedItem(R.id.allsong);

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    public  void LoadLastPlayingSong(){
        SharedPreferences sharedPreferences = getSharedPreferences(LastSong,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("taking last song",null);
        Type type = new TypeToken<song_table>(){}.getType();
        lastsong = gson.fromJson(json,type);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.allsong:
                allsongs=new Allsongs();
                toPlaylist=false;
                getSupportFragmentManager().beginTransaction().replace(R.id.allsonglistcontainer,allsongs).commit();
                break;
            case R.id.playlist:
                 playlist=new Playlist();

                getSupportFragmentManager().beginTransaction().replace(R.id.allsonglistcontainer,playlist).commit();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadAllsongs(){
        String songname, artistname, songDatapath, songDuration, songAlbumID;
        long Songid;
        int audioNameIndex, audionArtistNameIndex, audioIdIndex, DatapathIndex, DurationIndex, Album1Index;
        songs sng;
        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM_ID};


        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, "duration > 10000", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    audioNameIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                    songname = audioCursor.getString(audioNameIndex);
                    audionArtistNameIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    artistname = audioCursor.getString(audionArtistNameIndex);
                    audioIdIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
                    Songid = audioCursor.getLong(audioIdIndex);
                    DatapathIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                    songDatapath = audioCursor.getString(DatapathIndex);
                    DurationIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                    songDuration = audioCursor.getString(DurationIndex);
                    Album1Index = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
                    songAlbumID = audioCursor.getString(Album1Index);

                    sng = new songs(Songid, songname, artistname, songDatapath, songDuration, songAlbumID);

                    albumID = Long.parseLong(songAlbumID);
                    img.add(getAlbumart(albumID, this));
                    SongsList.add(sng);

                } while (audioCursor.moveToNext());
            }
        }
        if (audioCursor != null) {
            audioCursor.close();
        }
    }

    private void loadplaylistfolder() {

        SharedPreferences sharedPreferences = getSharedPreferences(playlistfoldername,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("taking playlistfoldername",null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(gson.fromJson(json,type) != null){

            playlistname = gson.fromJson(json,type);

        }



    }

}



