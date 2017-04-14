package com.liebert.lab002;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liebert.lab002.Models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MovieDetailsActivity extends AppCompatActivity implements RatingBar.OnRatingBarChangeListener {

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final int VOTE_MAX = 10;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    @BindView(R.id.backdrop_iv)
    ImageView backdropIv;

    @BindView(R.id.description_tv)
    TextView descriptionTv;

    @BindView(R.id.vote_rb)
    RatingBar voteRb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

//        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRealm = Realm.getDefaultInstance();
        movieId = getMovieFromExtra(savedInstanceState);

        ButterKnife.bind(this);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mMovie = mRealm.where(Movie.class).equalTo("id", movieId).findFirst();

        Glide.with(getBaseContext())
                .load(mMovie.getBackdropImageUri())
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        supportStartPostponedEnterTransition();
//                        return false;
//                    }
//                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(backdropIv);

        toolbar.setTitle(mMovie.getTitle());
        toolbarLayout.setTitle(mMovie.getTitle());

        descriptionTv.setText(mMovie.getOverview());
        initRating();
        voteRb.setOnRatingBarChangeListener(this);
    }

    @Override
    public boolean onNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        supportFinishAfterTransition();
        return true;
    }

    private int getMovieFromExtra(Bundle savedInstanceState) {
        int movieId = -1;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                movieId = extras.getInt(EXTRA_MOVIE);
            }
        } else {
            movieId = (int) savedInstanceState.getSerializable(EXTRA_MOVIE);
        }
        return movieId;
    }

    public void initRating() {
        double userVote = mMovie.getUserVote();
        if (userVote != 0) {
            voteRb.setRating(movieVoteToRating(voteRb, userVote));
        } else {
//            float averageVote = Float.parseFloat(mMovie.getVoteAverage().toString());
            voteRb.setRating(movieVoteToRating(voteRb, mMovie.getVoteAverage()));
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (!fromUser) {
            return;
        }
        mRealm.executeTransaction(realm -> mMovie.setUserVote(ratingToMovieVote(ratingBar)));
    }

    public double ratingToMovieVote(RatingBar ratingBar) {
        return (double) ((ratingBar.getRating() / ratingBar.getNumStars()) * VOTE_MAX);
    }

    public float movieVoteToRating(RatingBar ratingBar, double movieVote) {
        return (float) movieVote / VOTE_MAX * ratingBar.getNumStars();
    }
}
