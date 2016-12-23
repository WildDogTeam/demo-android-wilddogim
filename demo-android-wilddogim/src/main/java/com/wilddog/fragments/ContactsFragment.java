package com.wilddog.fragments;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.activitys.ChatActivity;
import com.wilddog.adapters.ContactsAdapter;
import com.wilddog.model.FriendInfo;
import com.wilddog.type.ChatType;
import com.wilddog.utils.Constant;
import com.wilddog.utils.GenerateConversationId;
import com.wilddog.utils.SharedPrefrenceTool;
import com.wilddog.wilddogim.WilddogIM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ContactsFragment extends BaseFragment {

    private ListView lv_Friend;
    private List<FriendInfo> friendInfoList = new ArrayList<>();
    private ContactsAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public View initView() {
        View view = View.inflate(getActivity(), R.layout.fragment_contacts, null);
        lv_Friend = (ListView) view.findViewById(R.id.lv_friends);
        adapter = new ContactsAdapter(getActivity(), friendInfoList);
        lv_Friend.setAdapter(adapter);
        initFriendData();
        lv_Friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendInfo friendInfo = friendInfoList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constant.USER_ID, GenerateConversationId.genSingleChatID(WilddogIM.getCurrentUser().getUid(),friendInfo.getId()));
                intent.putExtra(Constant.CHATTYPE, ChatType.SINGLE_CHAT);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initFriendData() {
        friendInfoList.addAll(WilddogIMApplication.getFriendManager().getFriendList(SharedPrefrenceTool.getUserID(getActivity())));
        handler.sendEmptyMessage(0);

    }

}
