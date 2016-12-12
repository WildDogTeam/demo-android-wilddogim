package com.wilddog.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.wilddog.activitys.SpaceImageDetailActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/28.
 */
public class EnlargePictureTool {
    public static void enlarge(final Context context, final ImageView imageView, final String url){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SpaceImageDetailActivity.class);
                intent.putExtra("images",  url);
                int[] location = new int[2];
                imageView.getLocationOnScreen(location);
                intent.putExtra("locationX", location[0]);
                intent.putExtra("locationY", location[1]);
                intent.putExtra("width", imageView.getWidth());
                intent.putExtra("height", imageView.getHeight());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(0, 0);
            }
        });

    }
}
