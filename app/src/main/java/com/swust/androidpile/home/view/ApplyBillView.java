package com.swust.androidpile.home.view;

/**
 * 作者：Created by chen on 2017-06-06.
 * 邮箱：1663268062@qq.com
 */

public interface ApplyBillView {
    /**
     * 页面显示信息
     *
     * @param msg
     */
    void show(String msg);

    /**
     * 充电账单
     *
     * @param msg
     */
    void getChargingBillInfo(String msg);

    /**
     * 充值账单
     *
     * @param msg
     */
    void getMoneyBillInfo(String msg);
}
