package com.liebert.lab002;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liebert.lab002.Helpers.*;
import com.liebert.lab002.Helpers.Utils;
import com.liebert.lab002.Models.Cast;
import com.liebert.lab002.Models.Credits;
import com.liebert.lab002.Models.Crew;
import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.Services.ThemoviedbService;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieActorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieActorsFragment extends Fragment {

    @BindView(R.id.actors_rv)
    RecyclerView mActorsRv;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    private static final String ARG_MOVIE_ID = "movieId";

    public MovieActorsFragment() {
        // Required empty public constructor
    }

    public static MovieActorsFragment newInstance(int movieId) {
        MovieActorsFragment fragment = new MovieActorsFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActorsRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mActorsRv.setAdapter(new CreditsAdapter(getContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_actors, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MOVIE_ID, movieId);
    }

    public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.CreditsViewHolder> {

        Context mContext;
        Retrofit mRetrofit;
        ThemoviedbService mThemoviedbService;

        List<Cast> mCastList = new LinkedList<>();

        public CreditsAdapter(Context context) {
            mContext = context;

            Credits credits = mRealm.where(Credits.class).equalTo("id", movieId).findFirst();
            if (credits != null) {
                mCastList.addAll(credits.getCast());
                notifyDataSetChanged();
                return;
            }

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(ThemoviedbService.SERVICE_ENDPOINT)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mThemoviedbService = mRetrofit.create(ThemoviedbService.class);
            Observable<Credits> movieCreditsObservable = mThemoviedbService.getMovieCredits(movieId, ThemoviedbService.API_KEY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> {
                        Log.e("BAD THINGS", "NETWORK ERRRRROR");
                    })
                    .doOnComplete(() -> {
                        Log.d("API CALL COMPLETED", "Images downloaded");
                    });
            movieCreditsObservable.subscribe(movieCredits -> {
                mCastList.addAll(movieCredits.getCast());
                copyCreditsToRealm(movieCredits);
                notifyDataSetChanged();
            });
        }

        @Override
        public CreditsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.actor_item, parent, false);
            return new CreditsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CreditsViewHolder holder, int position) {
            Cast cast = mCastList.get(position);
            holder.actorCharacterTv.setText(cast.getCharacter());
            holder.actorNameTv.setText(cast.getName());

            String imagePath = Utils.ImagePath.getFullImagePath(cast.getProfilePath(), Utils.ImagePath.WIDTH_154)
                    .getPath().toString();
            Glide.with(mContext)
                    .load(imagePath)
                    .into(holder.actorCiv);
        }

        @Override
        public int getItemCount() {
            return mCastList.size();
        }

        public class CreditsViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.actor_civ)
            CircleImageView actorCiv;

            @BindView(R.id.actor_name_tv)
            TextView actorNameTv;

            @BindView(R.id.actor_character_tv)
            TextView actorCharacterTv;


            public CreditsViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }

    private void copyCreditsToRealm(Credits movieCredits) {
        mRealm.executeTransaction(realm -> {
            Movie movie = realm.where(Movie.class).equalTo("id", movieId).findFirst();

            // impotant:
            // to make credits managed by Realm do this
            Credits movieCreditsInRealm = realm.copyToRealmOrUpdate(movieCredits);
            movie.setMovieCredits(movieCreditsInRealm);
            realm.copyToRealmOrUpdate(movie);
        });
    }


}
