package com.liebert.lab002;

/**
 * Created by shorti1996 on 08.04.2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liebert.lab002.Models.Movie;

import java.util.List;

import butterknife.BindView;

/**
 * Created by shorti1996 on 08.04.2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private List<Movie> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_tv);
            genre = (TextView) view.findViewById(R.id.genre_tv);
            year = (TextView) view.findViewById(R.id.year_tv);
        }
    }


    public MoviesAdapter(List<Movie> moviesList) {
        this.moviesList = moviesList;
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
//        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getReleaseDate());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
