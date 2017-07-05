package com.test.mobileguardtest.utils;

import android.content.Context;

/**
 * Created by kona on 2017/7/4.
 */

public class DensityUtils {
    public static int dip2px(Context context,int dip){
        float density = context.getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }

    public static int px2dip(Context context,int px){
        float density = context.getResources().getDisplayMetrics().density;
        int dip = (int) (px / density + 0.5f);
        return dip;
    }
}
