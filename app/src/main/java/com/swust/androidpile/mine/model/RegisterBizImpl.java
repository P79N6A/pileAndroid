package com.swust.androidpile.mine.model;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.IMEIUtil;
import com.swust.androidpile.utils.ThreeDESUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class RegisterBizImpl implements RegisterBiz {
    /**
     * 请求获取验证码
     *
     * @param url
     * @param phone
     * @param listener
     */
    @Override
    public void registerCode(final String url, final String phone, final MineListener listener) {
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
                obj.addProperty("phone", phone);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("RegisterActivity_identify");
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 请求注册
     *
     * @param url
     * @param code
     * @param phone
     * @param listener
     */
    @Override
    public void register(final String url, final String code, final String phone,
                         final String pwd, final String name, final MineListener listener, final Context context) {
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
                obj.addProperty("name", name);
                obj.addProperty("phone", phone);
                //加密验证码
                obj.addProperty("code", ThreeDESUtil.encrypt(code));
                //加密密码
                obj.addProperty("pwd", ThreeDESUtil.encrypt(pwd));
                //加密IMEI号
                obj.addProperty("imei", ThreeDESUtil.encrypt(IMEIUtil.getIMEI(context)));

                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("RegisterActivity_register");
        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }
}
