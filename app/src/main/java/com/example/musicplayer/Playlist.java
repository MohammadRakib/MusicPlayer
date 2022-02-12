package com.example.musicplayer;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.example.musicplayer.MainActivity.playlistfoldername;
import static com.example.musicplayer.Song_fragment.viewModel;

public class Playlist extends Fragment {



    ImageButton addplaylist;
    ListView folderlist;
    static ArrayList<String> playlistname;
    static public ArrayAdapter<String> playlistadapter;
    Context context;
    Button platlistdeletebutton;
    static boolean deleteMode;
    static List<song_table> playlistAllsongs;
    static boolean toPlaylist=false;





    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.playlist_fragment, container, false);
        addplaylist=v.findViewById(R.id.addplaylist);
        folderlist=v.findViewById(R.id.folderPlaylist);
        platlistdeletebutton=v.findViewById(R.id.playlistdelete);
        context=v.getContext();
        deleteMode=false;
        loadplaylistfolder();  //loading playlist
        playlistadapter = new ArrayAdapter<>(context, R.layout.playlist_row,R.id.foldername,playlistname);
        folderlist.setAdapter(playlistadapter);
        if(playlistname!=null){
            String playlistName=playlistname.get(0);
            viewModel.findlist(playlistName);
        }
        folderlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(deleteMode){
                    String playlistName=playlistname.get(position);
                    playlistname.remove(position);
                    viewModel.delete(playlistName);
                    playlistadapter.notifyDataSetChanged();
                }else {
                    toPlaylist=true;
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show();
                    String playlistName=playlistname.get(position);
                    viewModel.findlist(playlistName);
                    if(playlistAllsongs!=null){
                        PlaylistsongsShower playlistsongsShower = new PlaylistsongsShower();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.allsonglistcontainer,playlistsongsShower).commit();

                    }
                    else {
                        Toast.makeText(context, "there is no song in this playlist", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        addplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playlistnameDialog playlistnameDialog =new playlistnameDialog();
                playlistnameDialog.show(getActivity().getSupportFragmentManager(),"Add playlist name");

            }
        });

        platlistdeletebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(deleteMode){
                    platlistdeletebutton.setBackgroundResource(R.drawable.ic_delete_black_24dp);
                    deleteMode=false;

                }
                else {
                    platlistdeletebutton.setBackgroundResource(R.drawable.ic_delete_forever_black_24dp);
                    deleteMode=true;
                    Toast.makeText(context, "Clicked the playist folder to delete", Toast.LENGTH_SHORT).show();
                }




            }
        });


        return v;
    }

    private void loadplaylistfolder() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(playlistfoldername,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("taking playlistfoldername",null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        if(gson.fromJson(json,type) != null){

            playlistname = gson.fromJson(json,type);

        }



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = ViewModelProviders.of(this).get(PlaylistViewModel.class);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(playlistfoldername,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(playlistname);
        editor.putString("taking playlistfoldername",json);
        editor.apply();
        if(toPlaylist){
           Song_fragment  song_fragment=new Song_fragment();
           getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragement_song_container,song_fragment).commit();

        }else {
            Song_fragment  song_fragment=new Song_fragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragement_song_container,song_fragment).commit();
        }
    }
}
