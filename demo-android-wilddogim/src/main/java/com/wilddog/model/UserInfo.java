package com.wilddog.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/28.
 */
public class UserInfo implements Serializable {
    private String token;
    private String userID;
    private String userName;
    private String avatar;

    public UserInfo() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "String{" +
                "token='" + token + '\'' +
                ", userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
