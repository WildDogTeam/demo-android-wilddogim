package com.wilddog.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;


/**
 * Created by Administrator on 2015/12/29.
 */
public class AlertMessageUtil {
    public static Toast toast;
    public static AlertDialog.Builder dialog;
    public static ProgressDialog progressDialog;
    private static TextView tv;

    public static void showShortToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(WilddogIMApplication.getContext(), content, Toast.LENGTH_SHORT);
            View view = View.inflate(WilddogIMApplication.getContext(), R.layout.toast_bg, null);
            toast.setView(view);
            tv = (TextView) view.findViewById(R.id.toast_content);
            tv.setText(content);
        } else {
            tv.setText(content);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 66);
        toast.show();
    }

    public static void showLongToast(String content) {
        if (toast == null) {
            toast = Toast.makeText(WilddogIMApplication.getContext(), content, Toast.LENGTH_LONG);
            View view = View.inflate(WilddogIMApplication.getContext(), R.layout.toast_bg, null);
            toast.setView(view);
            tv = (TextView) view.findViewById(R.id.toast_content);
            tv.setText(content);
        } else {
            tv.setText(content);
        }
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 66);
        toast.show();
    }

    public static void Showdialog(String title,Context context ,  final Listener listener) {

        dialog = new AlertDialog.Builder(context);

        dialog.setTitle("你确定吗");
        dialog.setMessage(title);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.onok();
                }
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    listener.oncancel();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public interface Listener {
        void onok();

        void oncancel();
    }

    public static void showprogressbar(String title, Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage("载入中");
        progressDialog.show();
    }

    public static void dismissprogressbar() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
