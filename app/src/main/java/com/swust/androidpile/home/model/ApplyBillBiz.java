package com.swust.androidpile.home.model;

import android.content.Context;

import com.swust.androidpile.entity.User;

/**
 * 作者：Created by chen on 2017-06-06.
 * 邮箱：1663268062@qq.com
 */

public interface ApplyBillBiz {

    /**
     * 查询本地用户
     *
     * @param context
     * @return
     */
    public User findUser(Context context);

    /**
     * 获取充电订单
     *
     * @param url
     * @param phone
     * @param listener
     */
    public void applyChargingBill(String url, String phone, ApplyBillListener listener);

    /**
     * 获取充值订单
     * @param url
     * @param phone
     * @param money
     * @param listener
     */
    public void applyMoneyBill(String url, String phone, double money, ApplyBillListener listener);

}
