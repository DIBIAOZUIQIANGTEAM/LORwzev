package com.wsns.lor.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.wsns.lor.R;
import com.wsns.lor.other.chatting.activity.MessageFragment;
import com.wsns.lor.utils.SmileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.MessageDirect;
import cn.jpush.im.android.api.enums.MessageStatus;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

@SuppressLint("InflateParams")
public class ConversationAdapter extends BaseAdapter {
    private List<Conversation> normal_list;
    private LayoutInflater inflater;
    private Context context;
    private int change = 1;
    ViewHolder holder;
    private MessageFragment messageFragment;

    @SuppressLint("SdCardPath")
    public ConversationAdapter(MessageFragment messageFragment, Context context,
                               List<Conversation> normal_list
    ) {
        this.messageFragment=messageFragment;
        this.context = context;
        this.normal_list = normal_list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return normal_list.size();
    }

    @Override
    public Conversation getItem(int position) {
        return normal_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final String nickname;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            holder = new ViewHolder();
            convertView = creatConvertView();

            // 初始化控件
            // 昵称
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            // 未读消息
            holder.tv_unread = (TextView) convertView.findViewById(R.id.tv_unread);
            // 最近一条消息
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            // 时间
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            // 发送状态
            holder.msgState = (ImageView) convertView.findViewById(R.id.msg_state);
            // 单聊数据加载
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            convertView.setTag(holder);
        }
        // 获取与此用户/群组的会话
        final Conversation conversation = getItem(position);
        // 获取用户nickname或者群组groupid
        UserInfo targetInfo = (UserInfo) conversation.getTargetInfo();
        nickname = targetInfo.getNickname();

        // 显示昵称
        holder.tv_name.setText(nickname);
        // 显示头像

        targetInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (bitmap != null)
                    holder.iv_avatar.setImageBitmap(bitmap);
            }
        });


        if (conversation.getUnReadMsgCnt() > 0) {
            // 显示与此用户的消息未读数
            holder.tv_unread.setText(String.valueOf(conversation
                    .getUnReadMsgCnt()));
            holder.tv_unread.setVisibility(View.VISIBLE);
        } else {
            holder.tv_unread.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMessage().size() != 0) {
            // 把最后一条消息的内容作为item的message内容
            Message lastMessage = conversation.getLatestMessage();
            holder.tv_content.setText(
                    SmileUtils.getSmiledText(context,
                            getMessageDigest(lastMessage, context)),
                    BufferType.SPANNABLE);
            Date date = new Date(lastMessage.getCreateTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            String createTime = simpleDateFormat.format(date);
            holder.tv_time.setText(createTime);
            if (lastMessage.getDirect() == MessageDirect.send
                    && lastMessage.getStatus() == MessageStatus.send_fail) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }


        RelativeLayout re_parent = (RelativeLayout) convertView
                .findViewById(R.id.re_parent);

        re_parent.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String title = "操作";
                showDialog(title, conversation);
                return false;
            }
        });
        if (position < normal_list.size()) {
            // 加入删除后
            re_parent.setBackgroundColor(0xFFF5FFF1);
        }
        return convertView;
    }

    private View creatConvertView() {
        View convertView;

        convertView = inflater.inflate(R.layout.item_conversation_single,
                null, false);

        return convertView;

    }

    private static class ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView tv_name;
        /**
         * 消息未读数
         */
        TextView tv_unread;
        /**
         * 最后一条消息的内容
         */
        TextView tv_content;
        /**
         * 最后一条消息的时间
         */
        TextView tv_time;
        /**
         * 用户头像
         */
        ImageView iv_avatar;
        ImageView msgState;

    }


    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(Message message, Context context) {
        String digest = "";
        switch (message.getContentType()) {

            case image: // 图片消息

                digest = "图片";

                break;

            case text: // 文本消息

                TextContent stringExtra = (TextContent) message.getContent();
                digest = stringExtra.getText();
                break;

            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }


    private void showDialog(String title, final Conversation conversation) {

        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();

        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.fragment_message_alertdialog);

        window.findViewById(R.id.ll_title).setVisibility(View.VISIBLE);

        TextView tv_title = (TextView) window.findViewById(R.id.tv_title);
        tv_title.setText(title);

        TextView tv_content = (TextView) window.findViewById(R.id.tv_content1);
        tv_content.setText("删除该聊天");
        tv_content.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                JMessageClient.deleteSingleConversation(((UserInfo) conversation.getTargetInfo()).getUserName());
                messageFragment.refresh();
                dlg.cancel();
            }
        });

    }

}
