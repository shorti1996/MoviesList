package com.liebert.lab002.Services;

import com.liebert.lab002.Models.GenresList;
import com.liebert.lab002.Models.MoviesData;

import io.reactivex.Observable;
import retrofit2.http.GET;


/**
 * Created by shorti1996 on 07.04.2017.
 */

public interface ThemoviedbService {
    String SERVICE_ENDPOINT = "https://api.themoviedb.org/3/";
    String API_KEY = "8310fbcd5170a2a381e1723ea77f2f12";
    String DISCOVER = "discover/movie" + "?api_key=" + API_KEY;
    String GENRES = "genre/movie/list" + "?api_key=" + API_KEY;

/*    @GET("DISCOVER/movie?{apiKey}")
    Observable<MoviesData> getMoviesDiscover(@Path(API_KEY) String apiKey);*/

    @GET(ThemoviedbService.DISCOVER)
    Observable<MoviesData> getMoviesDiscover();

    //https://api.themoviedb.org/3/genre/movie/list?api_key=8310fbcd5170a2a381e1723ea77f2f12
    @GET(ThemoviedbService.GENRES)
    Observable<GenresList> getGenres();

}
