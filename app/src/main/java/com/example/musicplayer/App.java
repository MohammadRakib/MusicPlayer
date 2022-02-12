package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static final String CHANNEL_ID="musicChannel";
    private static App st;

    static List<songs> SongsList;  //initialize in MainActivity
    static ArrayList<Bitmap> img ; //initialize in PlayerActivity
    static song_table lastsong; // initialize in mainActivity in loadLastplayingsong method
    static Button btn_next, btn_previous, btn_pause, btn_loop; //initialize in PlayerActivity
    static TextView songTextLevel;//initialize in PlayerActivity
    static ImageView musicimage;//initialize in PlayerActivity
    static SeekBar songSeekbar;//initialize in PlayerActivity
    static MediaPlayer myMediaPlayer;//initialize in PlayerActivity
    static int position;//initialize in MainActivity and song_fragment
    static String sname;//initialize in PlayerActivity
    static String albumId;//initialize in PlayerActivity
    static long albumID;//initialize in PlayerActivity
    static Bitmap musicImg;//initialize in PlayerActivity
    static Handler handler;//initialize in PlayerActivity
    static Runnable runnable;//initialize in PlayerActivity
    static boolean loopOk = false;//initialize in PlayerActivity
    static List<songs> songsList  = new ArrayList<>();//initialize in PlayerActivity
    static boolean firsttimeOpen=true;
    static  boolean firsttimeOpenSongFragment=true;




    public static App getInstance() {
        return st;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CreateNotificationChannel();
        st = this;
    }

    private void CreateNotificationChannel() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel musicServiceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "MusicPlayer",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

             NotificationManager manager= getSystemService(NotificationManager.class);
             manager.createNotificationChannel(musicServiceChannel);
        }

    }
}
