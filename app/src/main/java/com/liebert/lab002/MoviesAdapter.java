package com.liebert.lab002;

/**
 * Created by shorti1996 on 08.04.2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liebert.lab002.Helpers.OnLoadMoreListener;
import com.liebert.lab002.Models.Movie;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_MOVIE_LEFT = 0;
    private final int VIEW_TYPE_MOVIE_RIGHT = 1;
    private final int VIEW_TYPE_LOADING = 100;

    private boolean isMoreLoading = false;
    // when to start loading more items (this number = how many items on list before)
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private List<Movie> moviesList;
    private Context mContext;
    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar loadingPb;
        private TextView loadingMessageTv;
        public ProgressViewHolder(View view) {
            super(view);
            loadingPb = (ProgressBar) view.findViewById(R.id.movies_list_loading_pb);
            loadingMessageTv = (TextView) view.findViewById(R.id.loading_more_movies_tv);
            loadingMessageTv.setText(R.string.loading_more_movies_text);
        }
    }

    private class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView title, year, genre;
        public ImageView backdropIv;
        public View tintView;
        public ProgressBar progressBar;
        public ImageView eyeIv;

        public MovieViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_tv);
            genre = (TextView) view.findViewById(R.id.genre_tv);
            year = (TextView) view.findViewById(R.id.year_tv);
            backdropIv = (ImageView) view.findViewById(R.id.backdrop_iv);
            progressBar = (ProgressBar) view.findViewById(R.id.movie_row_image_loading_iv);
            tintView = view.findViewById(R.id.backdrop_tint);
            eyeIv = (ImageView) view.findViewById(R.id.eye_iv);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie clickedMovie = moviesList.get(adapterPosition);
//            Toast.makeText(mContext, clickedMovie.getTitle(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, MovieDetailsActivity.class);
            // Pass data object in the bundle and populate details activity.
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, clickedMovie.getId());
            Pair<View, String> p1 = Pair.create(backdropIv, "movie_details_backdrop");
            Pair<View, String> p2 = Pair.create(tintView, "movie_details_tint");
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) mContext, p1, p2);
            mContext.startActivity(intent, options.toBundle());
/*            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);*/
        }
    }

    public MoviesAdapter(/*List<Movie> moviesList,*/ Context context, SwipeRefreshLayout swipeRefreshLayout) {
        this.moviesList = new ArrayList<>();
        this.mContext = context;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public void clearMoviesList() {
        this.moviesList = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void swapMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    public List<Movie> getMoviesList() {
        return this.moviesList;
    }

    public void setRecyclerView(RecyclerView view){
        mRecyclerView = view;
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    isMoreLoading = true;
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.mLinearLayoutManager=linearLayoutManager;
    }

    @Override
    public int getItemViewType(int position) {
        return moviesList.get(position).getIsDummy() ? VIEW_TYPE_LOADING : VIEW_TYPE_MOVIE_LEFT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == VIEW_TYPE_MOVIE_LEFT) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_row, parent, false);
            return new MovieViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_loading, parent, false);
            return new ProgressViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MovieViewHolder) {
            MovieViewHolder movieViewHolder = (MovieViewHolder) holder;
            Movie movie = moviesList.get(position);
            movieViewHolder.title.setText(movie.getTitle());
//        holder.genre.setText(String.valueOf(movie.getGenreIds().get(0).getInt()));
            movieViewHolder.genre.setText(movie.getFirstGenre());
            movieViewHolder.year.setText(movie.getReleaseDate());
            movieViewHolder.eyeIv.setVisibility(movie.getSeen() ? View.VISIBLE : View.GONE);

            Glide.with(mContext)
                    .load(movie.getBackdropImageUri())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            ((MovieViewHolder) holder).progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(movieViewHolder.backdropIv);
        }
    }

    public void setIsMoreLoading(boolean isProgress) {
        isMoreLoading = isProgress;
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
