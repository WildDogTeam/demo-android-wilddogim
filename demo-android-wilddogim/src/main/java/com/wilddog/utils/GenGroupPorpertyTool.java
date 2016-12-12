package com.wilddog.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public class GenGroupPorpertyTool {
    public static String genGroupName(List<String> list ,String id){
        if(list==null){return id;}
        String name ="";
        switch (list.size()){
            case 0:
                name=id;
                break;
            case 1:
                name =FriendsManamger.getFriendInfoById(list.get(0)).getName();
                break;
            case 2:
                name =FriendsManamger.getFriendInfoById(list.get(0)).getName()+","+FriendsManamger.getFriendInfoById(list.get(1)).getName();
                break;
            case 3:
                name = FriendsManamger.getFriendInfoById(list.get(0)).getName()+","+FriendsManamger.getFriendInfoById(list.get(1)).getName()+","+FriendsManamger.getFriendInfoById(list.get(2)).getName();
                break;
            default:
                name = FriendsManamger.getFriendInfoById(list.get(0)).getName()+","+FriendsManamger.getFriendInfoById(list.get(1)).getName()+","+FriendsManamger.getFriendInfoById(list.get(2)).getName()+"...";
                break;
        }
        return name;

    }

    public static List<String> genGroupUrlList(List<String> list){
        List<String> groupMemberAvatarList = new ArrayList<>();
        for(String uid:list){
            groupMemberAvatarList.add(FriendsManamger.getFriendInfoById(uid).getAvatar());
        }
        if(groupMemberAvatarList.size()>0){
            return groupMemberAvatarList;
        }else {
            return null;
        }
    }
}
