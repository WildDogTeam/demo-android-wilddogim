package com.wilddog.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.adapters.AddGroupMemberAdapter;
import com.wilddog.model.FriendInfo;
import com.wilddog.type.ChatType;
import com.wilddog.utils.AlertMessageUtil;
import com.wilddog.utils.Constant;
import com.wilddog.wilddogim.Conversation;
import com.wilddog.wilddogim.WilddogIMClient;

import com.wilddog.wilddogim.error.WilddogIMError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseMemberActivity extends AppCompatActivity {
    private ListView addFriendsList;
    private AddGroupMemberAdapter addGroupMemberAdapter;
    private List<FriendInfo> friendlists = new ArrayList<FriendInfo>();
    private List<String> addMemnerList = new ArrayList<>();
    private Button createGroup;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addGroupMemberAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_member);
        initView();
        initData();
    }

    private void initView() {
        addFriendsList = (ListView) findViewById(R.id.lv_add_members);
        createGroup = (Button) findViewById(R.id.bt_create_group);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  TODO 群邀请
                getMemberList();
            }
        });
        addGroupMemberAdapter = new AddGroupMemberAdapter(ChooseMemberActivity.this, friendlists);
        addFriendsList.setAdapter(addGroupMemberAdapter);
        addFriendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddGroupMemberAdapter.ViewHolder holder = (AddGroupMemberAdapter.ViewHolder) view.getTag();
                holder.bselect.toggle();
                AddGroupMemberAdapter.getIsSelected().put(position, holder.bselect.isChecked());
            }
        });

    }

    private void initData() {
        friendlists.addAll(WilddogIMApplication.getFriendManager().getFriendList(WilddogIMClient.getCurrentUser().getUid()));
        handler.sendEmptyMessage(0);
    }


    private void getMemberList() {

        HashMap<Integer, Boolean> bMap = AddGroupMemberAdapter.getIsSelected();
        ArrayList<String> lUsers = new ArrayList<String>();
        for (Map.Entry<Integer, Boolean> entry : bMap.entrySet()) {
            if (entry.getValue()) {
                lUsers.add(friendlists.get(entry.getKey()).getId());
            }
        }
        if (lUsers.size() < 1) {
            AlertMessageUtil.showShortToast("至少需要一个邀请成员");
            return;
        } else {
            // 邀请 群成员
            sendCreateGroupRequest(lUsers);

        }


    }

    private void sendCreateGroupRequest(final List<String> ids) {
        WilddogIMClient.newConversation(ids, new WilddogIMClient.CompletionListener() {
                    @Override
                    public void onComplete(WilddogIMError error, Conversation wilddogConversation) {
                        if(error==null){
                            Log.d("groupinfo", wilddogConversation.toString());
                            if(ids.size()>1){
                                Intent intent = new Intent(ChooseMemberActivity.this, ChatActivity.class);
                                intent.putExtra(Constant.USER_ID, wilddogConversation.getConversationId());
                                intent.putExtra(Constant.CHATTYPE, ChatType.CHAT_GROUP);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(ChooseMemberActivity.this, ChatActivity.class);
                                intent.putExtra(Constant.USER_ID, wilddogConversation.getConversationId());
                                intent.putExtra(Constant.CHATTYPE, ChatType.SINGLE_CHAT);
                                startActivity(intent);
                                finish();
                            }

                        }else {
                            Log.e("groupinfo", error.toString());
                        }
                    }
                });




    }
}
