package com.wilddog.utils;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wilddog.WilddogIMApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/1/21.
 */
public class Volleyutil {
    public static final String TAG = Volleyutil.class.getName();
    private static JSONObject JsonObject;

    public static void GET(String path, final Listener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonObject = jsonObject;
                LogUtil.d(TAG, JsonObject.toString());
                try {
                    String responcecode = JsonObject.getString("code");
                    if (responcecode.equals("0")) {
                        if (listener != null) {
                            listener.onsuccess(JsonObject);
                        }
                    } else {
                        String msg = JsonObject.getString("msg");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(listener!=null){
                    try {
                        listener.onfailure(new JSONObject(volleyError.getMessage().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                volleyError.printStackTrace();
            }
        });
        WilddogIMApplication.getRequestQueue().add(jsonObjectRequest);
    }
    public static void CONNECTGET(String path, final Listener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonObject = jsonObject;
                LogUtil.d(TAG, JsonObject.toString());
                try {
                        if (listener != null) {
                            listener.onsuccess(JsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(listener!=null){
                    try {
                        listener.onfailure(new JSONObject(volleyError.getMessage().toString()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                volleyError.printStackTrace();
            }
        });
        WilddogIMApplication.getRequestQueue().add(jsonObjectRequest);
    }

    public static void UIDGET(String path, final Listener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonObject = jsonObject;
                LogUtil.d(TAG, JsonObject.toString());
                try {
                    if (listener != null) {
                        listener.onsuccess(JsonObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(listener!=null){
                    String message=volleyError.getMessage().toString();
                    Log.e("error",message);
                    listener.onfailure(new JSONObject());
                }
                volleyError.printStackTrace();
            }
        });
        WilddogIMApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public static void WXGET(String path, final Listener listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, path, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonObject = jsonObject;
                LogUtil.d(TAG, JsonObject.toString());
                try {

                    if (!JsonObject.toString().contains("errcode") || "0".equals(JsonObject.getString("errcode"))) {
                        if (listener != null) {
                            listener.onsuccess(JsonObject);
                        }
                    } else {
                        String msg = JsonObject.getString("errmsg");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        WilddogIMApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public static void POST(String path, Map params, final Listener listener) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, path, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JsonObject = jsonObject;
                LogUtil.d(TAG, JsonObject.toString());
                try {
                    String responcecode = JsonObject.getString("code");
                    if (responcecode.equals("0")) {
                        if (listener != null) {
                            listener.onsuccess(JsonObject);
                        }
                    } else {
                        if (listener != null) {
                            listener.onfailure(JsonObject);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        WilddogIMApplication.getRequestQueue().add(jsonObjectRequest);
    }


    public interface Listener {
        public abstract void onsuccess(JSONObject jsonObject);

        public abstract void onfailure(JSONObject jsonObject);
    }
}
