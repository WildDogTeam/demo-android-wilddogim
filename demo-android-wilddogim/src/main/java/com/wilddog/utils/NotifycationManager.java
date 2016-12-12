package com.wilddog.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.wilddog.R;
import com.wilddog.activitys.ChatActivity;
import com.wilddog.type.ChatType;
import com.wilddog.wilddogim.message.Message;


import org.json.JSONObject;

/**
 * Created by Administrator on 2016/7/8.
 */
public class NotifycationManager {
    private static int count = 0;

    public static void sendNotifycation(Context context, String title, String content, Message message) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content) //设置通知栏显示内容
                .setContentIntent(getDefalutIntent(context , message)) //设置通知栏点击意图
//  .setNumber(number) //设置通知集合的数量
                .setTicker("您有一条新消息") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
//  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);//设置通
        //Notification.FLAG_AUTO_CANCEL
        count++;
        Notification notification =mBuilder.build();
        notification.flags=Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(count, notification);
    }

    private static PendingIntent getDefalutIntent(Context context,  Message message) {
        Intent intent = getIntent(context, message);
        Log.e("BroadCast","getDefalutIntent CHATTYPE:"+intent.getStringExtra(Constant.CHATTYPE)+",USER_ID:"+intent.getStringExtra(Constant.USER_ID));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    private static Intent getIntent(Context context, Message message) {
        // 携带数据
        Intent chatIntent = new Intent(context, ChatActivity.class);
        JSONObject jsonObject = null;
       String conversationId= message.getConversation().getConversationId();
        // 推送部分

        if (conversationId.contains("-")) {
            chatIntent.putExtra(Constant.USER_ID, conversationId);
            chatIntent.putExtra(Constant.CHATTYPE, ChatType.SINGLE_CHAT);
            Log.e("BroadCast","getIntent USER_ID:"+conversationId+",CHATTYPE"+ ChatType.SINGLE_CHAT);
        } else {
            chatIntent.putExtra(Constant.USER_ID, conversationId);
            chatIntent.putExtra(Constant.CHATTYPE, ChatType.CHAT_GROUP);
            Log.e("BroadCast","getIntent USER_ID:"+conversationId+",CHATTYPE"+ ChatType.CHAT_GROUP);
        }
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return chatIntent;
    }
}
