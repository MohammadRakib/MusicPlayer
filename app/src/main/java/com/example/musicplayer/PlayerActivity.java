package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.musicplayer.App.SongsList;
import static com.example.musicplayer.App.firsttimeOpen;
import static com.example.musicplayer.App.firsttimeOpenSongFragment;
import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.Playlist.playlistname;
import static com.example.musicplayer.PlaylistsongsShower.myPlaylistSongs;
import static com.example.musicplayer.Song_fragment.play;
import static com.example.musicplayer.Song_fragment.viewModel;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.handler;
import static com.example.musicplayer.App.songSeekbar;
import static com.example.musicplayer.App.albumID;
import static com.example.musicplayer.App.albumId;
import static com.example.musicplayer.App.btn_loop;
import static com.example.musicplayer.App.btn_next;
import static com.example.musicplayer.App.btn_pause;
import static com.example.musicplayer.App.btn_previous;
import static com.example.musicplayer.App.runnable;
import static com.example.musicplayer.App.loopOk;
import static com.example.musicplayer.App.musicimage;
import static com.example.musicplayer.App.musicImg;
import static com.example.musicplayer.App.img;
import static com.example.musicplayer.App.sname;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.App.songTextLevel;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.PlaylistsongsShower.fromplaylist;


public class PlayerActivity extends AppCompatActivity {

    //all static variable are declared in App class

    Button btn_equilizer;
    Button addtoplaylistButtonPl;

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


    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btn_next = findViewById(R.id.next);
        btn_previous = findViewById(R.id.previous);
        btn_pause = findViewById(R.id.pause);
        btn_loop = findViewById(R.id.Loop);
        addtoplaylistButtonPl = findViewById(R.id.addtoplaylistfromplayer);
        btn_equilizer=findViewById(R.id.equalizer);
        songTextLevel = findViewById(R.id.songlevel);
        songSeekbar = findViewById(R.id.seekBar);
        musicimage = findViewById(R.id.MusicImage);
        handler = new Handler();

        if(myMediaPlayer!=null){
            if (myMediaPlayer.isPlaying()) {
                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
            } else {
                btn_pause.setBackgroundResource(R.drawable.icon_play);
                play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            }
        }


        if(firsttimeOpen){
            myMediaPlayer = new MediaPlayer();
        }

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        myMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                songSeekbar.setMax(myMediaPlayer.getDuration());
                albumId = songsList.get(position).getAlbum();
                albumID = Long.parseLong(albumId);
                musicImg = getAlbumart(albumID, getApplicationContext());
                if (musicImg != null) {
                    musicimage.setImageBitmap(musicImg);
                } else {
                    musicimage.setImageResource(R.drawable.coverart);
                }


                if (myMediaPlayer!=null){
                    viewModel.setSongname(sname);
                    viewModel.setAlbumId(albumId);
                }
                play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                StartmusicService(position, sname);

            }
        });

        myMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (!loopOk) {
                    nextSong();
                } else {
                    songSeekbar.setMax(myMediaPlayer.getDuration());
                    myMediaPlayer.start();
                    changeSeekbar();
                }
            }
        });




            if(firsttimeOpen){
                myMediaPlayer.stop();
                currentsongplay();
                btn_pause.setBackgroundResource(R.drawable.icon_pause);
                play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                songTextLevel.setText(sname);
                songTextLevel.setSelected(true);
                firsttimeOpen=false;
                firsttimeOpenSongFragment=false;
                String Duration = String.valueOf(myMediaPlayer.getDuration());
                song_table lastsong=new song_table(songsList.get(position).getTitle()
                        ,songsList.get(position).getArtist()
                        ,songsList.get(position).getDatapath()
                        ,Duration,songsList.get(position).getAlbum()
                        ,LastSong
                        ,position);
                SharedPreferences sharedPreferences = getSharedPreferences(LastSong,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(lastsong);
                editor.putString("taking last song",json);
                editor.apply();
            }
            else {
                sname = songsList.get(position).getTitle();
                songSeekbar.setMax(myMediaPlayer.getDuration());
                albumId = songsList.get(position).getAlbum();
                albumID = Long.parseLong(albumId);
                musicImg = getAlbumart(albumID, getApplicationContext());
                if (musicImg != null) {
                    musicimage.setImageBitmap(musicImg);
                } else {
                    musicimage.setImageResource(R.drawable.coverart);
                }
                songTextLevel.setText(sname);
                songTextLevel.setSelected(true);


            }








        //Playing song from songlist


        songSeekbar.getProgressDrawable().setColorFilter(getResources().getColor
                (R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);


        songSeekbar.getThumb().setColorFilter(getResources().getColor
                (R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    myMediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myMediaPlayer.seekTo(seekBar.getProgress());
            }
        });


        btn_pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pauseSong();

            }
        });


        btn_next.setOnClickListener(new View.OnClickListener() {
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
                    SharedPreferences sharedPreferences = getSharedPreferences(LastSong, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(lastsong);
                    editor.putString("taking last song", json);
                    editor.apply();
                }
            }
        });


        btn_previous.setOnClickListener(new View.OnClickListener() {
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
                    SharedPreferences sharedPreferences = getSharedPreferences(LastSong, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(lastsong);
                    editor.putString("taking last song", json);
                    editor.apply();
                }
            }
        });

        btn_equilizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PlayerActivity.this,EqualizerActivity.class);
                startActivity(i);
            }
        });

        addtoplaylistButtonPl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playlistname.isEmpty()){
                    Toast.makeText(PlayerActivity.this, "Please create a playlist first", Toast.LENGTH_SHORT).show();
                }
                else {
                    AddtoPlaylistDialog addtoPlaylistDialog =new AddtoPlaylistDialog();
                    addtoPlaylistDialog.show(getSupportFragmentManager(),"Add to playlist");
                }

            }
        });


    }

    public void pauseSong() {
        songSeekbar.setMax(myMediaPlayer.getDuration());
        if (myMediaPlayer.isPlaying()) {
            btn_pause.setBackgroundResource(R.drawable.icon_play);
            play.setBackgroundResource(R.drawable.ic_play_circle_filled_black_24dp);
            myMediaPlayer.pause();


        } else {

              if(myMediaPlayer!=null){
                  btn_pause.setBackgroundResource(R.drawable.icon_pause);
                  myMediaPlayer.start();
                  play.setBackgroundResource(R.drawable.ic_pause_circle_filled_black_24dp);
                  changeSeekbar();
              }


        }
    }

    public void changeSeekbar() {

        songSeekbar.setProgress(myMediaPlayer.getCurrentPosition());
        if (myMediaPlayer.isPlaying()) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    changeSeekbar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }

    }


    public void currentsongplay() {

        myMediaPlayer.reset();


           sname = songsList.get(position).getTitle();
           String songpath = songsList.get(position).getDatapath();


           albumId = songsList.get(position).getAlbum();
           albumID = Long.parseLong(albumId);
           musicImg = getAlbumart(albumID, this);
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


    public static void nextSong() {

        try {

            myMediaPlayer.stop();
            myMediaPlayer.reset();
            if (position == songsList.size() - 1) {
                position = songsList.size() - 1;
            } else {
                position++;
            }

            String songpath = songsList.get(position).getDatapath();


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
            sname = songsList.get(position).getTitle();

            songTextLevel.setText(sname);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void previousSong() {

        try {

            myMediaPlayer.stop();
            myMediaPlayer.reset();

            if (position != 0) {
                position--;
            }

            String songpath = songsList.get(position).getDatapath();


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
            sname = songsList.get(position).getTitle();

            songTextLevel.setText(sname);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void LoopButtonclicklistener(View v) {

        if (loopOk) {
            loopOk = false;
            btn_loop.setBackgroundResource(R.drawable.ic_loop_red_24dp);
        } else {
            loopOk = true;
            btn_loop.setBackgroundResource(R.drawable.ic_loop_green_24dp);
        }
    }


    public void StartmusicService(int position, String songname) {

        Intent MusicserviceIntent = new Intent(this, MusicplayerService.class);
        String artist = songsList.get(position).getArtist();
        MusicserviceIntent.putExtra("songname", songname).putExtra("artist", artist).putExtra("albumId", albumId);


        startService(MusicserviceIntent);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
