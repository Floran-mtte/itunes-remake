package com.example.itunesremake;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private static final String TAG = "RecycleViewAdapater";
    private ArrayList<Track> tracks = new ArrayList<>();
    private Context mContext;
    public static final int KEY_1 = 1;
    public static final int KEY_2 = 2;


    public RecyclerViewAdapter(ArrayList<Track> tracks, Context mContext) {
        this.mContext = mContext;
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        Log.d(TAG, "onBindViewHolder : called.");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Picasso.get().load(tracks.get(i).getArtworkUrl60()).into(holder.cover);
        holder.artistName.setText(tracks.get(i).getArtistName());
        holder.collectionName.setText(tracks.get(i).getCollectionName());
        holder.trackName.setText(tracks.get(i).getTrackName());

        holder.favorite.setTag(R.id.Track, tracks.get(i));
        holder.favorite.setTag(R.id.User, user);
        holder.favorite.setOnClickListener(this);

        if(tracks.get(i).getFavorite())
        {
            holder.favorite.setImageResource(R.drawable.favorite_pressed);
        }

    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.favorite:
                ImageButton favBtn = v.findViewById(R.id.favorite);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Create a new user with a first and last name
                Map<String, Object> track = new HashMap<>();
                Track currentTrack = (Track) v.getTag(R.id.Track);
                FirebaseUser user = (FirebaseUser) v.getTag(R.id.User);

                track.put("artistName",currentTrack.getArtistName());
                track.put("trackName", currentTrack.getTrackName());
                track.put("collectionName", currentTrack.getCollectionName());
                track.put("artworkUrl60",currentTrack.getArtworkUrl60());
                track.put("userId",user.getUid());

                if(currentTrack.getFavorite())
                {
                    db.collection("favorites").document(currentTrack.getDocumentId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    favBtn.setImageResource(R.drawable.favorite);


                }
                else
                {
                    // Add a new document with a generated ID
                    db.collection("favorites")
                            .add(track)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                    favBtn.setImageResource(R.drawable.favorite_pressed);

                }

                break;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView cover;
        TextView artistName;
        TextView collectionName;
        TextView trackName;
        RelativeLayout layout;
        ImageButton favorite;
        public ViewHolder(View itemView)
        {
            super(itemView);

            cover = itemView.findViewById(R.id.cover);
            artistName = itemView.findViewById(R.id.artistName);
            collectionName = itemView.findViewById(R.id.collectionName);
            trackName = itemView.findViewById(R.id.trackName);
            layout = itemView.findViewById(R.id.parent_layout);
            favorite = itemView.findViewById(R.id.favorite);
        }
    }
}
