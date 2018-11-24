package com.swust.androidpile.main;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.swust.androidpile.utils.IMEIUtil;
import com.swust.androidpile.utils.LogUtil;

public class MyApplication extends Application {
    public static RequestQueue queue;//网络请求队列
    private static final String TAG = "test";

    @Override
    public void onCreate() {
        super.onCreate();

        queue = Volley.newRequestQueue(getApplicationContext());
        initCloudChannel(this);
    }

    public static RequestQueue getHttpQueue() {
        return queue;
    }

    /**
     * 初始化云推送通道
     *
     * @param applicationContext
     */
    private void initCloudChannel(Context applicationContext) {
        PushServiceFactory.init(applicationContext);
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        register(applicationContext, pushService);//推送注册
    }

    /**
     * 推送注册
     *
     * @param applicationContext
     * @param pushService
     */
    public void register(final Context applicationContext, final CloudPushService pushService) {

        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                LogUtil.sop(TAG, "推送注册成功，返回值--->" + response);
                LogUtil.sop(TAG, "deviceId--->" + pushService.getDeviceId());
                bindAccount(applicationContext, pushService);//绑定账号，值为IMEI号
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                LogUtil.sop(TAG, "推送注册失败，返回值--->init cloudchannel failed -- errorcode:" + errorCode + " -- errorMessage:" + errorMessage);
                register(applicationContext, pushService);//重新注册
            }
        });
    }

    /**
     * 绑定账号
     *
     * @param applicationContext
     * @param pushService
     */
    public void bindAccount(final Context applicationContext, final CloudPushService pushService) {

        //获取IMEI
        String imei = IMEIUtil.getIMEI(applicationContext);
        LogUtil.sop(TAG, "imei--->" + imei);

        //账户名就是IMEI
        pushService.bindAccount(imei, new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtil.sop(TAG, "绑定账户成功，返回值--->" + s);
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtil.sop(TAG, "绑定账户失败，失败信息--->" + s + " , " + s1);
                bindAccount(applicationContext, pushService);//重新绑定账户
            }
        });
    }

    public void unBindAccound(CloudPushService pushService) {
        pushService.unbindAccount(new CommonCallback() {
            @Override
            public void onSuccess(String s) {
                LogUtil.sop("test", "接触绑定成功");
            }

            @Override
            public void onFailed(String s, String s1) {
                LogUtil.sop("test", "接触绑定失败");
            }
        });
    }

}
