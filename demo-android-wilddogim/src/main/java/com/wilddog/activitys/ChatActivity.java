package com.wilddog.activitys;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.adapters.ChatMsgListAdapter;
import com.wilddog.adapters.EmojiAdapter;
import com.wilddog.adapters.ViewPagerAdapter;
import com.wilddog.type.ChatType;
import com.wilddog.utils.AlertMessageUtil;
import com.wilddog.utils.CommonUtil;
import com.wilddog.utils.Constant;
import com.wilddog.utils.EmojiUtil;
import com.wilddog.utils.GenGroupPorpertyTool;
import com.wilddog.utils.GenerateConversationId;
import com.wilddog.utils.SharedPrefrenceTool;
import com.wilddog.view.customview.WildChatEditText;
import com.wilddog.wilddogim.Conversation;
import com.wilddog.wilddogim.ImageMessage;
import com.wilddog.wilddogim.MessageType;
import com.wilddog.wilddogim.TextMessage;
import com.wilddog.wilddogim.VoiceMessage;

import com.wilddog.wilddogim.WilddogIM;
import com.wilddog.wilddogim.common.callback.WilddogValueCallback;
import com.wilddog.wilddogim.common.error.WilddogIMError;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/6/28.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTV_Title;
    private String strUserID;

    private final String TAG = ChatActivity.class.getName();

    private final int MAX_PAGE_NUM = 20;
    private InputMethodManager inputKeyBoard;
    private List<com.wilddog.wilddogim.Message> wildMessageList = new ArrayList<>();
    private ChatMsgListAdapter adapter;
    private Button back;
    private String receiver;
    private String conversationType;
    private String strGroupID;
    private String strGroupName;
    private WilddogIM client;
    Conversation conversation;
    private Button addGroupMember;

    private ClipboardManager mClipBoard;
    private String mStrPhotoPath;

    private boolean mBNerverLoadMore = true;
    private boolean mBMore = true;
    private boolean mIsLoading = false;


    @Bind(R.id.btn_chat_back)
    Button btnChatBack;
    @Bind(R.id.ll_chat_back)
    LinearLayout llChatBack;

    @Bind(R.id.lv_msg_items)
    ListView lvMsgItems;
    @Bind(R.id.btn_media_pls)
    ImageButton btnMediaPls;
    @Bind(R.id.et_msg_input)
    WildChatEditText etMsgInput;
    @Bind(R.id.tv_voice_hold)
    TextView tvVoiceHold;
    @Bind(R.id.btn_send_msg)
    Button btnSendMsg;
    @Bind(R.id.btn_voice)
    Button btnVoice;
    @Bind(R.id.inputBar)
    LinearLayout inputBar;
    @Bind(R.id.vPagerEmoji)
    ViewPager vPagerEmoji;
    @Bind(R.id.ll_emojis)
    LinearLayout llEmojis;

    @Bind(R.id.btn_send_photo)
    Button btnSendPhoto;
    @Bind(R.id.btn_camera)
    Button btnCamera;

    LinearLayout videoChat;
    @Bind(R.id.ll_media)
    LinearLayout llMedia;
    @Bind(R.id.ll_more_container)
    LinearLayout llMoreContainer;
    @Bind(R.id.bar_bottom)
    LinearLayout barBottom;
    @Bind(R.id.root_layout)
    LinearLayout rootLayout;

    @Bind(R.id.ll_viewpager_dots)
    LinearLayout llViewPagerDots;

    // 表情小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;


    private int mLoadMsgNum = MAX_PAGE_NUM;

    private final static int FOR_SELECT_PHOTO = 1;
    private final static int FOR_START_CAMERA = 2;
    private final static int FOR_SELECT_FILE = 3;
    private final static int FOR_PHOTO_PREVIEW = 4;
    public final static int FOR_CHAT_TEXT_MENU = 5;
    public final static int FOR_CHAT_IMAGE_MENU = 6;
    public final static int FOR_GROUPINFO = 7;
    public final static int FOR_TAKE_VEDIO = 8;

    public final static int RESULT_CHAT_MENU_COPY = 1;
    public final static int RESULT_CHAT_MENU_DELETE = 2;
    public final static int RESULT_CHAT_MENU_RESEND = 3;
    public final static int RESULT_CHAT_MENU_SAVE = 4;

    public static boolean bFromOrgPic = false;
    private File mPttFile;
    private MediaRecorder mRecorder;
    private long mPttRecordTime;
    private String mStrPeerName;
    private int current = 0;


    private List<List<String>> emojis;
    private ArrayList<View> pageViews;
    private ArrayList<EmojiAdapter> emojiAdapters;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    wildMessageList.clear();
                    initHistoryData();
                    adapter.notifyDataSetChanged();
                    etMsgInput.setText("");
                    lvMsgItems.setSelection(wildMessageList.size() - 1);
                    break;
                case 2:
                    wildMessageList.clear();
                    initHistoryData();
                    adapter.notifyDataSetChanged();
                    lvMsgItems.setSelection(wildMessageList.size() - 1);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        onMyCreate();

    }

    private void onMyCreate() {
        Log.e("BroadCast", "onMyCreate CHATTYPE:" + getIntent().getStringExtra(Constant.CHATTYPE) + ",USER_ID:" + getIntent().getStringExtra(Constant.USER_ID));
        conversationType = getIntent().getStringExtra(Constant.CHATTYPE);
        ButterKnife.bind(this);
        Log.d("result", conversationType);
        addGroupMember = (Button) findViewById(R.id.btn_add_group_member);
        // 判断是群还是单聊
        if (ChatType.SINGLE_CHAT.equals(conversationType)) {
            strUserID = getIntent().getStringExtra(Constant.USER_ID);
            receiver = strUserID;
            addGroupMember.setVisibility(View.GONE);
        } else {
            strGroupID = getIntent().getStringExtra(Constant.USER_ID);
            SharedPrefrenceTool.setToID(this, strGroupID);
            receiver = strGroupID;
            addGroupMember.setVisibility(View.VISIBLE);
            addGroupMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowAlertDialog();
                }
            });
        }

        client = WilddogIM.newInstance();
        initConversation();
        initView();
        client.addMessageListener(listener);
        client.addGroupChangeListener(wilddogIMGroupChangeListener);
    }

    private WilddogIM.WilddogIMMessageListener listener = new WilddogIM.WilddogIMMessageListener() {
        @Override
        public void onNewMessage(List<com.wilddog.wilddogim.Message> messages) {
            for (com.wilddog.wilddogim.Message wildMessage : messages) {
                if (wildMessage.getConversation().getConversationId().equals(receiver)) {
                    clearUnreadCount();
                    Log.d("receiveMessage", messages.toString());
                    Message message = Message.obtain();
                    message.what = 2;
                    handler.sendMessage(message);
                }
            }
        }
    };

    private WilddogIM.WilddogIMGroupChangeListener wilddogIMGroupChangeListener = new WilddogIM.WilddogIMGroupChangeListener() {
        @Override
        public void memberJoined(String groupId, String owner, List<String> joinedUsers) {
            clearUnreadCount();
            wildMessageList.clear();
            initHistoryData();
            adapter.notifyDataSetChanged();
            lvMsgItems.setSelection(wildMessageList.size() - 1);
        }

        @Override
        public void memberQuit(String groupId, String quitUser) {
            clearUnreadCount();
            wildMessageList.clear();
            initHistoryData();
            adapter.notifyDataSetChanged();
            lvMsgItems.setSelection(wildMessageList.size() - 1);
        }

        @Override
        public void memberRemoved(String groupId, List<String> removeUsers) {
            clearUnreadCount();
            wildMessageList.clear();
            initHistoryData();
            adapter.notifyDataSetChanged();
            lvMsgItems.setSelection(wildMessageList.size() - 1);
        }
    };


    private void initConversation() {
        Log.d("result", receiver + conversationType);
        conversation = client.getConversation(receiver);
        if (conversation == null) {
            client.newConversation(Arrays.asList(strUserID), new WilddogIM.CompletionListener() {
                @Override
                public void onComplete(WilddogIMError error, Conversation wilddogConversation) {
                    if (error == null) {
                        conversation = wilddogConversation;
                    } else {
                        Log.d("result", "create conversationFailured"+error.toString());
                    }
                }
            });
        }
    }

    private void initView() {
        back = (Button) findViewById(R.id.btn_chat_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 设置标题显示
        mTV_Title = (TextView) findViewById(R.id.tv_titlebar_title);
        if (ChatType.SINGLE_CHAT.equals(conversationType)) {
            // 根据uid生成 对话者名字
            mTV_Title.setText(WilddogIMApplication.getFriendManager().getFriendInfoById(GenerateConversationId.getReceiver(strUserID)).getName());
        } else {
            // 根据群名称，获取群成员，生成群名
            mTV_Title.setText(GenGroupPorpertyTool.genGroupName(conversation.getMembers(), receiver));

        }
        // 消息显示listview
        lvMsgItems = (ListView) findViewById(R.id.lv_msg_items);
        initHistoryData();
        adapter = new ChatMsgListAdapter(ChatActivity.this, wildMessageList);
        lvMsgItems.setAdapter(adapter);
        if (wildMessageList.size() > 0) {
            lvMsgItems.setSelection(wildMessageList.size() - 1);
        }
        // 输入框和发送按钮 键盘
        btnSendMsg = (Button) findViewById(R.id.btn_send_msg);
        inputKeyBoard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        lvMsgItems.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInputKeyBoard();
                llMedia.setVisibility(View.GONE);
                llEmojis.setVisibility(View.GONE);
                etMsgInput.setVisibility(View.VISIBLE);
                tvVoiceHold.setVisibility(View.GONE);
                btnVoice.setBackgroundResource(R.drawable.aio_voice);
                if (!etMsgInput.getText().toString().isEmpty()) {
                    btnSendMsg.setVisibility(View.VISIBLE);
                    btnMediaPls.setVisibility(View.GONE);
                } else {
                    btnSendMsg.setVisibility(View.GONE);
                    btnMediaPls.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        lvMsgItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertMessageUtil.Showdialog("你确定要删除这条信息吗？", ChatActivity.this, new AlertMessageUtil.Listener() {
                    @Override
                    public void onok() {
                        wildMessageList.get(position).delete();
                        wildMessageList.remove(position);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void oncancel() {

                    }
                });
                return true;
            }
        });

        lvMsgItems.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        if (view.getFirstVisiblePosition() == 0 && !mIsLoading && mBMore) {
                            mBNerverLoadMore = false;
                            mLoadMsgNum += MAX_PAGE_NUM;
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }

        });

        btnMediaPls.setOnClickListener(this);

        btnSendMsg.setOnClickListener(this);

        btnVoice.setOnClickListener(this);

        llChatBack.setOnClickListener(this);


        //设置表情符号按钮位置
        Drawable drawable = getResources().getDrawable(R.drawable.aio_emoji);
        drawable.setBounds(0, 0, (int) CommonUtil.dp2px(this, 24), (int) CommonUtil.dp2px(this, 24));//第一0是距左边距离，第二0是距上边距离，40分别是长宽

        etMsgInput.setCompoundDrawables(null, null, drawable, null);//只放左边
        etMsgInput.setDrawableOnClickListener(new WildChatEditText.DrawableOnClickListener() {
            @Override
            public void drawableOnClick() {
                if (llEmojis.getVisibility() == View.GONE) {
                    llEmojis.setVisibility(View.VISIBLE);
                    hideInputKeyBoard();
                    llMedia.setVisibility(View.GONE);
                } else {
                    llEmojis.setVisibility(View.GONE);
                }
                etMsgInput.setVisibility(View.VISIBLE);

                if (etMsgInput.getText().toString().isEmpty()) {

                } else {
                    btnSendMsg.setVisibility(View.VISIBLE);
                }
            }
        });
        etMsgInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llMedia.setVisibility(View.GONE);
                }
            }
        });


        etMsgInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() <= 0) {
                    btnSendMsg.setVisibility(View.GONE);
                    btnMediaPls.setVisibility(View.VISIBLE);
                } else {

                    btnSendMsg.setVisibility(View.VISIBLE);
                    btnMediaPls.setVisibility(View.GONE);
                    llMedia.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        etMsgInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (llEmojis.getVisibility() == View.VISIBLE) {
                    llEmojis.setVisibility(View.GONE);
                }
                return false;
            }
        });

        btnSendPhoto.setOnClickListener(this);

        tvVoiceHold.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        tvVoiceHold.setText("松开发送");
                        startRecording();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!stopRecording()) {
                            return true;
                        }
                        sendFile(mPttFile.getAbsolutePath(), MessageType.VOICE);
                        break;
                }
                return false;
            }
        });

        mClipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        InitViewPager();
        initDots();

    }


    private void InitViewPager() {

        EmojiUtil.getInstace().initData();
        emojis = EmojiUtil.getInstace().mEmojiPageList;
        pageViews = new ArrayList<View>();

        View nullView = new View(getBaseContext());
        nullView.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView);

        emojiAdapters = new ArrayList<EmojiAdapter>();

        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(getBaseContext());
            EmojiAdapter adapter = new EmojiAdapter(getBaseContext(), emojis.get(i));
            view.setAdapter(adapter);
            emojiAdapters.add(adapter);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String strEmoji = (String) emojiAdapters.get(current).getItem(position);
                    if (strEmoji.equals(EmojiUtil.EMOJI_DELETE_NAME)) {
                        if (!TextUtils.isEmpty(etMsgInput.getText())) {
                            int selection = etMsgInput.getSelectionStart();
                            String strInputText = etMsgInput.getText().toString();
                            if (selection > 0) {
                                /*String strText = strInputText.substring(selection - 1);
                                if ("]".equals(strText)) {
                                    int start = strInputText.lastIndexOf("[");
                                    int end = selection;
                                    etMsgInput.getText().delete(start, end);
                                    return;
                                }
                                etMsgInput.getText().delete(selection - 1, selection);*/
                                backspace(etMsgInput);
                            }
                        }
                    } else {
                        SpannableString spannableString = EmojiUtil.getInstace().addEmoji(getBaseContext(), strEmoji);
                        if (spannableString == null) {
                            return;
                        }
                        etMsgInput.append(spannableString);
                    }
                }
            });
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setNumColumns(7);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            view.setLayoutParams(layoutParams);
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        View nullView2 = new View(getBaseContext());
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);


        vPagerEmoji.setAdapter(new ViewPagerAdapter(pageViews));
        vPagerEmoji.setCurrentItem(1);
        vPagerEmoji.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                Log.e("dots arg0", arg0 + "");
                setCurrentDot(arg0 - 1);
                current = arg0 - 1;
                if (arg0 == pageViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vPagerEmoji.setCurrentItem(arg0 + 1);
                    } else {
                        vPagerEmoji.setCurrentItem(arg0 - 1);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

        });
    }


    private void initDots() {

        dots = new ImageView[emojis.size()];

        // 循环取得小点图片
        for (int i = 0; i < emojis.size(); i++) {
            //dots[i] = (ImageView) llViewPagerDots.getChildAt(i);
            dots[i] = new ImageView(this);
            dots[i].setImageResource(R.drawable.viewpager_dots);
            dots[i].setEnabled(true);// 都设为灰色
            dots[i].setPadding(5, 5, 5, 5);
            llViewPagerDots.addView(dots[i], i);
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态
    }

    private void setCurrentDot(int position) {
        if (position < 0 || position > emojis.size() - 1
                || currentIndex == position) {
            return;
        }

        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = position;
    }


    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    //录音相关
    private void startRecording() {

        try {
            File file = new File("record_temp.mp3");
            if (file.exists()) {
                file.delete();
            }

            mPttFile = File.createTempFile("record_temp", ".mp3");
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // 设置压缩格式 AMR 更小，上传更快，并且可以满足用户需求
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mRecorder.setOutputFile(mPttFile.getAbsolutePath());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mRecorder.setPreviewDisplay(null);

                mRecorder.prepare();
            }

            mPttRecordTime = System.currentTimeMillis();
            mRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    stopRecording();
                    Toast.makeText(getBaseContext(), "录音发生错误:" + what, Toast.LENGTH_SHORT).show();
                }
            });
            mRecorder.start();

        } catch (IOException e) {
            Log.e(TAG, "start record error" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "start record error2" + e.getMessage());
            e.printStackTrace();
        }

    }

    //停止录音
    private boolean stopRecording() {
        if (mRecorder != null) {
            tvVoiceHold.setText("按住说话");
            mRecorder.setOnErrorListener(null);

            try {
                mRecorder.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "stop Record error:" + e.getMessage());
                mRecorder.release();
                mRecorder = null;
                return false;
            } catch (Exception e) {
                Log.e(TAG, "stop Record Exception:" + e.getMessage());
                mRecorder.release();
                mRecorder = null;
                return false;
            }
            mRecorder.release();
            mRecorder = null;
        }
        mPttRecordTime = System.currentTimeMillis() - mPttRecordTime;
        mPttRecordTime = mPttRecordTime / 1000;//换算为秒
        return true;

    }

    //    调用系统相机
    private void startCamera() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            try {
                File mPhotoFile = new File(Constant.IMAG_DIR);
                if (!mPhotoFile.exists()) {
                    mPhotoFile.mkdirs();
                }

                mStrPhotoPath = Constant.IMAG_DIR + getFileNameByDate() + ".jpg";
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mStrPhotoPath)));
                startActivityForResult(intent, FOR_START_CAMERA);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "启动失败：" + e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "sd卡不存在", Toast.LENGTH_LONG).show();
        }
    }


    //根据日期生成相片文件名称
    private String getFileNameByDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("_yyyyMMdd_HHmmss");
        return dateFormat.format(date);
    }

    //
    private void prepareSendVoiceMessage() {
        /*btnVoice.setVisibility(View.GONE);
        btnMediaPls.setImageResource(R.drawable.aio_keyboard);*/
        btnVoice.setBackgroundResource(R.drawable.aio_keyboard);
        this.etMsgInput.setVisibility(View.GONE);
        this.tvVoiceHold.setVisibility(View.VISIBLE);
    }


    private void sendTextMessage() {
        if (TextUtils.isEmpty(etMsgInput.getText())) {
            return;
        }
        com.wilddog.wilddogim.TextMessage message = com.wilddog.wilddogim.Message.newMessage(etMsgInput.getText().toString().trim());

        conversation.sendMessage(message, new WilddogValueCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "发送成功");
                clearUnreadCount();
                Message handlerMessage = Message.obtain();
                handlerMessage.what = 1;
                handler.sendMessage(handlerMessage);
            }

            @Override
            public void onFailed(int code, String des) {
                Log.d("result", des);

            }
        });
        clearUnreadCount();
        Message handlerMessage = Message.obtain();
        handlerMessage.what = 1;
        handler.sendMessage(handlerMessage);

    }


    // 初始化过去数据

    public void initHistoryData() {
        com.wilddog.wilddogim.Message lastMessage = conversation.getLastMessage();
        if (lastMessage != null) {
            List<com.wilddog.wilddogim.Message> list = conversation.getMessagesFromLast(null, 20);

            if (list.size() > 0) {
                Collections.reverse(list);
                if (wildMessageList.size() > 0) {
                    wildMessageList.clear();
                }
                wildMessageList.addAll(list);
            }
            // 将未读数清空
            clearUnreadCount();
        }
    }

    public void clearUnreadCount() {
        conversation.markAllMessagesAsRead();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPrefrenceTool.setToID(ChatActivity.this, null);
        current = 0;
        client.removeMessageListener(listener);
        client.removeGroupChangeListener(wilddogIMGroupChangeListener);
    }

    //隐藏键盘
    private void hideInputKeyBoard() {

        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                inputKeyBoard.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }


    private void ShowAlertDialog() {
        final Dialog dialog = new Dialog(ChatActivity.this);
        View view = View.inflate(ChatActivity.this, R.layout.dialog_group_operate_set, null);
        Button add = (Button) view.findViewById(R.id.btn_add);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        final Button remove = (Button) view.findViewById(R.id.btn_remove);
        Button exit = (Button) view.findViewById(R.id.btn_exit);
        if (conversation.equals(SharedPrefrenceTool.getUserID(ChatActivity.this))) {
            remove.setVisibility(View.VISIBLE);
        } else {
            remove.setVisibility(View.GONE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, AddMemberActivity.class);
                intent.putExtra(Constant.GROUPID, strGroupID);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, RemoveMemberActivity.class);
                intent.putExtra(Constant.GROUPID, strGroupID);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 移除自己
                List<String> removeList = new ArrayList<String>();
                removeList.add(client.getCurrentUser().getUid());
                conversation.removeMember(removeList);
                conversation.delete();
                finish();
            }
        });

        dialog.setContentView(view);
        dialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llEmojis.getVisibility() == View.VISIBLE || llMedia.getVisibility() == View.VISIBLE) {
                llEmojis.setVisibility(View.GONE);
                llMedia.setVisibility(View.GONE);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    //选择图片
    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, FOR_SELECT_PHOTO);
    }

    private String getPicPathFromData(Intent data) {
        Uri uri = data.getData();
        if (uri == null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));

        }
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            //	cursor = resolver.query(originalUri, proj, null, null, null);
            cursor = resolver.query(uri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            return path;
        } catch (Exception e) {
            Log.e(TAG, "FOR_SELECT_PHOTO Exception:" + e.toString());
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void sendFile(String path, int type) {
//    todo    检查网络
        if (path.length() == 0) {
            return;
        }
//        读取数据
        File file = new File(path);
        if (file.length() == 0) {
            return;
        }

        byte[] fileData = new byte[(int) file.length()];
        DataInputStream dis = null;

        try {
            dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {

        }
        ;

        try {
            if (type == MessageType.IMAGE) {
                ImageMessage imageMessage = new ImageMessage(MessageType.IMAGE, path);
                conversation.sendMessage(imageMessage, new WilddogValueCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        clearUnreadCount();
                        Message handlerMessage = Message.obtain();
                        handlerMessage.what = 1;
                        handler.sendMessage(handlerMessage);
                    }

                    @Override
                    public void onFailed(int code, String des) {
                        final int errorCode = code;
                        final String errorMsg = des;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getBaseContext(), "发送消息失败. code: " + errorCode + " errmsg: " + errorMsg, Toast.LENGTH_SHORT).show();
                                clearUnreadCount();
                                Message handlerMessage = Message.obtain();
                                handlerMessage.what = 1;
                                handler.sendMessage(handlerMessage);
                            }
                        });
                    }
                });


            } else if (type == MessageType.VOICE) {
                VoiceMessage voiceMessage = new VoiceMessage((int) mPttRecordTime, fileData);
                conversation.sendMessage(voiceMessage, new WilddogValueCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        clearUnreadCount();
                        Message handlerMessage = Message.obtain();
                        handlerMessage.what = 1;
                        handler.sendMessage(handlerMessage);
                    }

                    @Override
                    public void onFailed(int code, String des) {
                        final int errorCode = code;
                        final String errorMsg = des;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getBaseContext(), "发送消息失败. code: " + errorCode + " errmsg: " + errorMsg, Toast.LENGTH_SHORT).show();
                                clearUnreadCount();
                                Message handlerMessage = Message.obtain();
                                handlerMessage.what = 1;
                                handler.sendMessage(handlerMessage);
                            }
                        });
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        clearUnreadCount();
        Message handlerMessage = Message.obtain();
        handlerMessage.what = 1;
        handler.sendMessage(handlerMessage);
        //getMessagesFromLast();
    }

    public void onBack(View view) {
        finish();
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        switch (viewId) {

            case R.id.btn_media_pls://多媒体
                showMediaPls();
                break;
            case R.id.btn_send_msg://发送消息
                sendTextMessage();

                break;
            case R.id.btn_voice://发送语音
                hideInputKeyBoard();
//                按住说话按键如果不可见
                if (tvVoiceHold.getVisibility() != View.VISIBLE) {
                    prepareSendVoiceMessage();

                } else {
                    btnVoice.setBackgroundResource(R.drawable.aio_voice);
                    tvVoiceHold.setVisibility(View.GONE);
                    etMsgInput.setVisibility(View.VISIBLE);

                }
                break;
            case R.id.btn_send_photo://发送图片
                selectPhoto();
                break;
            case R.id.btn_camera://发送照片
                startCamera();
                break;
            case R.id.ll_chat_back://返回
                finish();
                break;


        }
    }

    private void showMediaPls() {
        hideInputKeyBoard();
        llEmojis.setVisibility(View.GONE);
        etMsgInput.setVisibility(View.VISIBLE);
        etMsgInput.clearFocus();
        tvVoiceHold.setVisibility(View.GONE);
        //多媒体显示
        if (llMedia.getVisibility() == View.GONE) {
            llMedia.setVisibility(View.VISIBLE);
            btnVoice.setBackgroundResource(R.drawable.aio_voice);
        } else {
            llMedia.setVisibility(View.GONE);
        }
        if (!etMsgInput.getText().toString().isEmpty()) {
            btnSendMsg.setVisibility(View.VISIBLE);
            btnVoice.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            相片处理相关
            if (requestCode == FOR_START_CAMERA) {
                if (mStrPhotoPath == null || mStrPhotoPath.length() == 0) {
                    Log.e(TAG, "mStrPhotoPath null");
                    return;
                }
                File file = new File(mStrPhotoPath);
                if (file == null || !file.exists()) {
                    Log.e(TAG, "mStrPhotoPath file not exists");
                    return;
                }
                Intent intent = new Intent(ChatActivity.this, PhotoPreviewActivity.class);
                intent.putExtra("photo_url", mStrPhotoPath);
                startActivityForResult(intent, FOR_PHOTO_PREVIEW);


            } else if (requestCode == FOR_PHOTO_PREVIEW) {
                if (data != null) {
                    boolean bOrg = data.getBooleanExtra("pic_org", false);
                    String filePath = data.getStringExtra("filePath");
                    Log.d(TAG, "pic org:" + bOrg + ":" + filePath);
                    if (filePath == null) {
                        return;
                    }
                        /*if(bOrg){
                            mPicLevel = 0;
                        }*/

                    sendFile(filePath, MessageType.IMAGE);
                }
            } else if (requestCode == FOR_SELECT_PHOTO) {
                String filePath = getPicPathFromData(data);
                if (filePath != null) {
                    Intent intent = new Intent(ChatActivity.this, PhotoPreviewActivity.class);
                    Log.e(TAG, filePath);
                    intent.putExtra("photo_url", filePath);
                    startActivityForResult(intent, FOR_PHOTO_PREVIEW);
                }


            }


            //复制、删除、重发等
            if (requestCode == FOR_CHAT_TEXT_MENU) {
                if (data == null) {
                    return;
                }
                final int pos = data.getIntExtra("items", -1);
                TextMessage entity = (TextMessage) wildMessageList.get(pos);
                if (entity == null) {
                    return;
                }

                switch (resultCode) {
                    /*case RESULT_CHAT_MENU_COPY:
                        WildTextElem textElem = (WildTextElem) entity.getElem();
                        if (textElem != null) {
                            mClipBoard.setPrimaryClip(ClipData.newPlainText("", textElem.getText()));
                            mChatMsgListAdapter.notifyDataSetChanged();
                        }
                        break;*/
                   /* case RESULT_CHAT_MENU_DELETE:
                        WildMessage message = entity.getMessage();
                        if (message.REMOVE()) {
                            recentEntityList.REMOVE(pos);
                        } else {

                        }
                        mChatMsgListAdapter.notifyDataSetChanged();
                        lvMsgItems.requestFocusFromTouch();
                        lvMsgItems.setSelection(pos - 1);
                        break;*/
                    case RESULT_CHAT_MENU_RESEND:

                        conversation.sendMessage(entity, new WilddogValueCallback<String>() {
                            @Override
                            public void onSuccess(String o) {

                            }

                            @Override
                            public void onFailed(int code, String des) {

                            }
                        });
                        break;


                }
            } else if (requestCode == FOR_CHAT_IMAGE_MENU) {
                if (data == null) {
                    return;
                }
                final int pos = data.getIntExtra("items", -1);
                com.wilddog.wilddogim.Message entity = wildMessageList.get(pos);
                switch (resultCode) {
                   /* case RESULT_CHAT_MENU_COPY:
                        return;
                    case RESULT_CHAT_MENU_DELETE:
                        WildMessage message = entity.getMessage();
                        if (message.REMOVE()) {
                            recentEntityList.REMOVE(pos);
                        } else {

                        }
                        mChatMsgListAdapter.notifyDataSetChanged();
                        lvMsgItems.requestFocusFromTouch();
                        lvMsgItems.setSelection(pos - 1);
                        break;*/
                    case RESULT_CHAT_MENU_RESEND:

                        conversation.sendMessage(entity, new WilddogValueCallback<String>() {
                            @Override
                            public void onSuccess(String o) {

                            }

                            @Override
                            public void onFailed(int code, String des) {

                            }
                        });
                        break;

                }
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ChatMsgListAdapter.mPlayer != null) {
            ChatMsgListAdapter.mPlayer.stop();
            ChatMsgListAdapter.mPlayer.release();
            ChatMsgListAdapter.mPlayer = null;
        }
    }


}
