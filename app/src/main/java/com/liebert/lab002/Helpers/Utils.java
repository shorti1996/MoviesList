package com.liebert.lab002.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;

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
}
