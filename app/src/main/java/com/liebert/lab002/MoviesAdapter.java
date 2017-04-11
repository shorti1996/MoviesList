package com.liebert.lab002;

/**
 * Created by shorti1996 on 08.04.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liebert.lab002.Models.Movie;

import java.util.List;

/**
 * Created by shorti1996 on 08.04.2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private List<Movie> moviesList;
    private Context mContext;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final String TAG = MoviesAdapterViewHolder.class.getSimpleName();

        public TextView title, year, genre;
        public ImageView backdropIv;

        public MoviesAdapterViewHolder(View view) {
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
            Log.d(TAG, "" + adapterPosition);
            Toast.makeText(mContext, "" + adapterPosition, Toast.LENGTH_SHORT).show();
/*            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);*/
        }
    }

    public MoviesAdapter(List<Movie> moviesList, Context context) {
        this.moviesList = moviesList;
        this.mContext = context;
    }

    public void addMoviesToList(List<Movie> moviesList) {
        this.moviesList.addAll(moviesList);
        notifyDataSetChanged();
    }

    public void clearMoviesList() {
        this.moviesList = null;
        notifyDataSetChanged();
    }

    public void swapMoviesList(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MoviesAdapterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
//        holder.genre.setText(String.valueOf(movie.getGenreIds().get(0).getInt()));
        holder.genre.setText(movie.getFirstGenre());
        holder.year.setText(movie.getReleaseDate());

        Glide.with(mContext)
                .load(movie.getBackdropImageUri())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(holder.backdropIv);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
