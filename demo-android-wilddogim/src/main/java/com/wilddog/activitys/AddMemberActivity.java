package com.wilddog.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.adapters.AddGroupMemberAdapter;
import com.wilddog.model.FriendInfo;
import com.wilddog.utils.AlertMessageUtil;
import com.wilddog.utils.Constant;
import com.wilddog.wilddogim.Conversation;
import com.wilddog.wilddogim.WilddogIM;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMemberActivity extends AppCompatActivity {
    private ListView addFriendsList;
    private AddGroupMemberAdapter addGroupMemberAdapter;
    private List<FriendInfo> friendlists = new ArrayList<FriendInfo>();
    private List<String> addMemnerList = new ArrayList<>();
    private Button createGroup;
    private String groupId;
    private Conversation conversation;
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
        groupId = getIntent().getStringExtra(Constant.GROUPID);
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
        addGroupMemberAdapter = new AddGroupMemberAdapter(AddMemberActivity.this, friendlists);
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
        conversation=WilddogIM.newInstance().getConversation(groupId);

        List<FriendInfo> groupMemberInfos=new ArrayList<>();
        for(String id:conversation.getMembers()){
           groupMemberInfos.add(WilddogIMApplication.getFriendManager().getFriendInfoById(id));
        }
        List<FriendInfo> all=WilddogIMApplication.getFriendManager().getFriendList(WilddogIM.getCurrentUser().getUid());
        all.removeAll(groupMemberInfos);
        friendlists.addAll(all);
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
            conversation.addMember(lUsers);
           finish();
        }


    }


}
