package com.swust.androidpile.home.model;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class QueryAroundBizImpl implements QueryAroundBiz {

    private static final String TAG = "QueryAroundBizImpl";

    /**
     * 查询附近的电站
     *
     * @param url
     * @param latLng
     * @param homeListener
     */
    @Override
    public void findChargingStations(String url, final String city, final LatLng latLng, final HomeListener homeListener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
                        homeListener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                homeListener.failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("lat", latLng.latitude);
                obj.addProperty("lng", latLng.longitude);
                obj.addProperty("city", city);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("QueryAroundFragment");
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 根据所选城市名搜索附近充电站
     *
     * @param url
     * @param city
     * @param latLng
     * @param homeListener
     */
    @Override
    public void findChargingStationsByCondition(String url, final String city, final LatLng latLng, final HomeListener homeListener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
//                        LogUtil.sop(TAG, jsonstring);
                        homeListener.success(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                homeListener.failed();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("lat", latLng.latitude);
                obj.addProperty("lng", latLng.longitude);
                obj.addProperty("city", city);
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("QueryActivity");
        request.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 检查当前网络是否可用
     */
    public boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取离线电站数据
     */
    public JsonArray getNativeStatiions(Context context) {
        BufferedReader reader = null;
        JsonArray jsonArray = null;
        StringBuilder content = new StringBuilder();
        try {
            FileInputStream in = null;
            in = context.openFileInput("station");
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            line = content.toString();
            if (!line.equals("")) {
                JsonParser parser = new JsonParser();
                JsonObject j = parser.parse(line).getAsJsonObject();
//                    Log.i("test","j="+j.toString());
                String arryString = j.get("stations").getAsString();
                jsonArray = parser.parse(arryString).getAsJsonArray();
//                Log.i("test","stations="+jsonArray.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonArray;
    }


}
