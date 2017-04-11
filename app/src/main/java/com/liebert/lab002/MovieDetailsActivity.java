package com.liebert.lab002;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liebert.lab002.Models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MovieDetailsActivity extends AppCompatActivity {

    int movieId;
    Realm mRealm;
    Movie mMovie;

    public static final String EXTRA_MOVIE = "extra_movie";

    @BindView(R.id.backdrop_iv)
    ImageView backdropIv;

    @BindView(R.id.description_tv)
    TextView descriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
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
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        Movie mMovie = mRealm.where(Movie.class).equalTo("id", movieId).findFirst();
        Glide.with(this)
                .load(mMovie.getBackdropImageUri())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
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
            if(extras != null) {
                movieId = extras.getInt(EXTRA_MOVIE);
            }
        } else {
            movieId = (int) savedInstanceState.getSerializable(EXTRA_MOVIE);
        }
        return movieId;
    }
}
