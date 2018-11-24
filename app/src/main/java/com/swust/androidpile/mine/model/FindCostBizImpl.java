package com.swust.androidpile.mine.model;

import android.util.Log;

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
 * Created by Administrator on 2017/4/8 0008.
 */

public class FindCostBizImpl implements FindCostBiz {

    /**
     * 充电记录查询
     *
     * @param url
     * @param phone
     * @param listener
     */
    @Override
    public void findCost(String url, final String phone, final int count, final MineListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {//参数设置
                    @Override
                    public void onResponse(String arg0) {//返回数据成功监听
                        Log.i("test","findcost");
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
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("phone", phone);//电话号码
                jsonObject.addProperty("count", count);//请求下拉页
                map.put("jsonstring", jsonObject.toString());
                return map;
            }
        };//request创建完毕
        request.setTag("findCost");//按照url中的action来确定，保证不会重复
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    //充值记录查询
    @Override
    public void findChargeCost(String url, final String phone, final MineListener listener) {
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
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("phone", phone);//电话号码
                map.put("jsonstring", jsonObject.toString());
                return map;
            }
        };//request创建完毕
        request.setTag("findChargeCost");//按照url中的action来确定，保证不会重复
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }


}
