package com.liebert.lab002;


import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liebert.lab002.Models.Movie;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieImagesFragment extends Fragment {

    @BindView(R.id.movie_images_grid)
    GridView mImagesGrid;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    List<Integer> mImageList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOVIE_ID = "movieId";

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

        mImageList = new LinkedList<>();
        for (int i = 0; i < 6; i++) {
            mImageList.add(R.drawable.backdrop);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_images, container, false);
        ButterKnife.bind(this, view);

        mImagesGrid.setAdapter(new ImageAdapter(getContext()));

        return view;
    }

    void attach(){
        MovieDetailsActivity.attachFragmentToView(MovieDetailsFragment.newInstance(movieId),
                R.id.fragment_movie_images_root,
                false,
                "",
                this.getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        attach();
    }

    public class ImageAdapter extends BaseAdapter {
        Context mContext;

        public ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(mContext);
            if (convertView == null) {
//                DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
//                int screenWidth = metrics.widthPixels/3;
//                iv.setLayoutParams(new GridView.LayoutParams(screenWidth/2, screenWidth/2));
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
//                iv.setLayoutParams(new GridView.LayoutParams(params));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                iv.setPadding(0, 0, 0, 0);
            } else {
                iv = (ImageView) convertView;
            }
            iv.setImageResource(mImageList.get(position));
            return iv;
        }
    }
}
