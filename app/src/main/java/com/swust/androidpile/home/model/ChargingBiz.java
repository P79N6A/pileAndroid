package com.swust.androidpile.home.model;

import android.content.Context;

/**
 * Created by Administrator on 2017/4/9 0009.
 */

public interface ChargingBiz {

    /**
     * 判断填写的充电数据是否正确
     *
     * @param legalOrNot
     * @param buttonStyleLastId
     * @param charging_type
     * @param inputString
     * @param inputHexString
     * @return
     */
    void isLegalOrNot(boolean legalOrNot, int buttonStyleLastId, String charging_type, String inputString, String inputHexString
            , ChargingListener listener);

    /**
     * 扫码充电
     *
     * @param url
     * @param data
     * @param listener
     */
    void startCharging(String url, String data, ChargingListener listener);

    /**
     * 停止远程充电
     *
     * @param url
     * @param phone
     * @param data
     * @param listener
     */
    void stopCharging(String url, String phone, String data, ChargingListener listener);

    /**
     * 添加充电信息
     *
     * @param data
     */
    void addChargingInfo(String data, Context context);

    void addNotes(String notes, Context context);

}
