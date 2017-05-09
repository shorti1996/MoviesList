package com.liebert.lab002;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.liebert.lab002.Models.Movie;
import com.liebert.lab002.Views.SquareImageView;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MovieImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieImagesFragment extends Fragment {

//    @BindView(R.id.movie_images_grid)
//    GridView mImagesGrid;
    @BindView(R.id.movie_images_grid)
    RecyclerView mImagesGrid;

    int movieId;
    Realm mRealm;
    Movie mMovie;

    List<Integer> mImageList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MOVIE_ID = "movieId";

    private static final int IMAGES_COLUMN_NUMBER = 3;

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

//        mImagesGrid.setAdapter(new ImageAdapter(getContext()));


        return view;
    }

    void attach(){
        MovieDetailsActivity.attachFragmentToView(
                MovieDetailsFragment.newInstance(movieId),
                R.id.fragment_movie_images_root,
                false,
                "",
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
    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {

        Context mContext;

        public ImagesAdapter(Context context) {
            mContext = context;
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
            squareImageView.setImageResource(mImageList.get(position));
        }

        @Override
        public int getItemCount() {
            return mImageList.size();
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
