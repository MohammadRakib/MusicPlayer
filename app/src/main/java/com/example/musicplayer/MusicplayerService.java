package com.example.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import android.support.v4.media.session.MediaSessionCompat;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileDescriptor;


import static com.example.musicplayer.App.CHANNEL_ID;
import static com.example.musicplayer.App.firsttimeOpenSongFragment;
import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.PlaylistsongsShower.myPlaylistSongs;


public class MusicplayerService extends Service {

    public static final String LastSong="lastPlaysong";
    MediaSessionCompat mediaSession;
    BackgroundMusicControl backgroundMusicControl;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSessionCompat(this, "mediasession");
        backgroundMusicControl = new BackgroundMusicControl();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.musicplayer_close");
        filter.addAction("com.example.musicplayer_next");
        filter.addAction("com.example.musicplayer_pause");
        filter.addAction("com.example.musicplayer_previous");
        filter.addAction("com.example.musicplayer_loop");
        registerReceiver(backgroundMusicControl, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        int duration=myMediaPlayer.getDuration();
        String Duration= String.valueOf(duration);
        myMediaPlayer.stop();
        unregisterReceiver(backgroundMusicControl);
        if(songsList!=myPlaylistSongs){
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

        firstcount=0;
        firsttimeOpenSongFragment=true;

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
    public int onStartCommand(Intent intent, int flags, int startId) {

        getAudiofocusAndPlay();
        String songname = intent.getStringExtra("songname");
        String artist = intent.getStringExtra("artist");
        String albumId = intent.getStringExtra("albumId");
        long albumID = Long.parseLong(albumId);
        Bitmap musicImg = getAlbumart(albumID, this);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent BacktoTheMplayerpendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent musicBackGroundCloseIntent = new Intent("com.example.musicplayer_close");
        PendingIntent musicBackGroundClosePendingIntent = PendingIntent.getBroadcast(this, 0, musicBackGroundCloseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musicBackGroundnextIntent = new Intent("com.example.musicplayer_next");
        PendingIntent musicBackgroundnextPandindIntent = PendingIntent.getBroadcast(this, 0, musicBackGroundnextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musicBackGroundpreviousIntent = new Intent("com.example.musicplayer_previous");
        PendingIntent musicBackgroundpreviousPandindIntent = PendingIntent.getBroadcast(this, 0, musicBackGroundpreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musicBackGroundpauseIntent = new Intent("com.example.musicplayer_pause");
        PendingIntent musicBackgroundpausePandindIntent = PendingIntent.getBroadcast(this, 0, musicBackGroundpauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent musicBackGroundLoopIntent = new Intent("com.example.musicplayer_loop");
        PendingIntent musicBackgroundLoopPandindIntent = PendingIntent.getBroadcast(this, 0, musicBackGroundLoopIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        Notification musicNotifacationBr = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songname)
                .setContentText(artist)
                .setSmallIcon(R.drawable.ic_library_music_black_24dp)
                .setLargeIcon(musicImg)
                .addAction(R.drawable.ic_loop_red_24dp,"loop",musicBackgroundLoopPandindIntent)
                .addAction(R.drawable.icon_previous, "previous", musicBackgroundpreviousPandindIntent)
                .addAction(R.drawable.icon_pause, "pause", musicBackgroundpausePandindIntent)
                .addAction(R.drawable.icon_next, "next", musicBackgroundnextPandindIntent)
                .addAction(R.drawable.ic_close_black_24dp, "Close", musicBackGroundClosePendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1, 2, 3)
                        .setMediaSession(mediaSession.getSessionToken()))
                .setContentIntent(BacktoTheMplayerpendingIntent)
                .setAutoCancel(true)
                .build();
        startForeground(1, musicNotifacationBr);

        return START_NOT_STICKY;

    }

    private AudioManager am;
    static int firstcount=0; //for debugging audiofocus purpose

    public void getAudiofocusAndPlay() {
        am = (AudioManager) this.getBaseContext().getSystemService(Context.AUDIO_SERVICE);

        //request audio focus
        int result = am.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            PlayerActivity playerActivity = new PlayerActivity();
            myMediaPlayer.start();
            playerActivity.changeSeekbar();
        }
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            PlayerActivity pause = new PlayerActivity();
            if(myMediaPlayer.isPlaying() && firstcount>0){
                pause.pauseSong();
            }
            firstcount++;
        }
    };

}
