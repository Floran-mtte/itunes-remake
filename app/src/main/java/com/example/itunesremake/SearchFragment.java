package com.example.itunesremake;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SearchFragment";
    public static final String URL = "https://itunes.apple.com/search?term=";
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    private MediaPlayer mp;
    LinkedList<Integer> favorites = new LinkedList<Integer>();
    LinkedList<String> documents = new LinkedList<String>();
    EditText search_input;
    Button search_button;
    RecyclerView recycler;
    RequestQueue requestQueue;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        search_input = view.findViewById(R.id.search_input);
        search_button = view.findViewById(R.id.search_button);
        recycler = view.findViewById(R.id.recyclerView);

        search_button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        checkFav(user.getUid());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.search_button:
                Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap
                // Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

                // Instantiate the RequestQueue with the cache and network.
                requestQueue = new RequestQueue(cache, network);
                // Start the queue
                requestQueue.start();

                String requestUrl = URL + search_input.getText();
                jsonParse(requestUrl);
            break;
        }
    }

    private void jsonParse(String url)
    {

        Log.i("url",url);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    JSONArray result = response.getJSONArray("results");

                    Gson gson = new GsonBuilder().create();
                    ArrayList<Track> tracks = new ArrayList<>();
                    for(int i = 0;i < result.length();i++)
                    {
                        Track track = gson.fromJson(result.get(i).toString(), Track.class);
                        track.setFavorite(false);

                        for(int x = 0; x < favorites.size();x++)
                        {
                            if(favorites.get(x) == track.getTrackId())
                            {
                                Log.d(TAG,"test favorite search");
                                track.setFavorite(true);
                                track.setDocumentId(documents.get(x));
                            }
                        }
                        tracks.add(track);
                    }

                    showResults(tracks);

                }
                catch (JSONException e)
                {
                    Log.e("error:",e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error2:",error.getMessage());
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void showResults(final ArrayList<Track> tracks)
    {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(tracks,getActivity().getApplicationContext());
        recycler.setAdapter(adapter);


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

    public void checkFav(String idUser)
    {
        db.collection("favorites").whereEqualTo("userId",idUser).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    int i = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> db_track = document.getData();
                        favorites.add(i,Integer.valueOf(db_track.get("trackId").toString()));
                        documents.add(i, document.getId());
                        i++;
                    }
                }
            }
        });
    }


}
