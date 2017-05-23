package com.liebert.lab002;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.R;

import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieActorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieActorsFragment extends Fragment {

    int movieId;
    Realm mRealm;
    Movie mMovie;

    private static final String ARG_MOVIE_ID = "movieId";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MovieActorsFragment() {
        // Required empty public constructor
    }

    public static MovieActorsFragment newInstance(int movieId) {
        MovieActorsFragment fragment = new MovieActorsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
        mRealm = Realm.getDefaultInstance();
        mMovie = mRealm.where(Movie.class).equalTo("id", movieId).findFirst();
    }

    //TODO create layout and bind butterknife
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_actors, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MOVIE_ID, movieId);
    }
}
