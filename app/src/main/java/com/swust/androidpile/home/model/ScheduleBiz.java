package com.swust.androidpile.home.model;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public interface ScheduleBiz {

    /**
     * 查询充电进度
     * @param url
     * @param phone
     * @param listener
     */
    void schedule(String url,String phone,HomeListener listener);

    public void stopCharging(String url, final String phone, final ChargingListener listener);
}
