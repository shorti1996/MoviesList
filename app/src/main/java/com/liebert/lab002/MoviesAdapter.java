package com.liebert.lab002;

/**
 * Created by shorti1996 on 08.04.2017.
 */

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

        public MovieViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_tv);
            genre = (TextView) view.findViewById(R.id.genre_tv);
            year = (TextView) view.findViewById(R.id.year_tv);
            backdropIv = (ImageView) view.findViewById(R.id.backdrop_iv);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Toast.makeText(mContext, "" + adapterPosition, Toast.LENGTH_SHORT).show();
/*            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);*/
        }
    }

    public MoviesAdapter(/*List<Movie> moviesList,*/ Context context) {
        this.moviesList = new ArrayList<>();
        this.mContext = context;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.onLoadMoreListener = listener;
    }

    public void clearMoviesList() {
        this.moviesList = null;
        notifyDataSetChanged();
    }

    public void swapMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    public List<Movie> getMoviesList() {
        return this.moviesList;
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

            Glide.with(mContext)
                    .load(movie.getBackdropImageUri())
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(movieViewHolder.backdropIv);
        }
    }

    public void setProgressMore(boolean isProgress) {
        if (isProgress) {
            // never update UI directly from worker thread
            new Handler().post(() -> {
                //in MainActivity
//                moviesList.add(null);
                notifyItemInserted(moviesList.size()); //so on the last position because one for loading view is added
//                notifyDataSetChanged();
            });
        } else {
            //TODO This will probably produce errors (check size)
            isMoreLoading = false;
//            moviesList.remove(moviesList.size() - 1);
//            notifyItemRemoved(moviesList.size());
//            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
