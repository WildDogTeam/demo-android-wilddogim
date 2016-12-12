package com.wilddog.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.wilddog.R;
import com.wilddog.WilddogIMApplication;
import com.wilddog.activitys.ChatActivity;
import com.wilddog.activitys.ChatMenuActivity;
import com.wilddog.model.FriendInfo;
import com.wilddog.model.UserInfo;
import com.wilddog.utils.DateHelper;
import com.wilddog.utils.DownLoadData;
import com.wilddog.utils.EnlargePictureTool;
import com.wilddog.utils.FriendsManamger;
import com.wilddog.utils.SharedPrefrenceTool;
import com.wilddog.wilddogim.WilddogIMClient;

import com.wilddog.wilddogim.core.group.GroupMemberOptionType;
import com.wilddog.wilddogim.core.wildcallback.WildValueCallBack;
import com.wilddog.wilddogim.message.GroupTipMessage;
import com.wilddog.wilddogim.message.ImageMessage;
import com.wilddog.wilddogim.message.Message;
import com.wilddog.wilddogim.message.MessageStatus;
import com.wilddog.wilddogim.message.MessageType;
import com.wilddog.wilddogim.message.TextMessage;
import com.wilddog.wilddogim.message.VoiceMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * Created by Charles on 2016/4/14.
 * 聊天列表适配器
 */
public class ChatMsgListAdapter extends BaseAdapter {
    //    日志tag
    private static String TAG = ChatMsgListAdapter.class.getSimpleName();
    private static final int ITEMCOUNT = 11;
    public static final int TYPE_TEXT_SEND = 0;
    public static final int TYPE_TEXT_RECV = 1;
    public static final int TYPE_IMAGE_SEND = 2;
    public static final int TYPE_IMAGE_RECV = 3;
    public static final int TYPE_FILE_SEND = 4;
    public static final int TYPE_FILE_RECV = 5;
    public static final int TYPE_SOUND_SEND = 6;
    public static final int TYPE_SOUND_RECV = 7;
    public static final int TYPE_GROUP_TIPS = 8;
    public static final int TYPE_VIDEO_SEND = 9;
    public static final int TYPE_VIDEO_RECV = 10;

    private List<Message> chatMsgLst = null;
    private LayoutInflater mInflater;

    private Context mContext;
    private Activity mActivity;
    private final String mUserName;

    private ProgressDialog progressDialog;
    private boolean mIsVoicePlaying = false;
    private ImageView currentPlayingIV;
    private AnimationDrawable currentAnimation;
    public static MediaPlayer mPlayer = null;
    private int playPos = 0;


    public ChatMsgListAdapter(Context context, List<Message> chatMsgLst) {
        this.chatMsgLst = chatMsgLst;
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mInflater = LayoutInflater.from(context);
        mUserName = WilddogIMClient.getCurrentUser().getUid();
    }

    @Override
    public int getCount() {
        if (chatMsgLst != null) {

            return chatMsgLst.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (chatMsgLst != null) return chatMsgLst.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //判断Item布局类型
    @Override
    public int getItemViewType(int position) {
        Message entity = chatMsgLst.get(position);
       if(entity.getMessageType()== MessageType.TEXT) {
           return mUserName.equals(entity.getSender()) ? TYPE_TEXT_SEND : TYPE_TEXT_RECV;
       }else  if(entity.getMessageType()== MessageType.IMAGE){
           return mUserName.equals(entity.getSender()) ? TYPE_IMAGE_SEND : TYPE_IMAGE_RECV;
       }else  if(entity.getMessageType()== MessageType.VOICE){
           return mUserName.equals(entity.getSender()) ? TYPE_SOUND_SEND : TYPE_SOUND_RECV;

       }else if(entity.getMessageType()== MessageType.GROUPTIP){
           return TYPE_GROUP_TIPS;
       }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message entity = chatMsgLst.get(position);


        ViewHolder holder = null;
        if (convertView == null) {
            convertView = createViewByType(position);
            holder = new ViewHolder();
            holder.tv_sendtime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            holder.pb_status = (ProgressBar) convertView.findViewById(R.id.pb_status);
            if (mUserName.equals(entity.getSender())) {
                holder.iv_msg_status = (ImageView) convertView.findViewById(R.id.iv_msg_status);
            }
            holder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_chatcontent);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

          //设置消息未发送状态
        if (holder.iv_msg_status != null && entity.status().equals(MessageStatus.FAILED) ) {
            holder.iv_msg_status.setVisibility(View.VISIBLE);
        } else if (holder.iv_msg_status != null) {
            holder.iv_msg_status.setVisibility(View.GONE);
        }

        if (mUserName.equals(entity.getSender())) {
            // 头像和名字
            UserInfo info = SharedPrefrenceTool.readUserInfo(mContext);
            holder.tv_username.setText(info.getUserName());
            ImageLoader.getInstance().displayImage(info.getAvatar(),holder.iv_avatar);
        } else {
            // 头像和名字
            FriendInfo info = WilddogIMApplication.getFriendManager().getFriendInfoById(entity.getSender());
            if(info!=null){
            holder.tv_username.setText(info.getName());
            ImageLoader.getInstance().displayImage(info.getAvatar(),holder.iv_avatar);
            }
        }
        if(entity.getMessageType()!=MessageType.GROUPTIP){
        if (entity.getMessageType()==MessageType.TEXT) {
            holder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        } else if (entity.getMessageType()==MessageType.IMAGE) {
            holder.iv_chat_item_content = (ImageView) convertView.findViewById(R.id.iv_chat_item_content);
            holder.rl_pic_new_content = (RelativeLayout) convertView.findViewById(R.id.rl_pic_new_content);
        } else if (entity.getMessageType()==MessageType.VOICE) {
            holder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_total_time);
            holder.iv_chat_item_content = (ImageView) convertView.findViewById(R.id.iv_chat_item_content);
            holder.rl_chat_item_content = (RelativeLayout) convertView.findViewById(R.id.rl_chat_item_content);
        }
        }else {
            holder.tv_chatcontent = (TextView) convertView.findViewById(R.id.tv_msg_content);
        }
        if (entity.getMessageType()==MessageType.TEXT) {
            DisplayTextMsg(entity, mUserName.equals(entity.getSender()), holder, position, entity.status());
        } else if (entity.getMessageType()==MessageType.IMAGE) {
            //防止图片错位 TODO 没有url
            //立即发送
            ImageMessage imageMessage=(ImageMessage) entity;
                DisplayPicMsg(imageMessage, mUserName.equals(entity.getSender()), holder, position, entity.status());

        } else if (entity.getMessageType()==MessageType.VOICE) {
            VoiceMessage imageMessage = (VoiceMessage) entity;
            DisplayPttMsg(imageMessage,mUserName.equals(entity.getSender()), holder, position, entity.status());
        }else  if(entity.getMessageType()==MessageType.GROUPTIP){
            GroupTipMessage groupTipMessage=(GroupTipMessage)entity;
            DisplayGrpTips(groupTipMessage, holder, position);
        }


        //判断何时显示发送日期
        if (position == 0) {
            Log.d("time", entity.getSendAt()+":"+DateHelper.GetStringFormat(entity.getSendAt())+":");
            holder.tv_sendtime.setText(DateHelper.GetStringFormat(entity.getSendAt()));
        } else {
            Log.d("time", entity.getSendAt()+":"+DateHelper.GetStringFormat(entity.getSendAt())+":");
            if (DateHelper.LongInterval(entity.getSendAt(), chatMsgLst.get(position - 1).getSendAt())) {
                holder.tv_sendtime.setText(DateHelper.GetStringFormat((entity.getSendAt())));
                holder.tv_sendtime.setVisibility(View.VISIBLE);
            } else {
                holder.tv_sendtime.setVisibility(View.GONE);

            }
        }


        return convertView;
    }

    private void DisplayTextMsg(final Message message, boolean isSelf, ViewHolder holder, final int position, final String status) {
        holder.tv_chatcontent.setText(((TextMessage) message).getText());

        if (isSelf) {
            if (status.equals(MessageStatus.SENDING) ) {
                holder.pb_status.setVisibility(View.VISIBLE);
                return;
            }else {
                holder.pb_status.setVisibility(View.GONE);
            }
        }

        holder.tv_chatcontent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                todo 信息重发
                Intent intent = new Intent(mActivity, ChatMenuActivity.class);
                intent.putExtra("item", position);
                intent.putExtra("type", ChatActivity.FOR_CHAT_TEXT_MENU);
                if (status ==MessageStatus.FAILED) {
                    intent.putExtra("needReSend", true);
                } else {
                    intent.putExtra("needReSend", false);
                }
                mActivity.startActivityForResult(intent, ChatActivity.FOR_CHAT_TEXT_MENU);
                Log.d(TAG, "ptt menu,put item:" + position);

                return true;
            }
        });

    }

     private void DisplayPttMsg(final VoiceMessage elem, boolean isSelf, ViewHolder holder, final int position, final String status) {
         final boolean bIsSelf = isSelf;
         final int tempPosition = position;
 //设置显示语音信息时长
         holder.tv_chatcontent.setText(elem.getDuration() + "'");

         final ImageView im = holder.iv_chat_item_content;

         //当前正在播放
         if (position == playPos && mIsVoicePlaying) {

             currentPlayingIV = im;
             currentAnimation = null;

             @DrawableRes int animationResId;
             if (bIsSelf) {
                 animationResId = R.anim.mystop;
             } else {
                 animationResId = R.anim.stop;
             }

             AnimationDrawable animation = (AnimationDrawable) mContext.getResources().getDrawable(animationResId);

             im.setImageDrawable(animation);
             animation.start();
             currentAnimation = animation;
         } else {
             if (bIsSelf) {
                 im.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
             } else {
                 im.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
             }
         }



         holder.rl_chat_item_content.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //正在播放，停止播放ptt
                 if (currentPlayingIV == im && mIsVoicePlaying) {
                     stopCurrentPttMedia(bIsSelf);
                     return;
                 }
                 DownLoadData.getData(elem.getUrl(), new WildValueCallBack<byte[]>() {
                     @Override
                     public void onSuccess(byte[] bytes) {
                         final byte[] voiceBytes = bytes;

                         mActivity.runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 String filePath = mContext.getFilesDir().getAbsolutePath() + "/ptt/tmp_ptt.arr";

                                 try {
                                     File file = new File(filePath);
                                     if (!file.getParentFile().exists()) {
                                         file.getParentFile().mkdirs();
                                     }

                                     FileOutputStream fos = new FileOutputStream(file);
                                     fos.write(voiceBytes);
                                     fos.flush();
                                     fos.close();
                                     //如果其他消息正在播放，点击取消播放
                                     if (mIsVoicePlaying) {
                                         stopCurrentPttMedia(bIsSelf);
                                     }

                                     mPlayer = new MediaPlayer();
                                     mPlayer.setDataSource(filePath);
                                     mPlayer.prepare();
                                     mPlayer.start();
                                     mIsVoicePlaying = true;

                                     int animationResId;
                                     if (bIsSelf) {
                                         animationResId = R.anim.mystop;
                                     } else {
                                         animationResId = R.anim.stop;
                                     }
                                     final AnimationDrawable animateDrawable = (AnimationDrawable) mContext.getResources().getDrawable(animationResId);
                                     currentPlayingIV = im;
                                     currentAnimation = animateDrawable;
                                     playPos = position;

                                     im.setImageDrawable(animateDrawable);

                                     currentAnimation.start();

                                     mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                         @Override
                                         public void onCompletion(MediaPlayer mp) {
                                             mIsVoicePlaying = false;
                                             if (mPlayer != null) {
                                                 mPlayer.stop();
                                                 mPlayer.release();
                                                 mPlayer = null;
                                             }
                                             currentAnimation.stop();
                                             if (bIsSelf) {
                                                 currentPlayingIV.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
                                             } else {
                                                 currentPlayingIV.setImageResource(R.drawable.skin_aio_ptt_record_friend_nor);
                                             }
                                         }
                                     });


                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }

                             }
                         });


                     }

                     @Override
                     public void onFailed(int code, String des) {
                         Log.e(TAG, "获取语音数据（VoiceMsg）失败");
                     }
                 });
             }
         });
         if (isSelf) {
             if (status.equals(MessageStatus.SENDING)) {
                 holder.pb_status.setVisibility(View.VISIBLE);
                 return;
             }else {
                 holder.pb_status.setVisibility(View.GONE);
             }
         }
        holder.rl_chat_item_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ChatMenuActivity.class);
                intent.putExtra("item", position);
                intent.putExtra("type", ChatActivity.FOR_CHAT_IMAGE_MENU);
                if (status == MessageStatus.FAILED) {
                    intent.putExtra("needReSend", true);
                } else {
                    intent.putExtra("needReSend", false);
                }
                mActivity.startActivityForResult(intent, ChatActivity.FOR_CHAT_IMAGE_MENU);
                Log.d(TAG, "ptt menu,put item:" + position);

                return true;
            }
        });

    }

    private void stopCurrentPttMedia(boolean bIsSelf) {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        currentAnimation.stop();
        if (bIsSelf) {
            currentPlayingIV.setImageResource(R.drawable.skin_aio_ptt_record_user_nor);
        } else {
            currentPlayingIV.setImageResource(R.drawable.skin_aio_ptt_record_friend_nor);
        }
        mIsVoicePlaying = false;
    }

    private void DisplayPicMsg(final ImageMessage e, final boolean isSelf, final ViewHolder holder, final int position, final String status) {
        final ViewHolder viewHolder = holder;
        holder.iv_chat_item_content.setClickable(true);

        holder.iv_chat_item_content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mActivity, ChatMenuActivity.class);
                intent.putExtra("item", position);
                intent.putExtra("type", ChatActivity.FOR_CHAT_IMAGE_MENU);
                if (status == MessageStatus.FAILED) {
                    intent.putExtra("needReSend", true);
                } else {
                    intent.putExtra("needReSend", false);
                }
                mActivity.startActivityForResult(intent, ChatActivity.FOR_CHAT_IMAGE_MENU);
                Log.d(TAG, "pic menu,put item:" + position);

                return true;
            }
        });

        if(isSelf){
            if (status.equals( MessageStatus.SUCCESS)) {
                holder.iv_chat_item_content.setVisibility(View.VISIBLE);
                //TODO 最大尺寸限制
                float widthRate = e.getWidth()/250.0f;
                float hightRate = e.getHeight()/290.0f;
                if(widthRate>1.0 || hightRate>1.0){
                    // 比例大于一需要缩放
                    ImageSize imageSize;
                    if(widthRate>hightRate){
                        imageSize= new ImageSize(250,(int)(e.getHeight()/(widthRate)), 0);
                        Log.d("rate",widthRate+":"+hightRate+":"+250+":"+ (int)(e.getHeight()/(widthRate)));
                    }else if(e.getWidth()==e.getHeight()/290){
                        imageSize= new ImageSize(250, 290, 0);
                    }else {
                        imageSize= new ImageSize((int)(e.getWidth()/hightRate), 290, 0);
                        Log.d("rate",widthRate+":"+hightRate+":"+(int)(e.getWidth()/hightRate)+":"+ 290);
                    }

                    ImageLoader.getInstance().displayImage(e.getThumbnailURL().trim(),holder.iv_chat_item_content);
                }else {
                    // 不需要缩放
                    ImageLoader.getInstance().displayImage(e.getThumbnailURL().trim(),holder.iv_chat_item_content);
                }

                EnlargePictureTool.enlarge(mContext,holder.iv_chat_item_content,e.getOriginalURL());
            }else{
                // 失败和发送中显示的
                holder.iv_chat_item_content.setImageBitmap(getRightOritation(e.getPath(), true));
                // TODO 设置点击重发
            }
        }else {
            float widthRate = e.getWidth()/250.0f;
            float hightRate = e.getHeight()/290.0f;
            if(widthRate>1.0 || hightRate>1.0){
                // 比例大于一需要缩放
                ImageSize imageSize;
                if(widthRate>hightRate){
                    imageSize= new ImageSize(250,(int)(e.getHeight()/(widthRate)), 0);
                    Log.d("rate",widthRate+":"+hightRate+":"+250+":"+ (int)(e.getHeight()/(widthRate)));
                }else if(e.getWidth()==e.getHeight()/290){
                    imageSize= new ImageSize(250, 290, 0);
                }else {
                    imageSize= new ImageSize((int)(e.getWidth()/hightRate), 290, 0);
                    Log.d("rate",widthRate+":"+hightRate+":"+(int)(e.getWidth()/hightRate)+":"+ 290);
                }


                ImageLoader.getInstance().displayImage(e.getThumbnailURL().trim(),holder.iv_chat_item_content);
            }else {
                // 不需要缩放
                ImageLoader.getInstance().displayImage(e.getThumbnailURL().trim(),holder.iv_chat_item_content);
            }
        holder.iv_chat_item_content.setVisibility(View.VISIBLE);
            EnlargePictureTool.enlarge(mContext,holder.iv_chat_item_content,e.getOriginalURL());

        }
        if (isSelf) {
            if (status.equals(MessageStatus.SENDING) ) {
                holder.pb_status.setVisibility(View.VISIBLE);
                return;
            }else {
                holder.pb_status.setVisibility(View.GONE);
            }
        }

    }

    private void SaveMap(String filePath, byte[] bytes) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fops = new FileOutputStream(file);
            fops.write(bytes);
            fops.flush();
            fops.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

        }

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
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        Log.d(TAG, "sampleSize:" + inSampleSize);
        return inSampleSize;
    }

    public static Bitmap getRightOritation(String filePath, boolean isCalculate) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (isCalculate) {
            options.inSampleSize = calculateInSampleSize(options, 198, 198);
        } else {
            options.inSampleSize = 1;
        }
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

    public static Bitmap getRightOritationNew(String filePath) {
        //调用GetRightOritation，采样率为1
        return getRightOritation(filePath, false);
    }
    private View createViewByType(int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_TEXT_SEND:
                return mInflater.inflate(R.layout.chat_item_text_send, null);

            case TYPE_TEXT_RECV:
                return mInflater.inflate(R.layout.chat_item_text_recv, null);

            case TYPE_IMAGE_RECV:
                return mInflater.inflate(R.layout.chat_item_pic_recv, null);

            case TYPE_IMAGE_SEND:
                return mInflater.inflate(R.layout.chat_item_pic_send, null);

            case TYPE_SOUND_SEND:
                return mInflater.inflate(R.layout.chat_item_ptt_send, null);
            case TYPE_SOUND_RECV:
                return mInflater.inflate(R.layout.chat_item_ptt_recv, null);
            case TYPE_GROUP_TIPS:
                return mInflater.inflate(R.layout.chat_item_group_tips, null);

            default:
                return mInflater.inflate(R.layout.chat_item_text_recv, null);
        }

    }

    private void DisplayGrpTips(GroupTipMessage elem, ViewHolder holder, int position) {

        String strTemp = new String();
        String opUser = elem.getOpUser();
       FriendsManamger friendsManamger= WilddogIMApplication.getFriendManager();
        if (elem.getSysType() == GroupMemberOptionType.JOIN) {
            //  TODO 邀请者可能和群内某些人不是好友关系，需要接口获取用户信息
            strTemp += friendsManamger.getFriendInfoById(elem.getOpUser()).getName();
            strTemp += "邀请";
            for (int i = 0; i < elem.getUserIdList().size(); i++) {
                String friendName = friendsManamger.getFriendInfoById(elem.getUserIdList().get(i)).getName();
                if (i == elem.getUserIdList().size() - 1) {
                    strTemp += friendName + " ";
                } else {
                    strTemp += friendName + ",";
                }
            }
            strTemp += "加入群聊";

        } else if (elem.getSysType() == GroupMemberOptionType.QUIT) {
            strTemp+=friendsManamger.getFriendInfoById(elem.getOpUser()).getName()+"退出群聊";
        } else if (elem.getSysType()==GroupMemberOptionType.REMOVE) {
            strTemp += friendsManamger.getFriendInfoById(elem.getOpUser()).getName();
            strTemp += "将";

            for (int i = 0; i < elem.getUserIdList().size(); i++) {
                String friendName = friendsManamger.getFriendInfoById(elem.getUserIdList().get(i)).getName();
                if (i == elem.getUserIdList().size() - 1) {
                    strTemp += friendName + " ";
                } else {
                    strTemp += friendName + ",";
                }
            }
            strTemp += "移除群";
        }

        holder.tv_chatcontent.setText(strTemp);

    }



    class ViewHolder {
        public TextView tv_sendtime;
        public ImageView iv_avatar;
        public TextView tv_username;
        public TextView tv_chatcontent;
        public ProgressBar pb_status;
        public ImageView iv_msg_status;

        public ImageView iv_chat_item_content;

        public RelativeLayout rl_chat_item_content;
        public RelativeLayout rl_pic_new_content;

    }


}
