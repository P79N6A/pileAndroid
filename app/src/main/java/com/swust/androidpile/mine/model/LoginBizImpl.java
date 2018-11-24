package com.swust.androidpile.mine.model;

import android.content.Context;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.Configure;
import com.swust.androidpile.utils.IMEIUtil;
import com.swust.androidpile.utils.ThreeDESUtil;
import com.swust.androidpile.utils.TokenUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class LoginBizImpl implements LoginBiz {

    /**
     * 账号密码登录
     *
     * @param phone
     * @param password
     * @param listener
     */
    public void login(final Context context, final String url, final String phone, final String password, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
                        listener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                listener.failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                try {
                    obj.addProperty("phone", phone);
                    //加密密码
//                    obj.addProperty("pwd",password);
                    obj.addProperty("pwd", ThreeDESUtil.encrypt(password));
//                    //加密IMEI号
                    obj.addProperty("imei", ThreeDESUtil.encrypt(IMEIUtil.getIMEI(context)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("LoginActivity");
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 请求验证码登录
     */
    public void loginIdentify(final String url, final String code, final String phone, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
//						Log.i("test", jsonstring);
                        listener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                listener.failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("phone", phone);
                //判断是否更新
                obj.addProperty("version", "version");
                //加密密码
                obj.addProperty("code", ThreeDESUtil.encrypt(code));
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("IdentifyingActivity_sure");
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 请求登录验证码
     */
    public void loginCode(final String url, final String phone, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
//                        Log.i("test", jsonstring);
                        listener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                listener.failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("phone", phone);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("IdentifyingActivity_identify");
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }


}
