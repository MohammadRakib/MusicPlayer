package com.example.musicplayer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static com.example.musicplayer.Playlist.playlistadapter;
import static com.example.musicplayer.Playlist.playlistname;

public class playlistnameDialog extends DialogFragment {

    EditText playlistnameEditor;
    static String playlistNameEditor;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.addplaylistdialog, null);

        playlistnameEditor=view.findViewById(R.id.playlistnameedit);

        builder.setView(view)
                .setTitle("Playlist name")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        playlistNameEditor=playlistnameEditor.getText().toString();
                        playlistname.add(playlistNameEditor);
                        playlistadapter.notifyDataSetChanged();

                    }
                });

         return builder.create();


    }
}
