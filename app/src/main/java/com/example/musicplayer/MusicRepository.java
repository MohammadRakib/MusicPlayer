package com.example.musicplayer;

import android.app.Application;
import android.os.AsyncTask;
import static com.example.musicplayer.Playlist.playlistAllsongs;


public class MusicRepository {

    private songDao songDao;



    public MusicRepository(Application application) {
        MusicDatabase database = MusicDatabase.getInstance(application);
        songDao = database.songDao();


    }

    public void insert(song_table song) {
        new InsertMusicAsyncTask(songDao).execute(song);
    }

    public void update(song_table song) {
        new UpdateMusicAsyncTask(songDao).execute(song);
    }

    public void delete(String playlistname) {
        new DeleteMusicAsyncTask(songDao).execute(playlistname);
    }

    public void findlist(String playlistname) {
        new findlistAsyncTask(songDao).execute(playlistname);
    }

    private static class InsertMusicAsyncTask extends AsyncTask<song_table, Void, Void> {
        private songDao songDao;

        private InsertMusicAsyncTask(songDao songDao) {
            this.songDao = songDao;
        }


        @Override
        protected Void doInBackground(song_table... song_tables) {
            songDao.Insert(song_tables[0]);
            return null;
        }
    }

    private static class UpdateMusicAsyncTask extends AsyncTask<song_table, Void, Void> {
        private songDao songDao;

        private UpdateMusicAsyncTask(songDao songDao) {
            this.songDao = songDao;
        }


        @Override
        protected Void doInBackground(song_table... song_tables) {
            songDao.Update(song_tables[0]);
            return null;
        }
    }

    private static class DeleteMusicAsyncTask extends AsyncTask<String, Void, Void> {
        private songDao songDao;

        private DeleteMusicAsyncTask(songDao songDao) {
            this.songDao = songDao;
        }


        @Override
        protected Void doInBackground(String... strings) {
            songDao.Delete(strings[0]);
            return null;
        }
    }


    private static class findlistAsyncTask extends AsyncTask<String,Void,Void>{
        private songDao songDao;
        public findlistAsyncTask(songDao songDao) {
            this.songDao = songDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            playlistAllsongs=songDao.findlist(strings[0]);
            return null;
        }
    }
}
