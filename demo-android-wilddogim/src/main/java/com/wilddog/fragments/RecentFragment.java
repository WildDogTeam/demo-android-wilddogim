package com.wilddog.fragments;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.activitys.ChatActivity;
import com.wilddog.activitys.MainActivity;
import com.wilddog.adapters.RecentListAdapter;
import com.wilddog.model.FriendInfo;
import com.wilddog.model.RecentEntity;
import com.wilddog.type.ChatType;
import com.wilddog.utils.AlertMessageUtil;
import com.wilddog.utils.Constant;
import com.wilddog.utils.GenGroupPorpertyTool;
import com.wilddog.utils.GenerateConversationId;
import com.wilddog.wilddogim.Conversation;
import com.wilddog.wilddogim.WilddogIMClient;
import com.wilddog.wilddogim.message.Message;

import com.wilddog.wilddogim.message.MessageType;
import com.wilddog.wilddogim.message.TextMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 */
public class RecentFragment extends BaseFragment {
    private ListView recentList;
    private List<RecentEntity> entitys;
    private RecentListAdapter adapter;
    public int backPos = 0;
    private boolean mHidden = false;
    private RelativeLayout rl_nomessage;
    private Button btn_makeconversation;
    private WilddogIMClient client;
    List<Conversation> conversationList;
    @Override
    public View initView() {

        View view = View.inflate(getActivity(), R.layout.fg_recent, null);
        getChatList(view);
        client=WilddogIMApplication.getClient();
        return view;
    }

    private void getChatList(View view) {
        recentList = (ListView) view.findViewById(R.id.list_recent);
        rl_nomessage = (RelativeLayout) view.findViewById(R.id.rl_nomessage);
        btn_makeconversation = (Button) view.findViewById(R.id.btn_make_conversation);
        btn_makeconversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).rbtn_contacts.performClick();
            }
        });
        entitys = new ArrayList<RecentEntity>();
        adapter = new RecentListAdapter(getActivity(), entitys);
        recentList.setAdapter(adapter);
        recentList.setVisibility(View.VISIBLE);
        rl_nomessage.setVisibility(View.GONE);
        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecentEntity entity = entitys.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(Constant.USER_ID, entity.getId());
                intent.putExtra(Constant.CHATTYPE, entity.getType());
                startActivity(intent);
            }
        });
       recentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
               AlertMessageUtil.Showdialog("你确定要删除会话吗？",getActivity(), new AlertMessageUtil.Listener() {
                   @Override
                   public void onok() {
                       Conversation conversation = conversationList.get(position);
                       conversation.delete();
                       entitys.remove(position);
                       adapter.notifyDataSetChanged();
                   }

                   @Override
                   public void oncancel() {

                   }
               });
               return true;
           }
       });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.mHidden = hidden;
        if (!mHidden) {
            Log.d("onHiddenChanged", mHidden + "");
            loadRecentContent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mHidden) {
            Log.d("onResume", mHidden + "");
            //登陆成功拉取消息
            loadRecentContent();
            //	fromChatActivity = false;
        }
    }


    private void refreshList() {
        recentList.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        recentList.setSelection(backPos);
    }

    public void loadRecentContent() {

        entitys.clear();
        conversationList= client.getConversations();
        if (conversationList.size() == 0) {
            // 为空
            recentList.setVisibility(View.GONE);
            rl_nomessage.setVisibility(View.VISIBLE);
        } else {

            recentList.setVisibility(View.VISIBLE);
            rl_nomessage.setVisibility(View.GONE);

            for (Conversation wilddogConversation : conversationList) {
                RecentEntity entity = new RecentEntity();
                String receiver = wilddogConversation.getConversationId();

                if (receiver.startsWith("GI")) {
                    entity.setId(receiver);
                    entity.setName(GenGroupPorpertyTool.genGroupName(wilddogConversation.getMembers(),receiver));
                    entity.setAvatar(Constant.DEFAULT_AVATAR_URL);
                    entity.setCount(wilddogConversation.getTotalUnreadMessageCount());
                    entity.setAvatarList(GenGroupPorpertyTool.genGroupUrlList(wilddogConversation.getMembers()));
                    entity.setType(ChatType.CHAT_GROUP);
                } else {
                    String toId = GenerateConversationId.getReceiver(receiver);
                    FriendInfo friendInfo = WilddogIMApplication.getFriendManager().getFriendInfoById(toId);
                    if (friendInfo != null) {
                        entity.setName(friendInfo.getName());
                        entity.setAvatar(friendInfo.getAvatar());
                    } else {
                        entity.setName(toId);
                        entity.setAvatar(Constant.DEFAULT_AVATAR_URL);
                    }

                    entity.setAvatarList(Arrays.asList(friendInfo.getAvatar()));
                    entity.setId(receiver);
                    entity.setCount(wilddogConversation.getTotalUnreadMessageCount());
                    entity.setType(ChatType.SINGLE_CHAT);
                }
                List<Message> list = wilddogConversation.getMessagesFromLast( null,20);
                if (list.size() > 0) {
                    Message message = list.get(0);
                    entity.setTime(message.getSendAt() + "");
                    if(message.getMessageType()== MessageType.TEXT){
                        entity.setContent(((TextMessage) message).getText());
                    }else if((message.getMessageType()== MessageType.IMAGE)){
                        entity.setContent("图片");
                    }else if((message.getMessageType()== MessageType.VOICE)){
                        entity.setContent("语音");
                    }else if((message.getMessageType()== MessageType.GROUPTIP)){
                        entity.setContent("群成员变化消息");
                    }
                    entitys.add(entity);
                } else {
                    entity.setTime(null);
                    entity.setContent(null);
                    entitys.add(entity);
                }
                refreshList();
            }
        }
    }
}
