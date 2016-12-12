package com.wilddog.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Charles on 2016/4/25.
 */
public class CommonUtil {
    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}
