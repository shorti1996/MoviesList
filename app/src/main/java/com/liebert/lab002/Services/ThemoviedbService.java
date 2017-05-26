package com.liebert.lab002.Services;

import com.liebert.lab002.Models.Credits;
import com.liebert.lab002.Models.GenresList;
import com.liebert.lab002.Models.MovieImages;
import com.liebert.lab002.Models.MoviesData;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by shorti1996 on 07.04.2017.
 */

public interface ThemoviedbService {
    String SERVICE_ENDPOINT = "https://api.themoviedb.org/3/";
    String API_KEY = "8310fbcd5170a2a381e1723ea77f2f12";
    String DISCOVER = "discover/movie";
    String GENRES = "genre/movie/list" + "?api_key=" + API_KEY;
    String MOVIE_IMAGES = "movie/{movie_id}/images";
    String MOVIE_CREDITS = "movie/{movie_id}/credits";
    String BACKDROP_IMAGE_ENDPOINT = "https://image.tmdb.org/t/p/w780/";
    String SMALL_IMAGE_ENDPOINT = "https://image.tmdb.org/t/p/w185/";

/*    @GET("DISCOVER/movie?{apiKey}")
    Observable<MoviesData> getMoviesDiscover(@Path(API_KEY) String apiKey);*/

    @GET(ThemoviedbService.DISCOVER)
    Observable<MoviesData> getMoviesDiscover(@Query("api_key") String apiKey, @Query("page") int page);

    // https://api.themoviedb.org/3/genre/movie/list?api_key=8310fbcd5170a2a381e1723ea77f2f12
    @GET(ThemoviedbService.GENRES)
    Observable<GenresList> getGenres();

    // https://api.themoviedb.org/3/movie/283995/images?api_key=8310fbcd5170a2a381e1723ea77f2f12
    @GET(ThemoviedbService.MOVIE_IMAGES)
    Observable<MovieImages> getMovieImages(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    //backdrop img
    //https://image.tmdb.org/t/p/w780/wSJPjqp2AZWQ6REaqkMuXsCIs64.jpg
//    @GET(ThemoviedbService.BACKDROP_IMAGE_ENDPOINT + "/")
//    Observable<Ima>

    // https://api.themoviedb.org/3/movie/274857/credits?api_key=8310fbcd5170a2a381e1723ea77f2f12
    @GET(ThemoviedbService.MOVIE_CREDITS)
//    Observable<ResponseBody> - cool for debug
    Observable<Credits> getMovieCredits(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

}
