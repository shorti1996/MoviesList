package com.liebert.lab002;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.liebert.lab002.Models.Genre;
import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.Models.MoviesData;
import com.liebert.lab002.Models.RealmInt;
import com.liebert.lab002.Services.ThemoviedbService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static io.reactivex.internal.operators.observable.ObservableBlockingSubscribe.subscribe;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movies_rv) RecyclerView moviesRv;
    @BindView(R.id.main_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

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
        mMoviesAdapter.setOnLoadMoreListener(mMoviesPresenter);
        mMoviesPresenter.loadNextPage();

    }

    private void loadGenres() {
        List<Genre> genres = mRealm.where(Genre.class).findAll();
        if (genres.size() == 0) {
            getGenresFromApi();
        } else {
            for (Genre g : genres) {
//                titlesTv.append(r.getTitle() + "\n");
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
                    Log.e("API CALL COMPLETED", "Genres downloaded");
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

    private void getMoviesFromApi(){
        // this makes gson compatible with mRealm
        // otherwise gson won't work with the model
        Type token = new TypeToken<RealmList<RealmInt>>(){}.getType();
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
                })
                .registerTypeAdapter(token, new TypeAdapter<RealmList<RealmInt>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<RealmInt> value) throws IOException {
                        // Ignore
                    }

                    @Override
                    public RealmList<RealmInt> read(JsonReader in) throws IOException {
                        RealmList<RealmInt> list = new RealmList<RealmInt>();
                        in.beginArray();
                        while (in.hasNext()) {
                            list.add(new RealmInt(in.nextInt()));
                        }
                        in.endArray();
                        return list;
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ThemoviedbService.SERVICE_ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ThemoviedbService themoviedbService = retrofit.create(ThemoviedbService.class);
        Observable<MoviesData> discoverMovies = themoviedbService.getMoviesDiscover(ThemoviedbService.API_KEY, 2)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (mMoviesAdapter != null) {
                        mMoviesAdapter.notifyDataSetChanged();
                    }
                    Log.e("API CALL COMPLETED", "All movies listed");
                });

        discoverMovies.subscribe(moviesData -> {
                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(moviesData);
                    mRealm.commitTransaction();

                    Log.d("MoviesData", "" + moviesData.getPage());

                    for (Movie m : moviesData.getMovies()) {
                        Log.d("Movie", m.getTitle());
                    }

                });
    }

    private List<Movie> writeMoviesToRealm(MoviesData moviesData) {
        List<Movie> movies = moviesData.getMovies();
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(movies);
        mRealm.commitTransaction();
        return movies;
    }

    private List<Movie> readMoviesFromRealm() {
        RealmResults<MoviesData> realmResults = mRealm.where(MoviesData.class).findAllSorted("page", Sort.ASCENDING);
        List<Movie> movies = new ArrayList<>();
        for (MoviesData md : realmResults) {
            movies.addAll(md.getMovies());
        }
        return movies;
    }

    public Realm getRealm(){
        return mRealm;
    }



}
