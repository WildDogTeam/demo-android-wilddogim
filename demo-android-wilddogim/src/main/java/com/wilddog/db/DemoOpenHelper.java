package com.wilddog.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wilddog.WilddogIMApplication;
import com.wilddog.model.FriendInfo;
import com.wilddog.model.UserInfo;

import com.wilddog.utils.MD5;
import com.wilddog.utils.SharedPrefrenceTool;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/7.
 */
public class DemoOpenHelper extends SQLiteOpenHelper {
    private final String USERTABLE = "userInfo";
    private static String DBNAME;
    private Context mContext;
    private static DemoOpenHelper demoOpenHelper;
    private static String uid;

    private DemoOpenHelper(Context context) {
        super(context, DBNAME, null, 1);
        this.mContext = context;
    }

    public static DemoOpenHelper getInstance() {
        uid = SharedPrefrenceTool.getUserID(WilddogIMApplication.getContext());
        String fileName = MD5.GetMD5Code(uid);
        DBNAME = "USER" + fileName + ".db";
        if (demoOpenHelper == null) {
            demoOpenHelper = new DemoOpenHelper(WilddogIMApplication.getContext());

        }
        return demoOpenHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
  /*
        *  table_userinfo
        * */
        db.execSQL("CREATE TABLE if not exists " + USERTABLE + "(" + "uid text primary key," + "name text," +
                "avatar text" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertUserInfo(UserInfo info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("uid", info.getUserID());
        cv.put("name", info.getUserName());
        cv.put("avatar", info.getAvatar());

        long row = db.insert(USERTABLE, null, cv);

        return row;
    }

    public void insertFriendInfos(List<FriendInfo> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (FriendInfo friendInfo : list) {
             if(getFriendInfoById(friendInfo.getId())!=null){return;}else {
            ContentValues cv = new ContentValues();
            cv.put("uid", friendInfo.getId());
            cv.put("name", friendInfo.getName());
            cv.put("avatar", friendInfo.getAvatar());
            long row = db.insert(USERTABLE, null, cv);}
        }
    }

    public UserInfo getUserInfoById(String uid) {
        UserInfo info = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USERTABLE, null, "uid=?", new String[]{uid}, null, null, null, null);
        if (cursor.getCount() == 0) {
            return info;
        } else {
            while (cursor.moveToNext()) {
                info = new UserInfo();
                info.setUserID(cursor.getString(cursor.getColumnIndex("uid")));
                info.setUserName(cursor.getString(cursor.getColumnIndex("name")));
                info.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            }
        }
        cursor.close();
        return info;

    }


    public FriendInfo getFriendInfoById(String uid) {
        FriendInfo info = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USERTABLE, null, "uid=?", new String[]{uid}, null, null, null, null);
        if (cursor.getCount() == 0) {
            return info;
        } else {
            while (cursor.moveToNext()) {
                info = new FriendInfo();
                info.setId(cursor.getString(cursor.getColumnIndex("uid")));
                info.setName(cursor.getString(cursor.getColumnIndex("name")));
                info.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
            }
        }
        cursor.close();
        return info;

    }


    public List<FriendInfo> getUserFriendList(String uid) {
        List<FriendInfo> friendInfos = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(USERTABLE, null, "uid NOT IN (?)", new String[]{uid}, null, null, null, null);
        if (cursor.getCount() == 0) {
            return friendInfos;
        } else {
            friendInfos = new ArrayList<>();
            while (cursor.moveToNext()) {
                FriendInfo info = new FriendInfo();
                info.setId(cursor.getString(cursor.getColumnIndex("uid")));
                info.setName(cursor.getString(cursor.getColumnIndex("name")));
                info.setAvatar(cursor.getString(cursor.getColumnIndex("avatar")));
                friendInfos.add(info);
            }
        }
        cursor.close();
        return friendInfos;
    }

}
