package com.example.musicplayer;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import static com.example.musicplayer.MusicplayerService.LastSong;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.Song_fragment.viewModel;

@Database(entities = {song_table.class}, version = 1)
public abstract class MusicDatabase extends RoomDatabase {

    private static volatile MusicDatabase musicDatabase;

    public abstract songDao songDao();

    public static synchronized MusicDatabase getInstance(Context context) {
        if (musicDatabase == null) {
            musicDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    MusicDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return musicDatabase;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(musicDatabase).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private songDao songDao;

        private PopulateDbAsyncTask(MusicDatabase db) {
            songDao = db.songDao();
        }


        @Override
        protected Void doInBackground(Void... voids) {



            return null;
        }
    }
}
