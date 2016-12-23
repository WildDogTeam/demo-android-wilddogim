package com.wilddog.utils;

import com.wilddog.wilddogim.WilddogIM;

/**
 * Created by Administrator on 2016/6/29.
 */
public class GenerateConversationId {
    private static final String SEPARATOR = "$";
    public static String genSingleChatID(String fromId, String toID) {
        String conversationId;
        int result = fromId.compareTo(toID);
        if (result > 0) {
            return toID + SEPARATOR + fromId;
        } else if (result < 0) {
            return fromId + SEPARATOR + toID;
        } else {
            // 不能出现相等的
            return fromId + SEPARATOR + toID;
        }

    }

    public static String getReceiver(String roomid) {

        if (roomid.startsWith("GI")) {
            return roomid;
        } else {
            String firstid = roomid.substring(0, roomid.indexOf(SEPARATOR));
            String secondid = roomid.substring(roomid.indexOf(SEPARATOR) + 1, roomid.length());
            if (firstid.equals(WilddogIM.getCurrentUser().getUid())) {
                return secondid;
            } else {
                return firstid;
            }
        }
    }

}
