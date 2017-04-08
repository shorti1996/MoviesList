package com.liebert.lab002;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.liebert.lab002.Models.MoviesData;
import com.liebert.lab002.Models.RealmInt;
import com.liebert.lab002.Models.Result;
import com.liebert.lab002.Services.ThemoviedbService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.titles_tv) TextView titlesTv;

    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Realm.init(this);
        mRealm = Realm.getDefaultInstance();

/*        if (checkCachedResults()) {
            getMovies();
        } else {
            RealmResults<Result> mResults = RealmQuery.createQuery(mRealm, Result.class)
                    .findAll();
            titlesTv.setText("");
            for (Result r : mResults) {
                titlesTv.append(r.getTitle() + "\n");
                Log.d("FROM CACHE", r.getTitle());
            }
        }*/
        getMovies();

        /*mRealm.beginTransaction();
        mRealm.deleteAll();
        mRealm.commitTransaction();

        if (checkCachedResults()) {
            getMovies();
        } else {
            RealmResults<Result> mResults = RealmQuery.createQuery(mRealm, Result.class)
                    .findAll();
            titlesTv.setText("");
            for (Result r : mResults) {
                titlesTv.append(r.getTitle());
                Log.d("FROM CACHE", r.getTitle());
            }
        }*/

    }

    private boolean checkCachedResults() {
        RealmResults<Result> mResults = RealmQuery.createQuery(mRealm, Result.class)
                .findAll();
        return mResults.size() == 0;
    }

    private void getMovies(){
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
        Observable<MoviesData> discoverMovies = themoviedbService.getMoviesDiscover(); //weatherService.getWeatherData("Islamabad");

        discoverMovies.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    Log.e("COMPLETED", "All mResults listed");
                })
                .subscribe(moviesData -> {
                    List<Result> results = moviesData.getResults();

                    mRealm.beginTransaction();
                    mRealm.copyToRealmOrUpdate(results);
                    mRealm.commitTransaction();

                    for (Result result : results) {
                        Log.d("Movie", result.getTitle());
                    }
                });
    }
}
