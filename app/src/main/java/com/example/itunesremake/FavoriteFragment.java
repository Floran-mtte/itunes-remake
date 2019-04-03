package com.example.itunesremake;


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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    public static final String TAG = "FavoriteFragment";
    RecyclerView recycler;
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
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("favorites").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        });
    }

    private void showResults(ArrayList<Track> tracks)
    {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(tracks,getActivity().getApplicationContext());
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    }

}
