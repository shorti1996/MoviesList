package com.liebert.lab002;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.liebert.lab002.Helpers.OnLoadMoreListener;
import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.Models.MoviesData;
import com.liebert.lab002.Models.RealmInt;
import com.liebert.lab002.Services.ThemoviedbService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.Sort;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shorti1996 on 11.04.2017.
 */

public class DiscoverMoviesPresenter implements OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    Context mContext;
    private MoviesAdapter mMoviesAdapter;
    private Realm mRealm;

    private int page = 0;
    private static final int PAGE_SIZE = 20;

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public void nextPage() {
        this.page = getPage() + 1;
    }

    public DiscoverMoviesPresenter(MoviesAdapter moviesAdapter, Realm realm, Context context) {
        this.mMoviesAdapter = moviesAdapter;
        this.mRealm = realm;
        this.mContext = context;
    }

    @Override
    public void onLoadMore() {
//        Toast.makeText(mContext, mContext.getString(R.string.loading_more_movies_toast), Toast.LENGTH_SHORT).show();
        Movie dummyMovie = new Movie();
        dummyMovie.setId(Movie.dummyId);
        dummyMovie.setIsDummy(true);
        List<Movie> newList = mMoviesAdapter.getMoviesList();
        newList.add(dummyMovie);
        mMoviesAdapter.swapMoviesList(newList);
        mMoviesAdapter.setIsMoreLoading(true);
//        mMoviesAdapter.notifyItemInserted(mMoviesAdapter.getItemCount()); //so on the last position because one for loading view is added
        mMoviesAdapter.notifyDataSetChanged();
        loadNextPage();
    }

    @Override
    public void onRefresh() {
        setPage(0);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realmInstance -> {
            realmInstance.where(MoviesData.class).findAll().deleteAllFromRealm();
            realmInstance.where(Movie.class).findAll().deleteAllFromRealm();
        });
        for (Movie m :
                realm.where(Movie.class).findAll()) {
            Log.e("Halo. Cos sie zepsulo.", m.getTitle());
        }
        mMoviesAdapter.clearMoviesList();
//        loadNextPage();
    }

//    public void addDummyMovieToRealm() {
//        Movie dummyMovie = new Movie();
//        dummyMovie.setId(Movie.dummyId);
//        dummyMovie.setIsDummy(true);
//        mRealm.executeTransaction(realm -> {
//            realm.copyToRealmOrUpdate(dummyMovie);
//        });
////        mRealm.beginTransaction();
////        mRealm.copyToRealmOrUpdate(dummyMovie);
////        mRealm.commitTransaction();
//    }

//    public void removeDummyMovieFromRealm() {
//        mRealm.executeTransaction(realm -> {
//            RealmResults<Movie> result = realm.where(Movie.class).equalTo("is_dummy", true).findAll();
//            result.deleteAllFromRealm();
//        });
//    }

    public void getMoviesFromApi() {
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
        Observable<MoviesData> discoverMovies = themoviedbService.getMoviesDiscover(ThemoviedbService.API_KEY, getPage())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> {
                    setPage(getPage()-1);
                    Log.e("KURWA", "NETWORK ERRRRROR");
                    return MoviesData.getEmptyMoviesData();
                })
//                .doOnError(throwable -> {
//                    Log.e("BAD THINGS", "NETWORK ERRRRROR");
//                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//                    throwable.printStackTrace();
//                })
                .doOnComplete(() -> {
                    if (mMoviesAdapter != null) {
//                        mMoviesAdapter.notifyDataSetChanged();
                    }
                    Log.d("API CALL COMPLETED", "Page downloaded");
                });

        discoverMovies.subscribe(moviesData -> {
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(moviesData);
            mRealm.commitTransaction();

            onNextPageDownloaded();
        });
    }

    public void onNextPageDownloaded() {
        setPage(getPage()-1);
        loadNextPage();
    }

    /**
     * Try to load MoviesData of correct page from realm.
     * If there is no data, request it from API, write to realm and retry loading from cache.
     */
    public void loadNextPage() {
        nextPage();
        MoviesData moviesData = mRealm.where(MoviesData.class).equalTo("page", getPage()).findFirst();
        if (moviesData == null || (moviesData != null && moviesData.getMovies().size() == 0)) {
            getMoviesFromApi();
        } else if (mMoviesAdapter != null) {
            List<MoviesData> realmResults = mRealm.where(MoviesData.class)
                    .lessThanOrEqualTo("page", getPage())
                    .findAllSorted("page", Sort.ASCENDING);
            List<Movie> movieList = new ArrayList<>();
            for (MoviesData md : realmResults) {
                movieList.addAll(md.getMovies());
            }
            //kind of success
            mMoviesAdapter.swapMoviesList(movieList);
            mMoviesAdapter.notifyDataSetChanged();
            mMoviesAdapter.setIsMoreLoading(false);
            mMoviesAdapter.mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void removeItem(int position) {
        int movieId = mMoviesAdapter.getMoviesList().get(position).getId();
        mMoviesAdapter.getMoviesList().remove(position);
        mRealm.executeTransaction(realm -> {
            realm.where(Movie.class).equalTo("id", movieId).findAll().deleteAllFromRealm();
        });
        mMoviesAdapter.notifyItemRemoved(position);
    }

    public void markItem(int position) {
        int movieId = mMoviesAdapter.getMoviesList().get(position).getId();
        mRealm.executeTransaction(realm -> {
            Movie movie = realm.where(Movie.class).equalTo("id", movieId).findFirst();
            movie.setSeen(!movie.getSeen());
            realm.copyToRealmOrUpdate(movie);
        });
        mMoviesAdapter.notifyItemChanged(position);
    }

}
