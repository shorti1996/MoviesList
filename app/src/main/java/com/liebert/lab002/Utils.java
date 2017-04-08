package com.liebert.lab002;

import android.content.Context;

import com.liebert.lab002.Models.Genre;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by shorti1996 on 08.04.2017.
 */

public class Utils {

/*    public static List<Integer> translateGenres(List<Integer> genresList) {
        List<Integer> result = new ArrayList<>();
        if (genresList == null) {
            return result;
        }
        for (int genre : genresList) {

        }
    }*/

    public static String translateGenre(Realm realm, int genre) {
        String genreString = realm.where(Genre.class).equalTo("id", genre).findFirst().getName();
        return genreString;
    }

    public static String translateGenre(int genre) {
        switch (genre) {
            case 28:
                return "Action";
            case 12:
                return "Adventure";
            case 16:
                return "Animation";
            case 35:
                return "Comedy";
            case 80:
                return "Crime";
            case 99:
                return "Documentary";
            case 18:
                return "Drama";
            case 10751:
                return "Family";
            case 14:
                return "Fantasy";
            case 36:
                return "History";
            case 27:
                return "Horror";
            case 10402:
                return "Music";
            case 9648:
                return "Mystery";
            case 10749:
                return "Romance";
            case 878:
                return "Science Fiction";
            case 10770:
                return "TV Movie";
            case 53:
                return "Thriller";
            case 10752:
                return "War";
            case 37:
                return "Western";
            default:
                return "";
        }
    }

}
