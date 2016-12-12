package com.wilddog.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.wilddog.R;
import com.wilddog.utils.EmojiUtil;
import com.wilddog.views.emoji.EmojiconTextView;

import java.util.List;


public class EmojiAdapter extends BaseAdapter {
	private List<String> data;
    private LayoutInflater inflater;

    private int size=0;

    public EmojiAdapter(Context context, List<String> list) {
        this.inflater=LayoutInflater.from(context);
        this.data=list;
        this.size=list.size();
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	String emoji=data.get(position);
        ViewHolder viewHolder=null;
        if(convertView == null) {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.item_face, null);
            viewHolder.iv_face=(EmojiconTextView)convertView.findViewById(R.id.item_iv_face);
            convertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        if(emoji.equals(EmojiUtil.EMOJI_DELETE_NAME)) {
            viewHolder.iv_face.setBackgroundResource(R.drawable.qb_tenpay_del);
        } else if(TextUtils.isEmpty(emoji)) {
            viewHolder.iv_face.setText(null);
        } else {
            viewHolder.iv_face.setTag(emoji);
            viewHolder.iv_face.setText(emoji);
        }

        return convertView;
    }

    class ViewHolder {

        public EmojiconTextView iv_face;
    }
}