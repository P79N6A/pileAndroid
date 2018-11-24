package com.swust.androidpile.main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;
import com.swust.androidpile.mine.activity.LoginActivity;
import com.swust.androidpile.mine.fragment.MineFragment;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.TokenUtil;

import java.util.Map;

/**
 * Created by Administrator on 2018/4/3/003.
 */

public class MyReceiver extends MessageReceiver {
    private static final String TAG = "test";

    @Override
    public void onNotification(Context context, String title, String summary, Map<String, String> extraMap) {
        LogUtil.sop(TAG, "大标题--->" + title + "\n小标题--->" + summary);
        if (title.equals("下线通知")) {
            //token清空
            TokenUtil.TOKENUTIL_TOKEN = "";
            TokenUtil.setToken(context, "");
            //MineFragment用户名设置
            MineFragment.MINEFRAGMENT_NAMETV.setText("登录/注册");
            //强制当前用户下线，前往登录界面
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    public void onMessage(Context context, CPushMessage cPushMessage) {
        LogUtil.sop(TAG, "onMessage, messageId: " + cPushMessage.getMessageId() + "," +
                " title: " + cPushMessage.getTitle() + ", content:" + cPushMessage.getContent());
    }

    @Override
    public void onNotificationOpened(Context context, String title, String summary, String extraMap) {
        LogUtil.sop(TAG, "onNotificationOpened, title: " + title + ", summary: " + summary + ", extraMap:" + extraMap);
        //通知点击以后的打开界面
        Intent intent = new Intent(context, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("title", title);
        intent.putExtra("content", summary);
        context.startActivity(intent);
    }

    @Override
    protected void onNotificationClickedWithNoAction(Context context, String title, String summary, String extraMap) {
        LogUtil.sop(TAG, "onNotificationClickedWithNoAction, title:" +
                " " + title + ", summary: " + summary + ", extraMap:" + extraMap);
    }

    @Override
    protected void onNotificationReceivedInApp(Context context, String title, String summary, Map<String, String> extraMap, int openType, String openActivity, String openUrl) {
        LogUtil.sop(TAG, "onNotificationReceivedInApp, title: " + title + ", summary: " +
                summary + ", extraMap:" + extraMap + ", openType:" + openType + ", openActivity:" + openActivity + ", openUrl:" + openUrl);
    }

    @Override
    protected void onNotificationRemoved(Context context, String messageId) {
        LogUtil.sop(TAG, "onNotificationRemoved");
    }
}
