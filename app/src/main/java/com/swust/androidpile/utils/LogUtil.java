package com.swust.androidpile.utils;

import android.util.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/1/11/011.
 */

public class LogUtil {

    //日志打印
    public static void sop(String tag, String logInfo) {
        if (Configure.LOGUTILFLAG) {
            //发布环境
        } else {
            //测试环境
            Log.i("test", tag + "---->" + logInfo);
        }
    }

}
