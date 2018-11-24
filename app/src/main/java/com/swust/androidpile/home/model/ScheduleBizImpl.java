package com.swust.androidpile.home.model;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class ScheduleBizImpl implements ScheduleBiz {

	private static final String TAG = "ScheduleBizImpl";

	@Override
	public void stopCharging(String url, final String phone, final ChargingListener listener) {
		StringRequest request = new StringRequest(Request.Method.POST, url,
				new Response.Listener<String>() {//参数设置
					@Override
					public void onResponse(String jsonstring) {//返回数据成功监听
						listener.success(jsonstring);
					}
				}, new Response.ErrorListener() {//错误监听
			@Override
			public void onErrorResponse(VolleyError error) {
				listener.failed("请求超时！");
				error.printStackTrace();
			}
		}) {//request参数创建完毕
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> map = new HashMap<String, String>();
				JsonObject jsonObj = new JsonObject();
				jsonObj.addProperty("phone", phone);
				map.put("jsonstring", jsonObj.toString());
				return map;
			}
		};//request创建完毕
		request.setTag("stop_scan");//按照url中的action来确定，保证不会重复
		//设置超时重传时间
		request.setRetryPolicy(new DefaultRetryPolicy(90000,0,1f));
		MyApplication.getHttpQueue().add(request);
	}

    /**
     * 查询充电进度
     * @param url
     * @param phone
     * @param listener
     */
    @Override
    public void schedule(String url, final String phone, final HomeListener listener) {
		LogUtil.sop(TAG,"查询充电进度请求中");

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
						error.printStackTrace();
					}
				}) {//request参数创建完毕
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				HashMap<String, String> map = new HashMap<String, String>();
				JsonObject obj=new JsonObject();
				obj.addProperty("phone", phone);
				map.put("jsonstring", obj.toString());
				return map;
			}
		};//request创建完毕
		request.setTag("schedulePile");//按照url中的action来确定，保证不会重复
		request.setRetryPolicy(new DefaultRetryPolicy(50000,0,1.0f));
		MyApplication.getHttpQueue().add(request);
	}

}
