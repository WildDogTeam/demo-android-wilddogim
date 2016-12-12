package com.wilddog.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;

import com.wilddog.model.FriendInfo;
import com.wilddog.model.UserInfo;
import com.wilddog.utils.Constant;
import com.wilddog.utils.SharedPrefrenceTool;
import com.wilddog.utils.Volleyutil;
import com.wilddog.wilddogauth.model.WilddogUser;
import com.wilddog.wilddogim.WilddogIMClient;
import com.wilddog.wilddogim.core.wildcallback.WildValueCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends BaseActivity {
    private EditText mEt_UserID;
    private Button mBtn_Login;
    private Button mBtn_choose_user;
    private WilddogIMClient client;
    String strUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        client=WilddogIMApplication.getClient();
   /*     mEt_UserID = (EditText) findViewById(R.id.et_userID);*/
        mBtn_Login = (Button) findViewById(R.id.btn_login);
        mBtn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkOnlineStatus();
            }
        });
        mBtn_choose_user= (Button) findViewById(R.id.btn_choose_user);
        mBtn_choose_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseUserToLogin();
            }
        });
    }
    // 获取未在线用户
    private void chooseUserToLogin(){
        startActivityForResult(new Intent(LoginActivity.this,ChooseLoginUserActivity.class),1);
    }



    private void checkOnlineStatus() {


        if (TextUtils.isEmpty(strUserID)) {
            Toast.makeText(LoginActivity.this,"当前用户为空",Toast.LENGTH_SHORT).show();
            return;
        }

        // 查询当前用户是否在线。

        String onlinePath =String.format(Constant.GET_CURRENT_USER_SATTUS,strUserID);
        Volleyutil.CONNECTGET(onlinePath, new Volleyutil.Listener() {
            @Override
            public void onsuccess(JSONObject jsonObject) {
                boolean status =false;
                try {
                    status = Boolean.parseBoolean(jsonObject.getString("connected"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(status){

                    Toast.makeText(LoginActivity.this,"当前用户已经在线",Toast.LENGTH_SHORT).show();
                }else {
                    login();
                }



    }
            @Override
            public void onfailure(JSONObject jsonObject) {
                Toast.makeText(LoginActivity.this,"查询在线状态失败",Toast.LENGTH_SHORT).show();
            }
        });

        }

    private void login(){
        String path = String.format(Constant.USER_LOGIN_URL, strUserID);

        Volleyutil.GET(path, new Volleyutil.Listener() {
            @Override
            public void onsuccess(JSONObject JsonObject) {
                final UserInfo user = parseUserInfo(JsonObject);
                //
                client.connect();
                client.addAuthStateListener(new WilddogIMClient.WilddogIMAuthStateListener() {
                    @Override
                    public void onAuthStateChanged(WilddogUser user) {
                        if(user==null){
                            // 为空
                        }else {
                            //登录成功
                            Log.d("result",user.getUid());
                        }
                    }
                });
                // 自定义token
                String token = user.getToken();
                client.signIn(token, new WildValueCallBack<WilddogUser>() {
                    @Override
                    public void onSuccess(WilddogUser wilddogUser) {
                        SharedPrefrenceTool.setUserID(LoginActivity.this, strUserID);
                        WilddogIMApplication.getFriendManager().saveUserInfo(user);
                        initFriendData(strUserID);
                    }

                    @Override
                    public void onFailed(int code, String des) {
                        Log.e("result",des);
                    }
                });
            }

            @Override
            public void onfailure(JSONObject JsonObject) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initFriendData(final String userID) {
        String path = String.format(Constant.USER_FRIENDS_URL, userID);
        Volleyutil.GET(path, new Volleyutil.Listener() {
            @Override
            public void onsuccess(JSONObject jsonObject) {
                List getFriends = new ArrayList();
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = (JSONObject) jsonArray.getJSONObject(i);
                        FriendInfo friendInfo = new FriendInfo();
                        friendInfo.setAvatar(object.getString("avatar"));
                        friendInfo.setId(object.getString("id"));
                        friendInfo.setName(object.getString("name"));
                        getFriends.add(friendInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getFriends.size() > 0) {
                    WilddogIMApplication.getFriendManager().saveFriendInfos(getFriends);
                }
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                goToNextPage(userID);
            }

            @Override
            public void onfailure(JSONObject jsonObject) {
                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void goToNextPage(String s) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constant.USER_ID, s);
        startActivity(intent);
        finish();
    }

    private UserInfo parseUserInfo(JSONObject jsonObject) {
        UserInfo userInfo = new UserInfo();
        // success 结果 {
        //  "code":0,
        //        "data":{"user":{"id":1,"name":"野狗1号","avatar":"https://img.wdstatic.cn/imdemo/1.png"},"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2IjowLCJpYXQiOjE0NjcwMjQxOTYsImQiOnsidWlkIjoiMSJ9fQ.AQKmO0aiIin4-GtvOPC8P4AFg8FyOoLusRG-2vZGOqo"}
        //}
        try {
            userInfo.setToken(jsonObject.getJSONObject("data").get("token").toString());
            userInfo.setAvatar(jsonObject.getJSONObject("data").getJSONObject("user").get("avatar").toString());
            userInfo.setUserID(jsonObject.getJSONObject("data").getJSONObject("user").get("id").toString());
            userInfo.setUserName(jsonObject.getJSONObject("data").getJSONObject("user").get("name").toString());
            SharedPrefrenceTool.saveUserInfo(userInfo, LoginActivity.this);
            // Push 登录时候初始化
//eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2IjowLCJpYXQiOjE1ODc3OTY0NDYsImQiOnsidWlkIjoiZy11c2VySWQifSwiZXhwIjoxNTg3Nzk2NDQ2LCJhZG1pbiI6dHJ1ZX0.kj01WkBnvVCDj9X0sk5G9pgcZsA1gUZbN8JiGAYNG5o
         /*   WilddogIMApplication.getPushManager().initialize(LoginActivity.this, userInfo.getUserID(), userInfo.getToken(), new SDKStateListener() {
                //    WilddogIMApplication.getPushManager().initialize(LoginActivity.this, "g-userId","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ2IjowLCJpYXQiOjE1ODc3OTY0NDYsImQiOnsidWlkIjoiZy11c2VySWQifSwiZXhwIjoxNTg3Nzk2NDQ2LCJhZG1pbiI6dHJ1ZX0.kj01WkBnvVCDj9X0sk5G9pgcZsA1gUZbN8JiGAYNG5o", new SDKStateListener() {
                @Override
                public void onClientInitialized(String s) {
                    Log.d("PushSDK", "Client init finished" + s);
                }
            });
            a82751dfe9f1f4d88b8530fb81cccb2f6
            // Push 登录时候打开
            if (!WilddogIMApplication.getPushManager().isPushTurnedOn(LoginActivity.this)) {
                WilddogIMApplication.getPushManager().turnOnPush(LoginActivity.this);
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && data!=null){
            strUserID = data.getStringExtra(Constant.CURRENT_USER);
            if(!TextUtils.isEmpty(strUserID)){
            mBtn_choose_user.setText(strUserID);}
        }
    }
}
