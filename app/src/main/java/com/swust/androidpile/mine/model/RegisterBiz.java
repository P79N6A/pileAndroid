package com.swust.androidpile.mine.model;

import android.content.Context;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface RegisterBiz {

    /**
     * 请求登录验证码
     * @param url
     * @param phone
     */
    void registerCode(String url, String phone, MineListener listener);

    /**
     * 请求验证码注册
     * @param url
     * @param code
     * @param phone
     * @param listener
     */
    void register(String url, String code, String phone, String pwd, String name, MineListener listener, Context context);

}
