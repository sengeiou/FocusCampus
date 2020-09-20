package com.sam.rental.ui.activity;

import android.content.Intent;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.sam.rental.R;
import com.sam.rental.common.MyActivity;

/**
 * 聊天详情页面
 */
public class ChatActivity extends MyActivity implements EaseChatFragment.EaseChatFragmentHelper {

    public static final String TAG = "ChatActivity";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView() {
        //use EaseChatFratFragment

        EaseChatFragment chatFragment = new EaseChatFragment();
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        // pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.layout_chat, chatFragment).commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected boolean isStatusBarEnabled() {
        return false;
    }


    @Override
    public void onSetMessageAttributes(EMMessage message) {
        /*message.setAttribute("nickName", "李思思");
        message.setAttribute("headPic", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1600016679561&di=75d0d27a11a97ff7cfa15842a2a0b3ba&imgtype=0&src=http%3A%2F%2Fa1.att.hudong.com%2F05%2F00%2F01300000194285122188000535877.jpg");
        message.setAttribute("userId", "22");*/
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {

    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }
}
