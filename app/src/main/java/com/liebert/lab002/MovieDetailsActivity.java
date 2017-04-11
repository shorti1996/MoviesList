package com.liebert.lab002;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

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

    @BindView(R.id.movie_details_backdrop_iv)
    ImageView backdropIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mRealm = Realm.getDefaultInstance();
        movieId = getMovieFromExtra(savedInstanceState);

        ButterKnife.bind(this);

        Movie mMovie = mRealm.where(Movie.class).equalTo("id", movieId).findFirst();
        Glide.with(this)
                .load(mMovie.getBackdropImageUri())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(backdropIv);
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
