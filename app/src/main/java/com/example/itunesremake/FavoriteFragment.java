package com.example.itunesremake;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    public static final String TAG = "FavoriteFragment";
    RecyclerView recycler;
    ArrayList<Track> track_list = new ArrayList<>();
    private MediaPlayer mp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recycler = view.findViewById(R.id.favoriteRecycler);

        return view;
    }


    @Override
    public void onStart()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("favorites")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            ArrayList<Track> tracks = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> db_track = document.getData();
                                Track track = new Track(
                                        Integer.valueOf(db_track.get("trackId").toString()),
                                        db_track.get("artistName").toString(),
                                        db_track.get("collectionName").toString(),
                                        db_track.get("trackName").toString(),
                                        db_track.get("artworkUrl60").toString(),
                                        db_track.get("previewUrl").toString());
                                track.setFavorite(true);
                                track.setDocumentId(document.getId());

                                tracks.add(track);
                            }

                            showResults(tracks);
                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }


                });

                db.collection("favorites").whereEqualTo("userId",user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }



                        ArrayList<Track> tracks = new ArrayList<>();
                        for (DocumentChange document : snapshots.getDocumentChanges()) {

                            switch (document.getType()) {
                                case ADDED:
                                    Map<String, Object> db_track = document.getDocument().getData();
                                    if(db_track.get("trackId").toString() != null)
                                    {
                                        try {
                                            int id = Integer.valueOf(db_track.get("trackId").toString());
                                            Track track = new Track(
                                                    id,
                                                    db_track.get("artistName").toString(),
                                                    db_track.get("collectionName").toString(),
                                                    db_track.get("trackName").toString(),
                                                    db_track.get("artworkUrl60").toString(),
                                                    db_track.get("previewUrl").toString());
                                            track.setFavorite(true);
                                            track.setDocumentId(document.getDocument().getId());

                                            track_list.add(track);
                                        }
                                        catch(Exception error)
                                        {
                                            Log.d(TAG, error.getMessage());
                                        }
                                    }

                                    break;
                                case REMOVED:
                                    Map<String, Object> track_removed = document.getDocument().getData();
                                    for(int i = 0; i < track_list.size();i++)
                                    {
                                        if(track_list.get(i).getTrackName().equals(track_removed.get("trackName").toString()))
                                        {
                                            track_list.remove(i);
                                            break;
                                        }
                                    }
                                    break;
                            }

                        }

                        for (int i = 0; i < track_list.size();i++)
                        {
                            Log.d("test",track_list.get(i).getTrackName());
                        }
                        showResults(track_list);

                    }
                });





                /*.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        ArrayList<Track> tracks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> db_track = document.getData();
                            Track track = new Track(db_track.get("artistName").toString(),
                                                    db_track.get("collectionName").toString(),
                                                    db_track.get("trackName").toString(),
                                                    db_track.get("artworkUrl60").toString());
                            track.setFavorite(true);
                            track.setDocumentId(document.getId());

                            tracks.add(track);
                        }

                        showResults(tracks);
                    }
                    else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
            }
        });*/
    }

    private void showResults(final ArrayList<Track> tracks)
    {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(tracks,getActivity().getApplicationContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = recycler.indexOfChild(v);
                if (mp != null && mp.isPlaying()) {
                    mp.stop();

                    Uri uri = Uri.parse(v.getTag(R.id.Preview).toString());
                    mp = MediaPlayer.create(getContext(), uri);
                    mp.start();
                } else {
                    Uri uri = Uri.parse(tracks.get(pos).getPreviewUrl());
                    mp = MediaPlayer.create(getContext(), uri);
                    mp.start();
                }
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

}
