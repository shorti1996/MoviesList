package com.liebert.lab002;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.liebert.lab002.Models.Backdrop;
import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.Models.MovieImages;
import com.liebert.lab002.Models.Poster;
import com.liebert.lab002.Services.ThemoviedbService;
import com.liebert.lab002.Views.SquareImageView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieImagesFragment extends Fragment {

    @BindView(R.id.movie_images_grid)
    RecyclerView mImagesGrid;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    List<Poster> mPosters;

    private static final String ARG_MOVIE_ID = "movieId";

    private static final int IMAGES_COLUMN_NUMBER = 3;
    public static final int IMAGES_COUNT = 6;

    public MovieImagesFragment() {
        // Required empty public constructor
    }

    public static MovieImagesFragment newInstance(int movieId) {
        MovieImagesFragment fragment = new MovieImagesFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_images, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    void attach(){
        MovieDetailsActivity.attachFragmentToView(
                MovieActorsFragment.newInstance(movieId),
                R.id.fragment_movie_images_root,
                false,
                "MOVIE_ACTORS_FRAGMENT",
                this.getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        attach();
        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.movie_images_grid);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), IMAGES_COLUMN_NUMBER));
        ImagesAdapter adapter = new ImagesAdapter(getContext());
        recyclerView.setAdapter(adapter);

        attach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_MOVIE_ID, movieId);
    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

        Context mContext;
        Retrofit mRetrofit;
        ThemoviedbService mThemoviedbService;

        List<Uri> mBackdropsUri = new LinkedList<>();

        public ImagesAdapter(Context context) {
            mContext = context;

            mRetrofit = new Retrofit.Builder()
                    .baseUrl(ThemoviedbService.SERVICE_ENDPOINT)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            mThemoviedbService = mRetrofit.create(ThemoviedbService.class);
            Observable<MovieImages> movieImagesObservable = mThemoviedbService.getMovieImages(movieId, ThemoviedbService.API_KEY)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(throwable -> {
                        Log.e("BAD THINGS", "NETWORK ERRRRROR");
                    })
                    .doOnComplete(() -> {
                        Log.d("API CALL COMPLETED", "Images downloaded");
                    });

            movieImagesObservable.subscribe(movieImages -> {
                List<Backdrop> backdrops = movieImages.getBackdrops();
                if (backdrops != null) {
                    for (int i = 0; i < IMAGES_COUNT; i++) {
                        if (i < backdrops.size()) {
                            // Log.d("Movie bg: ", backdrops.get(i).getFilePath());
                            // Log.d("AA: ", MovieImages.getFullPath(backdrops.get(i).getFilePath(), 0).toString());
                            mBackdropsUri.add(MovieImages.getFullPath(backdrops.get(i).getFilePath(), 0));
                        } else {
                            break;
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.square_image_item, parent, false);
            return new ImageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {
            SquareImageView squareImageView = holder.mSquareImageView;
            squareImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            squareImageView.setPadding(0, 0, 0, 0);
            Glide.with(mContext)
                    .load(mBackdropsUri.get(position).getPath())
                    .into(squareImageView);
//            squareImageView.setImageResource(mImageList.get(position));
        }

        @Override
        public int getItemCount() {
            return mBackdropsUri.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            SquareImageView mSquareImageView;

            public ImageViewHolder(View itemView) {
                super(itemView);
                mSquareImageView = (SquareImageView) itemView.findViewById(R.id.square_image_view_item);
            }

        }
    }

}
