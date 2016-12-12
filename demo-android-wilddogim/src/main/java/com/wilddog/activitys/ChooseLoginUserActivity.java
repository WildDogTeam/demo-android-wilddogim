package com.wilddog.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wilddog.R;
import com.wilddog.utils.Constant;
import com.wilddog.utils.Volleyutil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseLoginUserActivity extends AppCompatActivity {
    ListView listView ;
    List<String> users=new ArrayList<>();
    ArrayAdapter adapter;
    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(users.size()!=0){
                adapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_user);
        listView= (ListView) findViewById(R.id.lv_uid);
        adapter=new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra(Constant.CURRENT_USER,users.get(position));
                setResult(1,intent);
                finish();
            }
        });
        getUid();
    }

    private void getUid() {
        String path = Constant.GET_OFFLINE_USER;
        Volleyutil.UIDGET(path, new Volleyutil.Listener() {
            @Override
            public void onsuccess(JSONObject jsonObject) {

                try {
                    JSONArray array = jsonObject.getJSONArray("offLineUids");
                    for(int i=0;i<array.length();i++){
                       users.add(array.get(i).toString());
                    }
                    handler.sendEmptyMessage(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onfailure(JSONObject jsonObject) {
                Log.d("result",jsonObject.toString());
            }
        });
    }


}
