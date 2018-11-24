package com.swust.androidpile.home.model;

import com.amap.api.maps.model.LatLng;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public interface QueryAroundBiz {

    /**
     * 查询附近的电站
     *
     * @param url
     * @param latLng
     * @param homeListener
     */
    void findChargingStations(String url, String city, LatLng latLng, HomeListener homeListener);

    void findChargingStationsByCondition(String url, String city, LatLng latLng, HomeListener homeListener);

}
