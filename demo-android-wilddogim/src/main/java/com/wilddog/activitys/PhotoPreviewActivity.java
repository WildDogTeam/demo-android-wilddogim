package com.wilddog.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.wilddog.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PhotoPreviewActivity extends BaseActivity {
    private final static String TAG = PhotoPreviewActivity.class.getSimpleName();
    CheckBox checkBox;
    TextView tvSize;
    @Bind(R.id.btn_chat_back)
    Button btnChatBack;
    @Bind(R.id.ll_chat_back)
    LinearLayout llChatBack;
    @Bind(R.id.tv_titlebar_title)
    TextView tvTitlebarTitle;

    private boolean bOrg = false;
    private ImageView ivPic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_preview);
        ButterKnife.bind(this);
        tvTitlebarTitle.setText("预览");
        initView();
    }

    public void initView() {
        Log.d(TAG, "柴华");
        final String filePath = getIntent().getStringExtra("photo_url");
        Log.d(TAG, "柴华" + filePath);
        if (filePath != null) {
            Bitmap bitmap = GetRightOritationNew(filePath);
            Log.d(TAG, bitmap.toString());
            ivPic = (ImageView) findViewById(R.id.iv_pic);
            ivPic.setImageBitmap(bitmap);
        }
        checkBox = (CheckBox) findViewById(R.id.cb_pic_org);
        tvSize = (TextView) findViewById(R.id.tv_size);
        tvSize.setVisibility(View.GONE);
        checkBox.setChecked(false);
        Button btnSend = (Button) findViewById(R.id.btn_photo_send);

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    tvSize.setVisibility(View.VISIBLE);
                    tvSize.setText("(" + getFileSize(filePath) + ")");
                    bOrg = true;
                } else {
                    bOrg = false;
                    tvSize.setVisibility(View.GONE);
                }
            }

        });
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("pic_org", bOrg);
                resultIntent.putExtra("filePath", filePath);
                Log.d(TAG, "btnSend:" + bOrg + ":" + filePath);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

        });

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        if (reqWidth == 0 || reqHeight == 0) {
            return 1;
        }

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "origin, w= " + width + " h=" + height);
        Log.d(TAG, "request, w= " + reqWidth + " h=" + reqHeight);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        if (inSampleSize == 1 && (height > 4096 || width > 4096)) {
            inSampleSize *= 2;
        }
        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }

    private DisplayMetrics metrics = new DisplayMetrics();

    public Bitmap GetRightOritationNew(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        options.inSampleSize = calculateInSampleSize(options, metrics.widthPixels, metrics.heightPixels);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            exif = null;
            return bitmap;
        }
        int degree = 0;
        if (exif != null) {
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        }
        if (degree != 0) {
            Log.d(TAG, "degree:" + degree);
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        Log.d(TAG, "degree:" + degree);
        return bitmap;
    }


    private static String getFileSize(String filePath) {
        long size = 0;
        try {
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                Log.e(TAG, "获取文件大小,文件不存在!");
            }

        } catch (Exception e) {
            Log.e(TAG, "getFileSize" + e.toString());
        }
        return FormetFileSize(size);
    }


    private static String FormetFileSize(long size) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (size == 0) {
            return wrongSize;
        }
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "K";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public void onBack(View view) {
        finish();
    }

}
