package com.liebert.lab002.Helpers;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by shorti1996 on 09.05.2017.
 */

public class Utils {
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    public static int getDisplayOrientation(Context context){
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return display.getRotation();
    }

    public static class ImagePath {
        public static final String WIDTH_92 = "w92";
        public static final String WIDTH_154 = "w154";
        public static final String WIDTH_185 = "w185";
        public static final String WIDTH_342 = "w342";
        public static final String WIDTH_780 = "w780";
        public static final String WIDTH_ORIGINAL = "original";

        public static Uri getFullImagePath(String filePath, String width) {
            if (width.equals(null)) {
                width = WIDTH_185;
            }
            return new Uri.Builder().path("http://image.tmdb.org/t/p/")
                    .appendPath(width)
                    .appendPath(filePath)
                    .build();
        }
    }

}
