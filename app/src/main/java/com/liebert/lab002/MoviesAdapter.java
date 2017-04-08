package com.liebert.lab002;

/**
 * Created by shorti1996 on 08.04.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.liebert.lab002.Models.Movie;

import java.util.List;

import butterknife.BindView;

/**
 * Created by shorti1996 on 08.04.2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<Movie> moviesList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public ImageView backdropIv;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_tv);
            genre = (TextView) view.findViewById(R.id.genre_tv);
            year = (TextView) view.findViewById(R.id.year_tv);
            backdropIv = (ImageView) view.findViewById(R.id.backdrop_iv);
        }
    }


    public MoviesAdapter(List<Movie> moviesList, Context context) {
        this.moviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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
