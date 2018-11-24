package com.swust.androidpile.qr.model;

import android.content.Context;

import java.util.List;

public interface QRBiz {
    /**
     * 发送充电请求
     */
    void scan(String url, String data, QRListener listener);


    /**
     * 发送停止充电请求
     */
    void stop_scan(String url, String phone, String data, QRListener listener);


    /**
     * 判断填写信息是否合法
     *
     * @param context
     * @param legalOrNot
     * @param buttonStyleLastId
     * @param charging_type
     * @param inputString
     * @param inputHexString
     * @return
     */
    List<Object> isLegalOrNot(Context context, boolean legalOrNot, int buttonStyleLastId, String charging_type, String inputString,
                              String inputHexString);

    /**
     * 添加充电信息
     *
     * @param data
     */
    void addChargingInfo(String data, Context context);

    void addNotes(String notes, Context context);
}
