package com.liebert.lab002;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liebert.lab002.Models.Genre;
import com.liebert.lab002.Services.ThemoviedbService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.reactivex.internal.operators.observable.ObservableBlockingSubscribe.subscribe;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movies_rv) RecyclerView moviesRv;
    @BindView(R.id.main_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.navigation_view_main) BottomNavigationView mBottomNavigationView;

    Realm mRealm;
    private MoviesAdapter mMoviesAdapter;
    private DiscoverMoviesPresenter mMoviesPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

//        loadMovies();

        loadGenres();

        mMoviesAdapter = new MoviesAdapter(this, mSwipeRefreshLayout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        moviesRv.setLayoutManager(mLayoutManager);
        moviesRv.setItemAnimator(new DefaultItemAnimator());
        moviesRv.setAdapter(mMoviesAdapter);
        mMoviesAdapter.setRecyclerView(moviesRv);
        mMoviesAdapter.setLinearLayoutManager((LinearLayoutManager) mLayoutManager);

        mMoviesPresenter = new DiscoverMoviesPresenter(mMoviesAdapter, mRealm, this);
        mMoviesAdapter.mSwipeRefreshLayout.setOnRefreshListener(mMoviesPresenter);
        mMoviesAdapter.setOnLoadMoreListener(mMoviesPresenter);
        mMoviesPresenter.loadNextPage();

        mMoviesPresenter.initSwipe(moviesRv);

    }

    private void loadGenres() {
        List<Genre> genres = mRealm.where(Genre.class).findAll();
        if (genres.size() == 0) {
            getGenresFromApi();
        } else {
            for (Genre g : genres) {
                Log.d("FROM CACHE", g.toString());
            }
        }
    }

    private void getGenresFromApi(){
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ThemoviedbService.SERVICE_ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ThemoviedbService themoviedbService = retrofit.create(ThemoviedbService.class);
        Observable<Genre> genresObservable = themoviedbService.getGenres()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(genresList -> Observable.fromIterable(genresList.getGenres()))
                .doOnComplete(() -> {
                    Log.d("API CALL COMPLETED", "Genres downloaded");
                });

        genresObservable.subscribe(genre -> {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    mRealm.copyToRealmOrUpdate(genre);
                }
            });
            Log.d("Genre", genre.toString());
        });
    }

    public Realm getRealm(){
        return mRealm;
    }



}
