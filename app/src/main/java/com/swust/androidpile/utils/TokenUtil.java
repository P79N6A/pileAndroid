package com.swust.androidpile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/4/1/001.
 */

public class TokenUtil {
    public static String TOKENUTIL_TOKEN = "";

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        return token;
    }

    /**
     * 传递回来的token是加密过的
     *
     * @param context
     * @param token
     */
    public static void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        //获取到edit对象
        SharedPreferences.Editor edit = sharedPreferences.edit();
        //通过editor对象写入数据
        edit.putString("token", token);
        //提交数据存入到xml文件中
        edit.commit();
    }

    /**
     * token更新
     *
     * @param token
     * @param context
     */
    public static void updateToken(String token, Context context) {
//        LogUtil.sop("test", "token=" + token);
        setToken(context, token);
        TOKENUTIL_TOKEN = token;
    }

    public static boolean checkToken(Context context) {
        String token = "";
        if (TOKENUTIL_TOKEN.equals("")) {
            if (getToken(context).equals("")) {
                setToken(context, "");
                return false;
            }
            token = getToken(context);
            TOKENUTIL_TOKEN = token;
        } else {
            token = TOKENUTIL_TOKEN;
//            LogUtil.sop("test", token);
        }
        try {
            //token解密
            token = ThreeDESUtil.decrypt(token);
//            LogUtil.sop("test", "字符token=" + token);
            long nowTime = new Date().getTime() / 1000;
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//            String s = sdf.format(new Date());
//            Log.i("test",s);
            long longTime;
            try {
//                LogUtil.sop("test", "try");
                longTime = Long.parseLong(token);
//                LogUtil.sop("test", "now=" + nowTime + " , old=" + longTime);
                if (longTime > nowTime) {
                    return true;
                } else {
                    TOKENUTIL_TOKEN = "";
                    setToken(context, "");
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            TOKENUTIL_TOKEN = "";
            setToken(context, "");
        }
        return false;
    }
}
