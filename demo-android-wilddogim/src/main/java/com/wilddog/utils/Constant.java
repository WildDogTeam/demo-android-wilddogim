package com.wilddog.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/6/28.
 */
public class Constant {
    public static final boolean isRelease = true;
    // URL
    public static final String USER_APP_SERVER_BASEURL =(isRelease ?"https":"http")+"://imdemo.wilddog.com";
    public static final String USER_LOGIN_URL= USER_APP_SERVER_BASEURL +"/login?userId=%s";
    public static final String USER_FRIENDS_URL= USER_APP_SERVER_BASEURL +"/friend?userId=%s";

    // 获取token的接口
    public static final String OFFLINE_USER_BASE_URL=(isRelease ?"https":"http")+"://im.wilddog.com/v1/appId/wdimdemo";
    public static final String GET_OFFLINE_USER= OFFLINE_USER_BASE_URL+"/connected";
    public static final String GET_CURRENT_USER_SATTUS= OFFLINE_USER_BASE_URL+"/userId/%s/connected";

    // properties
    public static final String USER_ID="userID";

    public static final int PAGESIZE=20;

    // 群聊名称
    public static final String GROUPNAME="group";
    public static final String GROUPID="groupId";

    //
    public static final String CURRENT_USER ="currentuser";
    // 类型
    public static final String CHATTYPE="chattype";

    //
    public static final String DEFAULT_AVATAR_URL="https://img.wdstatic.cn/imdemo/1.png";

    //
    public static String IMAG_DIR = Environment.getExternalStorageDirectory()+ File.separator + "WildIMClient"+ File.separator + "camera" + File.separator;

}
