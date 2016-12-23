package com.wilddog.utils;

import android.util.Log;


import com.wilddog.client.SyncError;
import com.wilddog.wilddogim.common.callback.WilddogValueCallback;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadData {

    public DownLoadData() {
        // TODO Auto-generated constructor stub
    }
   // OkHttp的execute的方法是同步方法，
    //OkHttp的enqueue的方法是异步方法，
    public static void getData(final String path, final WilddogValueCallback<byte[]> callback) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(path)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call1, IOException e) {
                Log.d("获取音频失败", e.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int code = response.code();
                if (code == 200) {
                    InputStream is = response.body().byteStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    byte[] data = outStream.toByteArray();//网页的二进制数据
                    outStream.flush();
                    outStream.close();
                    is.close();

                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                } else {
                    if (callback != null) {
                        callback.onFailed(code, " getting data occurred error");
                    }
                }
            }
        });
    }

    public static void getImageData(final String path, final WilddogValueCallback<byte[]> callback) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(path)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call,Response response) {
                InputStream is = null;
                try {
                    is = response.body().byteStream();
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        outStream.write(buffer, 0, len);
                    }
                    byte[] data = outStream.toByteArray();//网页的二进制数据
                    outStream.flush();
                    outStream.close();
                    if (callback != null) {
                        callback.onSuccess(data);
                    }
                } catch (Exception e) {
                    callback.onFailed(new SyncError(111,e.toString(),"exception"));

                } finally {
                    if (is != null) try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
