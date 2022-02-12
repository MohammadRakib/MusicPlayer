package com.example.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>  {

    private List<song_table> songslist;

    private Context context;
    private OnItemClickListener mlistenr;
    ArrayList<Bitmap> img;


    public PlaylistAdapter(List<song_table> songslist, Context context, ArrayList<Bitmap> img) {
        this.songslist = songslist;
        this.context = context;
        this.img = img;

    }


    public interface OnItemClickListener {
        void onItemClick(int Position);
    }

    public void SetOnItemClickListener(OnItemClickListener listener) {
        mlistenr = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.songsrow, viewGroup, false);
        return new ViewHolder(view, mlistenr);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        song_table songs = songslist.get(i);
        if(img.get(i)!=null){
            viewHolder.musicImage.setImageBitmap(img.get(i));
        }else {
            viewHolder.musicImage.setImageResource(R.drawable.coverart);
        }
        viewHolder.musicname.setText(songs.getTitle());
        viewHolder.artistname.setText(songs.getArtist());

    }

    @Override
    public int getItemCount() {
        return songslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView musicname;
        public TextView artistname;
        public ImageView musicImage;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            musicname = itemView.findViewById(R.id.Musicname);
            artistname = itemView.findViewById(R.id.ArtistName);
            musicImage = itemView.findViewById(R.id.musicImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }

                }
            });

        }
    }


}
