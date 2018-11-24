package com.swust.androidpile.home.model;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.main.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：Created by chen on 2017-06-06.
 * 邮箱：1663268062@qq.com
 */

public class ApplyBillBizImpl implements ApplyBillBiz {
    /**
     * 查询本地用户
     *
     * @param context
     * @return
     */
    @Override
    public User findUser(Context context) {
        UserDao userDao = new UserDao(context);
        User user = userDao.findUser();
        return user;
    }

    /**
     * 获取充电账单
     *
     * @param url
     * @param phone
     * @param listener
     */
    @Override
    public void applyChargingBill(final String url, final String phone, final ApplyBillListener listener) {
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
        request.setTag("ApplyChargingBill");
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 获取充值账单
     *
     * @param url
     * @param phone
     * @param money
     * @param listener
     */
    @Override
    public void applyMoneyBill(String url, final String phone, final double money, final ApplyBillListener listener) {
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
                obj.addProperty("money", money);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("ApplyMoneyBill");
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }
}
