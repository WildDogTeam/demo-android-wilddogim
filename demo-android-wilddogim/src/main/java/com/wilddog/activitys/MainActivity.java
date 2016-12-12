package com.wilddog.activitys;



import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.fragments.ContactsFragment;
import com.wilddog.fragments.RecentFragment;
import com.wilddog.utils.Constant;
import com.wilddog.utils.NotifycationManager;
import com.wilddog.utils.ProgressManager;
import com.wilddog.wilddogim.WilddogIMClient;
import com.wilddog.wilddogim.message.MessageType;
import com.wilddog.wilddogim.message.TextMessage;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public RadioButton rbtn_recent, rbtn_contacts;
    private RecentFragment recentFragment;
    private ContactsFragment contactsFragment;
    private Button addButton;
    FragmentManager fgManager;
    private int currentTab = 1;
    private TextView mTV_Title;
    public String userID;
    private WilddogIMClient client;
    private EditText et_group_name;
    private Button loginOut;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mTV_Title.setText("WildIM");
                    break;
                case 2:
                    mTV_Title.setText("连接失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userID = getIntent().getStringExtra(Constant.USER_ID);

        fgManager = getSupportFragmentManager();
        client=WilddogIMApplication.getClient();
        client.addMessageListener(listener);
        client.addGroupChangeListener(groupChangeListener);
        onInit();


    }

    private WilddogIMClient.WilddogIMGroupChangeListener groupChangeListener =new WilddogIMClient.WilddogIMGroupChangeListener() {
        @Override
        public void memberJoined(String groupId, String owner, List<String> joinedUsers) {
            recentFragment.loadRecentContent();
        }

        @Override
        public void memberQuit(String groupId, String quitUser) {
            recentFragment.loadRecentContent();
        }

        @Override
        public void memberRemoved(String groupId, List<String> removeUsers) {
            recentFragment.loadRecentContent();
        }
    };

    private WilddogIMClient.WilddogIMMessageListener listener=new WilddogIMClient.WilddogIMMessageListener() {
        @Override
        public void onNewMessage(List<com.wilddog.wilddogim.message.Message> messages) {
            for(com.wilddog.wilddogim.message.Message wildMessage:messages){
            recentFragment.loadRecentContent();
            if (ProgressManager.isRunBackground(MainActivity.this)) {
                String content="";
                if(wildMessage.getMessageType()== MessageType.TEXT) {
                    content= ((TextMessage) wildMessage).getText();
                }else if(wildMessage.getMessageType()== MessageType.IMAGE){
                    content="图片";
                }else  if(wildMessage.getMessageType()== MessageType.VOICE){
                    content= "语音";
                }else {
                    content="其他";
                }
                NotifycationManager.sendNotifycation(MainActivity.this, WilddogIMApplication.getFriendManager().getFriendInfoById(wildMessage.getSender()).getName(),content , wildMessage);
            }
            }
        }
    };



    private void onInit() {
        mTV_Title = (TextView) findViewById(R.id.tv_title);
        mTV_Title.setText("WildIM");

        rbtn_recent = (RadioButton) findViewById(R.id.btn_goto_recent);
        rbtn_contacts = (RadioButton) findViewById(R.id.btn_goto_contacts);

        addButton = (Button) findViewById(R.id.bt_create_group);
        addButton.setOnClickListener(this);

        loginOut =(Button) findViewById(R.id.btn_login_out);
        loginOut.setOnClickListener(this);
        recentFragment = new RecentFragment();
        contactsFragment = new ContactsFragment();
        rbtn_recent.setOnClickListener(this);
        rbtn_contacts.setOnClickListener(this);
        changeFrament(recentFragment, null, "recentFragment");
        rbtn_recent.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_recent_pressed, 0, 0);
        rbtn_contacts.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.tab_contacts, 0, 0);

    }

    public void changeFrament(Fragment fragment, Bundle bundle, String tag) {
        for (int i = 0, count = fgManager.getBackStackEntryCount(); i < count; i++) {
            fgManager.popBackStack();
        }
        FragmentTransaction fg = fgManager.beginTransaction();
        fragment.setArguments(bundle);
        fg.add(R.id.fl_fgmanager, fragment, tag);
        fg.addToBackStack(tag);
        fg.commit();
    }

    public void changeRadioButtonImage(int btids) {
        int[] image_normal = {R.drawable.tab_recent, R.drawable.tab_contacts};
        int[] image_pressed = {R.drawable.tab_recent_pressed, R.drawable.tab_contacts_pressed
        };
        int[] rBtnIDs = {R.id.btn_goto_recent, R.id.btn_goto_contacts};
        switch (btids) {
            case R.id.btn_goto_recent:
                changeImage(image_normal, image_pressed, rBtnIDs, 0);
                currentTab = 1;
                break;
            case R.id.btn_goto_contacts:
                changeImage(image_normal, image_pressed, rBtnIDs, 1);
                currentTab = 2;
                break;
            default:
                break;
        }
    }

    public void changeImage(int[] image1, int[] image2, int[] rabtid, int index) {
        for (int i = 0; i < image1.length; i++) {
            if (i != index) {
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image1[i], 0, 0);
            } else {
                ((RadioButton) findViewById(rabtid[i])).setCompoundDrawablesWithIntrinsicBounds(0, image2[i], 0, 0);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // MessageManager.removeListener(listener, null);
        client.removeMessageListener(listener);
        client.removeGroupChangeListener(groupChangeListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_create_group:
                // 创建群 提示框
                gotoChooseMemberActivity();
                break;
            case R.id.btn_goto_recent:
                recentFragment = new RecentFragment();
                changeFrament(recentFragment, null, "recentFragment");
                changeRadioButtonImage(v.getId());
                break;
            case R.id.btn_goto_contacts:
                contactsFragment = new ContactsFragment();
                changeFrament(contactsFragment, null, "contactsFragment");
                changeRadioButtonImage(v.getId());
                break;
            case R.id.btn_login_out:

                //  弹出提示框
             AlertDialog.Builder dialog=  new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("确认退出吗？")
               .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       WilddogIMClient.signOut();
                       WilddogIMClient.disconnect();
                       finish();
                       startActivity(new Intent(MainActivity.this,LoginActivity.class));
                   }
               }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                   }
               }).show();



                break;
        }
    }


    private void gotoChooseMemberActivity() {
        Intent intent = new Intent(MainActivity.this, ChooseMemberActivity.class);
        startActivity(intent);
    }

}
