package com.swust.androidpile.alipay;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.main.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class AliPayBiz {

    /**
     * 查询用户
     * @param context
     * @return
     */
    public User findUser(Context context){
        UserDao userDao = new UserDao(context);
        User user = userDao.findUser();
        return user;
    }

    /**
     * 支付宝支付
     *
     * @param url
     * @param listener
     */
    public void pay(String url, final PayCallBack listener) {
        StringRequest request = new StringRequest(Method.POST, url,
                new Listener<String>() {//参数设置
                    @Override
                    public void onResponse(String jsonstring) {//返回数据成功监听
                        listener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {//错误监听
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {//request参数创建完毕
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                return map;
            }
        };//request创建完毕
        request.setTag("pay");//按照url中的action来确定，保证不会重复
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

}
