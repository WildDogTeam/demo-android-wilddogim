package com.wilddog.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.wilddog.R;
import com.wilddog.model.FriendInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/4/13.
 */
public class ContactsAdapter extends BaseAdapter {
    private Context context;
    private List<FriendInfo> list;

    public ContactsAdapter(Context context, List<FriendInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.contacts_item, null);
        TextView tvcategray = (TextView) view.findViewById(R.id.tv_contacts_name);
        ImageView iv = (ImageView) view.findViewById(R.id.img_avatar);
        FriendInfo friendInfo = list.get(position);
        tvcategray.setText(friendInfo.getName());
        ImageLoader.getInstance().displayImage(friendInfo.getAvatar(),iv);
        return view;
    }
}
