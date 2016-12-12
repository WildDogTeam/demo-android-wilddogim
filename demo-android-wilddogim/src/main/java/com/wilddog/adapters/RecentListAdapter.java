package com.wilddog.adapters;


import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.wilddog.R;
import com.wilddog.model.RecentEntity;
import com.wilddog.utils.Constant;
import com.wilddog.utils.DateHelper;
import com.wilddog.view.customview.NineGridImageView;
import com.wilddog.view.customview.NineGridImageViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecentListAdapter extends BaseAdapter {


    private static final String TAG = RecentListAdapter.class.getSimpleName();
    private Context mContext;
    private List<RecentEntity> listRecentMsg;
    private LayoutInflater inflater;

    public RecentListAdapter(Context context, List<RecentEntity> list) {
        // TODO Auto-generated constructor stub
        this.listRecentMsg = list;
        this.mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listRecentMsg.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listRecentMsg.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void refresh() {
        this.notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.recent_item, null);
            holder = new ViewHolder(convertView);
            if(listRecentMsg.get(position).getAvatarList()!=null){
                holder.bind(listRecentMsg.get(position).getAvatarList());}else {
                holder.bind(Arrays.asList(Constant.DEFAULT_AVATAR_URL));
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if(listRecentMsg.get(position).getAvatarList()!=null){
                holder.bind(listRecentMsg.get(position).getAvatarList());}else {
                holder.bind(Arrays.asList(Constant.DEFAULT_AVATAR_URL));
            }
        }

        if(listRecentMsg.get(position).getAvatarList()!=null){
            holder.bind(listRecentMsg.get(position).getAvatarList());}else {
            holder.bind(Arrays.asList(Constant.DEFAULT_AVATAR_URL));
        }
        final RecentEntity entity = listRecentMsg.get(position);
        holder.userName.setText((entity.getName() != null && !entity.getName().trim().equals("")) ? entity.getName() : entity.getId());
        if (TextUtils.isEmpty(entity.getTime())) {
            holder.sendTime.setText("");
        } else {
            holder.sendTime.setText(DateHelper.GetStringFormat(Long.parseLong(entity.getTime())));
        }
        if (TextUtils.isEmpty(entity.getContent())) {
            holder.message.setText("");
        } else {
            holder.message.setText(entity.getContent());
        }

        if (0 == entity.getCount()) {
            holder.unread_num.setVisibility(View.GONE);
        } else {
            holder.unread_num.setVisibility(View.VISIBLE);
            holder.unread_num.setText(String.valueOf(entity.getCount()));
        }
        return convertView;
    }




    private static class ViewHolder {
        TextView userName;
        TextView message;
        TextView unread_num;
        TextView sendTime;
        NineGridImageView avatar;
        private NineGridImageViewAdapter<String> mAdapter = new NineGridImageViewAdapter<String>() {
            @Override
            protected void onDisplayImage(Context context, ImageView imageView, String s) {
                ImageLoader.getInstance().displayImage(s,imageView);
               
            }

            @Override
            protected ImageView generateImageView(Context context) {
                return super.generateImageView(context);
            }

        };
        public void bind(List<String> urls) {
            avatar.setImagesData(urls);
        }

        public ViewHolder(View convertView) {

           userName = (TextView) convertView.findViewById(R.id.recent_name);
            sendTime = (TextView) convertView.findViewById(R.id.recent_time);
            message = (TextView) convertView.findViewById(R.id.recent_msg);
            unread_num = (TextView) convertView.findViewById(R.id.recent_unread_num);
            avatar = (NineGridImageView) convertView.findViewById(R.id.img_avatar);
            avatar.setAdapter(mAdapter);
        }


    }



}


