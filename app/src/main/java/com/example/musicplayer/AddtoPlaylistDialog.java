package com.example.musicplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import static com.example.musicplayer.App.myMediaPlayer;
import static com.example.musicplayer.App.position;
import static com.example.musicplayer.App.songsList;
import static com.example.musicplayer.Playlist.playlistadapter;
import static com.example.musicplayer.Playlist.playlistname;
import static com.example.musicplayer.Song_fragment.viewModel;

public class AddtoPlaylistDialog extends DialogFragment {

    ListView playlistview;
    ArrayAdapter<String> playlistdialogAdapter;
    Context context;
    static String selectedAddtoPlaylistName;




    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.playlistdialog, null);
        context = view.getContext();
        playlistview = view.findViewById(R.id.folderPlaylistDialog);
        playlistdialogAdapter = new ArrayAdapter<>(context, R.layout.playlist_row,R.id.foldername,playlistname);
        playlistview.setAdapter(playlistdialogAdapter);
        final int songposition= position;
        playlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedAddtoPlaylistName = playlistname.get(position);
                Toast.makeText(context, "Add to playlist", Toast.LENGTH_SHORT).show();
                int duration=myMediaPlayer.getDuration();
                String Duration= String.valueOf(duration);

                song_table addingSong=new song_table(songsList.get(songposition).getTitle()
                        ,songsList.get(songposition).getArtist()
                        ,songsList.get(songposition).getDatapath()
                        ,Duration,songsList.get(songposition).getAlbum()
                        ,selectedAddtoPlaylistName
                        ,songposition);
                viewModel.insert(addingSong);




            }
        });


        builder.setView(view)
                .setTitle("playlist")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


        return builder.create();


    }
}
