package com.wilddog.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.wilddog.model.UserInfo;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by Administrator on 2016/6/28.
 */
public class SharedPrefrenceTool {
    private static SharedPreferences sp;
    private static final String FILE_NAME = "share_date";

    public static void setUserID(Context context, String userID) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userId", userID);
        editor.commit();
    }

    public static String getUserID(Context context) {
        String userID;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        userID = sp.getString("userId", "1");
        return userID;
    }

    public static void setToID(Context context, String userID) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("toID", userID);
        editor.commit();
    }

    public static String getToID(Context context) {
        String userID;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        userID = sp.getString("toID", "1");
        return userID;
    }


    public static void saveUserInfo(UserInfo userinfo, Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(userinfo);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = new String(Base64.encodeBase64(baos
                    .toByteArray()));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userinfo", oAuth_Base64);

            editor.commit();
        } catch (IOException e) {
            // TODO Auto-generated
        }

    }

    public static UserInfo readUserInfo(Context context) {
        UserInfo userInfo = null;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String productBase64 = sp.getString("userinfo", "");

        //读取字节
        byte[] base64 = Base64.decodeBase64(productBase64.getBytes());

        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                userInfo = (UserInfo) bis.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userInfo;
    }

    public static void saveLastMessageId(String lastMessageId, Context context) {
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastmessageid", lastMessageId);
        editor.commit();
    }

    public static String getLastMessageId(Context context) {
        String userID;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        userID = sp.getString("lastmessageid", null);
        return userID;
    }


}
