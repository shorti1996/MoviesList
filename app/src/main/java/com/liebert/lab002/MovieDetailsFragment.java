package com.liebert.lab002;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment implements RatingBar.OnRatingBarChangeListener {

    @BindView(R.id.vote_rb)
    RatingBar voteRb;

    @BindView(R.id.description_tv)
    TextView descriptionTv;

    public static final int VOTE_MAX = 10;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOVIE_ID = "movieId";

    private OnFragmentInteractionListener mListener;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param movieId Movie id.
     * @return A new instance of fragment MovieDetailsFragment.
     */
    public static MovieDetailsFragment newInstance(int movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);
        voteRb.setOnRatingBarChangeListener(this);
        initRating();
//        descriptionTv.setText(mMovie.getOverview());
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void initRating() {
        //TODO
        double userVote = mMovie.getUserVote();
        if (userVote != 0) {
            voteRb.setRating(movieVoteToRating(voteRb, userVote));
        } else {
            voteRb.setRating(movieVoteToRating(voteRb, mMovie.getVoteAverage()));
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        //TODO
//        mRealm.executeTransaction(realm -> mMovie.setUserVote(ratingToMovieVote(ratingBar)));
    }

    public double ratingToMovieVote(RatingBar ratingBar) {
        return (double) ((ratingBar.getRating() / ratingBar.getNumStars()) * VOTE_MAX);
    }

    public float movieVoteToRating(RatingBar ratingBar, double movieVote) {
        return (float) movieVote / VOTE_MAX * ratingBar.getNumStars();
    }
}
