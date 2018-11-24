package com.swust.androidpile.mine.model;

import android.content.Context;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface LoginBiz {
    /**
     * 账号密码登录
     * @param url
     * @param phone
     * @param password
     * @param listener
     */
    void login(Context context, String url, String phone, String password, MineListener listener);

    /**
     * 请求登录验证码
     * @param url
     * @param phone
     */
    void loginCode(String url, String phone,MineListener listener);

    /**
     * 请求验证码登录
     * @param url
     * @param code
     * @param phone
     * @param listener
     */
    void loginIdentify(String url,String code,String phone,MineListener listener);

}
