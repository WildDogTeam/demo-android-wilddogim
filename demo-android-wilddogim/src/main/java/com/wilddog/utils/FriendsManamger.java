package com.wilddog.utils;

import com.wilddog.db.DemoOpenHelper;
import com.wilddog.model.FriendInfo;
import com.wilddog.model.UserInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/6/30.
 */
public class FriendsManamger {
    private static FriendsManamger friendsManamger;

    private FriendsManamger() {

    }

    public static FriendsManamger getFriendsManager() {
        if (friendsManamger == null) {
            friendsManamger = new FriendsManamger();
        }
        return friendsManamger;
    }

    public static void saveFriendInfos(List<FriendInfo> list) {

        DemoOpenHelper.getInstance().insertFriendInfos(list);

    }

    public static void saveUserInfo(UserInfo userInfo) {
        if(DemoOpenHelper.getInstance().getFriendInfoById(userInfo.getUserID())==null){
        DemoOpenHelper.getInstance().insertUserInfo(userInfo);}
    }

    public static FriendInfo getFriendInfoById(String id) {
        return DemoOpenHelper.getInstance().getFriendInfoById(id);
    }

    public static List<FriendInfo> getFriendList(String uid) {
        return DemoOpenHelper.getInstance().getUserFriendList(uid);
    }

}
