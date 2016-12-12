package com.wilddog.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.wilddog.R;
import com.wilddog.model.FriendInfo;

import java.util.HashMap;
import java.util.List;


public class AddGroupMemberAdapter extends BaseAdapter {

    private static final String TAG = AddGroupMemberAdapter.class.getSimpleName();
    private List<FriendInfo> listUser;
    private static HashMap<Integer, Boolean> isSelected;
    private LayoutInflater inflater;

    public AddGroupMemberAdapter(Context context, List<FriendInfo> list) {

        this.listUser = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static void setIsSelected(int listSize) {
        isSelected.clear();
        for (int i = 0; i < listSize; i++) {
            getIsSelected().put(i, false);
        }
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listUser.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listUser.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.create_group_contact_item, null);
            holder = new ViewHolder();
            holder.username = (TextView) convertView.findViewById(R.id.tv_username);
            holder.bselect = (CheckBox) convertView.findViewById(R.id.cb_user);
            holder.avatar = (ImageView)convertView.findViewById(R.id.img_avatar) ;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String entity = listUser.get(position).getName();
        holder.username.setText(entity);
        if (getIsSelected() != null && getIsSelected().get(position) != null) {
            boolean result = getIsSelected().get(position);
            holder.bselect.setChecked(result);
        } else {
            holder.bselect.setChecked(false);
        }
        ImageLoader.getInstance().displayImage(listUser.get(position).getAvatar(),holder.avatar);
        return convertView;
    }


    public static class ViewHolder {
        public TextView username;
        public CheckBox bselect;
        ImageView avatar;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        AddGroupMemberAdapter.isSelected = isSelected;
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }
}


