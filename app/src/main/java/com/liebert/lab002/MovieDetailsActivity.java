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


public class MovieDetailsActivity extends AppCompatActivity{

    public static final String EXTRA_MOVIE = "extra_movie";
    public static final String TAG_MOVIE_DETAILS_FRAGMENT = "movieDetailsFragment";

    int movieId;
    Realm mRealm;
    Movie mMovie;

    @BindView(R.id.backdrop_iv)
    ImageView backdropIv;

    @BindView(R.id.description_tv)
    TextView descriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mRealm = Realm.getDefaultInstance();
        movieId = getMovieFromExtra(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.movie_details_fragment, MovieDetailsFragment.newInstance(movieId), TAG_MOVIE_DETAILS_FRAGMENT)
                    .commit();
        }

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
}
