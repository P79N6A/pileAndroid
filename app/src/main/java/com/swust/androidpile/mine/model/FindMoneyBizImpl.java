package com.swust.androidpile.mine.model;

import android.util.Log;

import com.android.volley.AuthFailureError;
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

public class FindMoneyBizImpl implements FindMoneyBiz {

    //余额查询
    @Override
    public void findMoney(String url, final String phone, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {//参数设置
                    @Override
                    public void onResponse(String arg0) {//返回数据成功监听
                        Log.i("test","findmoney");
                        listener.success(arg0);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.failed();
            }
        }) {
            //request参数创建完毕
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("phone", phone);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };//request创建完毕
        request.setTag("findMoney");//按照url中的action来确定，保证不会重复
        MyApplication.getHttpQueue().add(request);
    }

}
