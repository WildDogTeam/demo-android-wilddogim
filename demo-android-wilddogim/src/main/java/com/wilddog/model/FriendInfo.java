package com.wilddog.model;

/**
 * Created by Administrator on 2016/6/28.
 */
public class FriendInfo {
    private String avatar;
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FriendInfo() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        FriendInfo s=(FriendInfo)obj;
        return id.equals(s.getId()) && name.equals(s.getName())&&avatar.equals(s.getAvatar());
    }
}
