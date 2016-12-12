package com.wilddog.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/8.
 */
public class DrawableManager {
    private static final String TAG = DrawableManager.class.getName();


    public static int getDrawableId(Context context) {
        int drawableResourceId = context.getResources().getIdentifier("push", "drawable", context.getPackageName());
        if (drawableResourceId == 0) {
            drawableResourceId = context.getResources().getIdentifier("push", "drawable", "com.wilddog.push.ext");
        }

        if (drawableResourceId == 0) {
            Log.e(TAG, String.format("Could not send notification for APP<%s> because of small icon with name %s  is not found", new Object[]{"imdemo", "push"}));
            return drawableResourceId;
        } else {
            return drawableResourceId;
        }
    }
}
