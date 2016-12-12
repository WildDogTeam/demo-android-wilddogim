package com.wilddog;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.wilddog.utils.Constant;
import com.wilddog.utils.FriendsManamger;
import com.wilddog.wilddogim.WilddogIMClient;
import com.nostra13.universalimageloader.cache.memory.MemoryCache;
import java.io.File;

/**
 * Created by Administrator on 2016/6/28.
 */
public class WilddogIMApplication extends Application {
    private static RequestQueue mQueue;
    private static FriendsManamger friendsManamger;
    private static WilddogIMApplication application;
    public static DisplayImageOptions mNormalImageOptions;
    private static WilddogIMClient client;
    public static final String IMAGES_FOLDER = Constant.IMAG_DIR;
    int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 5);
    MemoryCache memoryCache;

    @Override
    public void onCreate() {
        super.onCreate();
        mQueue = Volley.newRequestQueue(this);
        application = this;
        friendsManamger = FriendsManamger.getFriendsManager();
        client=client.newInstance(this,"wdimdemo");
        mNormalImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisc(true)
                .resetViewBeforeLoading(true).build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            memoryCache = new LruMemoryCache(memoryCacheSize);
        } else {
            memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
        }
        // This
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).defaultDisplayImageOptions(mNormalImageOptions)
                .denyCacheImageMultipleSizesInMemory().discCache(new UnlimitedDiskCache(new File(IMAGES_FOLDER)))
                // .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .memoryCache(memoryCache)
                .memoryCacheExtraOptions(480, 800)
                // default = device screen dimensions 内存缓存文件的最大长宽
                .diskCacheExtraOptions(480, 800, null)
                // .memoryCacheSize(memoryCacheSize)
                .tasksProcessingOrder(QueueProcessingType.LIFO).threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static WilddogIMClient getClient(){
        return client;
    }

    public static RequestQueue getRequestQueue() {
        return mQueue;
    }

    public static Context getContext() {
        return application;
    }

    public static FriendsManamger getFriendManager() {
        return friendsManamger;
    }



}
