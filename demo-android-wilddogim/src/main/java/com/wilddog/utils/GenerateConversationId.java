package com.wilddog.utils;

import com.wilddog.wilddogim.WilddogIMClient;

/**
 * Created by Administrator on 2016/6/29.
 */
public class GenerateConversationId {
    public static String genSingleChatID(String fromId, String toID) {
        String conversationId;
        int result = fromId.compareTo(toID);
        if (result > 0) {
            return toID + "-" + fromId;
        } else if (result < 0) {
            return fromId + "-" + toID;
        } else {
            // 不能出现相等的
            return fromId + "-" + toID;
        }

    }

    public static String getReceiver(String roomid) {

        if (roomid.startsWith("GI")) {
            return roomid;
        } /*else if (roomid.startsWith("GroupSystemRoom")) {
            return roomid;
        }*/ else {
            String firstid = roomid.substring(0, roomid.indexOf("-"));
            String secondid = roomid.substring(roomid.indexOf("-") + 1, roomid.length());
            if (firstid.equals(WilddogIMClient.getCurrentUser().getUid())) {
                return secondid;
            } else {
                return firstid;
            }
        }
    }

}
