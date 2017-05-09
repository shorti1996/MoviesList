package com.liebert.lab002;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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

    private static final String ARG_MOVIE_ID = "movieId";

    int movieId;
    Realm mRealm;
    Movie mMovie;

    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.backdrop_iv)
    ImageView backdropIv;

    @BindView(R.id.movie_details_tab_layout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        mRealm = Realm.getDefaultInstance();
        movieId = getMovieFromExtra(savedInstanceState);
        mMovie = mRealm.where(Movie.class).equalTo("id", movieId).findFirst();

        /** set the adapter for ViewPager */
        mViewPager.setAdapter(new DetailsFragmentPagerAdapter(
                getSupportFragmentManager()));

//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.movie_details_fragment, MovieDetailsFragment.newInstance(movieId), TAG_MOVIE_DETAILS_FRAGMENT)
//                    .commit();
//        }

//        supportPostponeEnterTransition();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

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

        mTabLayout.setupWithViewPager(mViewPager);
    }

    class DetailsFragmentPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        //TODO use resources
        private String[] tabTitles = {"About", "Details"};

        public DetailsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MovieDetailsFragment.newInstance(movieId);
            }
            if (position == 1) {
                return MovieImagesFragment.newInstance(movieId);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
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
            movieId = (int) savedInstanceState.getInt(ARG_MOVIE_ID);
        }
        return movieId;
    }

    /**
     * Attach a {@link android.support.v4.app.Fragment} to a view, usually a
     * {@link android.view.ViewGroup}. The view is provided as resource ID, as
     * present in mypackage.R.id.
     *
     * @param fragment
     *            The Fragment to attach.
     * @param viewId
     *            The resource ID for the view to attach the fragment to, as
     *            found in R.id.
     * @param addToBackStack
     *            {@literal true} to allow the user to undo the operation with
     *            the device's back button.
     * @param tag
     *            The tag name to give the Fragment as it is connected to the UI
     * @param context
     *            An {@link android.support.v4.app.FragmentActivity} that hosts
     *            the fragment and the view.
     */
    public static void attachFragmentToView(Fragment fragment, int viewId, boolean addToBackStack, String tag, FragmentActivity context)
    {
        FragmentManager fragMan = context.getSupportFragmentManager();
        FragmentTransaction transaction = fragMan.beginTransaction();

        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }

        transaction.add(viewId, fragment, tag);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MOVIE_ID, movieId);
    }
}
