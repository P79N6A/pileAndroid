package com.swust.androidpile.mine.model;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.main.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class AdviceBizImpl implements AdviceBiz {
    /**
     * 提交我的建议
     * @param url
     * @param phone
     * @param advice
     * @param listener
     */
    @Override
    public void mineAdvice(String url, final String phone, final String advice, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {//参数设置
                    @Override
                    public void onResponse(String arg0) {//返回数据成功监听
                        listener.success(arg0);
                    }
                }, new Response.ErrorListener() {//错误监听
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.failed();
            }
        }) {//request参数创建完毕
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj=new JsonObject();
                obj.addProperty("phone", phone);
                obj.addProperty("advice", advice);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };//request创建完毕
        request.setTag("MineAdviceActivity");//按照url中的action来确定，保证不会重复
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }
}
