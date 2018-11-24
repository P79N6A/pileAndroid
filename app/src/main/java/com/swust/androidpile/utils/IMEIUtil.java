package com.swust.androidpile.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Administrator on 2018/4/1/001.
 */

public class IMEIUtil {
    /**
     * 获取IMEI号（手机唯一识别码，15位，但手机代码获取只有14位，第15位要算法生成）
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}