package com.swust.androidpile.home.model;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.entity.Charging;
import com.swust.androidpile.entity.Notes;
import com.swust.androidpile.main.MyApplication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/9 0009.
 */

public class ChargingBizImpl implements ChargingBiz {

    /**
     * 判断填写的充电数据是否正确
     *
     * @param legalOrNot
     * @param buttonStyleLastId
     * @param charging_type
     * @param inputString
     * @param inputHexString
     * @return
     */
    @Override
    public void isLegalOrNot(boolean legalOrNot, int buttonStyleLastId, String charging_type, String inputString, String inputHexString
            , ChargingListener listener) {
        List<Object> list = new ArrayList<Object>();
        legalOrNot = true;
        if (buttonStyleLastId == 0) {//自动充满
            legalOrNot = true;
            charging_type = "00";
            inputString = "0";
            inputHexString = "00";
        } else {
            if (buttonStyleLastId == 4) {//按进度冲,数值最多可填写2位，且只能是整数
                charging_type = "04";
                if (inputString.length() > 2) {
                    listener.failed("最多2位数值！");
                    legalOrNot = false;
                } else {
                    try {
                        int inputInt = Integer.parseInt(inputString);
                        inputHexString = Integer.toHexString(inputInt).toUpperCase();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        legalOrNot = false;
                        listener.failed("请填写整数值！");
                    }
                }
            } else if (buttonStyleLastId == 1) {//按时间充，需要转化小时到分数。可以输入小数，最多3位数值，包括小数点
                charging_type = "01";
                if (inputString.length() > 3) {//这里是3不是2，因为小数点也占一位
                    listener.failed("最多3位数值，包括小数点！");
                    legalOrNot = false;
                } else {
                    try {
                        double inputDouble = Double.parseDouble(inputString);
                        inputDouble *= 60;//小时转为分钟数
                        inputHexString = (Integer.toHexString((int) inputDouble)).toUpperCase();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        legalOrNot = false;
                        listener.failed("充电预设值不正确！");//这里输入了其他符号
                    }
                }
            } else {//按照电量、金额充。    最多3位数值，且只能是整数
                if (buttonStyleLastId == 2) {
                    charging_type = "02";
                } else {
                    charging_type = "03";
                }
                if (inputString.length() > 3) {
                    listener.failed("最多3位数值！");
                    legalOrNot = false;
                } else {
                    try {
                        int inputInt = Integer.parseInt(inputString);
                        inputInt *= 100;
                        inputHexString = Integer.toHexString(inputInt).toUpperCase();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        legalOrNot = false;
                        listener.failed("请填写整数值！");
                    }
                }
            }
        }
        list.add(legalOrNot);
        list.add(buttonStyleLastId);
        list.add(charging_type);
        list.add(inputString);
        list.add(inputHexString);
        listener.isLegalOrNot(list);
    }

    /**
     * 扫码充电
     *
     * @param url
     * @param data
     * @param listener
     */
    @Override
    public void startCharging(final String url, final String data, final ChargingListener listener) {
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
                map.put("jsonstring", data);
                return map;
            }
        };//request创建完毕
        request.setTag("scan");//按照url中的action来确定，保证不会重复
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(90000, 0, 1f));
        MyApplication.getHttpQueue().add(request);
    }

    @Override
    public void stopCharging(String url, final String phone, final String data, final ChargingListener listener) {
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
//                jsonObj.addProperty("packageData", data);
                map.put("jsonstring", jsonObj.toString());
                return map;
            }
        };//request创建完毕
        request.setTag("stop_scan");//按照url中的action来确定，保证不会重复
        //超时不再重传
        request.setRetryPolicy(new DefaultRetryPolicy(90000, 0, 1f));
        MyApplication.getHttpQueue().add(request);
    }

    /**
     * 添加充电信息
     *
     * @param data
     */
    @Override
    public void addChargingInfo(String data, Context context) {
        Charging charging = new Charging(data);
        ChargingDao chargingDao = ChargingDao.getInstance(context);
        chargingDao.deleteCharging();
        chargingDao.addCharging(charging);
    }

    /**
     * 添加充电参数
     *
     * @param notes
     * @param context
     */
    public void addNotes(String notes, Context context) {
        Notes n = new Notes(notes);
        ChargingDao chargingDao = ChargingDao.getInstance(context);
        chargingDao.deleteNotes();
        chargingDao.addNotes(n);
    }

}
