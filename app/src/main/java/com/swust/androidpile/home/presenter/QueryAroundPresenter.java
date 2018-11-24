package com.swust.androidpile.home.presenter;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.swust.androidpile.home.model.HomeListener;
import com.swust.androidpile.home.model.QueryAroundBiz;
import com.swust.androidpile.home.view.QueryAroundView;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class QueryAroundPresenter {

    private QueryAroundView view;
    private QueryAroundBiz model;
    private Context context;

    public void setViewAndModel(Context context, QueryAroundView view, QueryAroundBiz model) {
        this.context = context;
        this.view = view;
        this.model = model;
    }

    /**
     * 寻找附近充电站
     *
     * @param url
     * @param city
     * @param latLng
     */
    public void findChargingStations(String url, final String city, final LatLng latLng) {
        model.findChargingStations(url, city, latLng, new HomeListener() {
            @Override
            public void success(String json) {
//                Log.i("test", "QueryAroundPresenter->" + json);
                view.stopProgressDialog();
                if (json.equals("error")) {
                    view.show("当前城市无充电站");
                } else {
                    try {
//                        Log.i("test","QueryAroundPresenter->"+"执行getStations()方法");
                        view.getStations(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed() {
                view.stopProgressDialog();
                view.show("网络请求失败");
//                Log.i("test","failed");
            }
        });
    }

    public void findChargingStationsByCondition(String url, final String city, final LatLng latLng) {
        view.showProgressDialog();
        model.findChargingStationsByCondition(url, city, latLng, new HomeListener() {
            @Override
            public void success(String jsonstring) {
                view.stopProgressDialog();
                if (jsonstring.equals("error")) {
                    view.show("当前城市无充电站");
                } else {
                    try {
                        view.getStations(jsonstring);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed() {
                view.stopProgressDialog();
                view.show("网络请求失败");
            }
        });
    }

    public void findChargingStationsByUpdate(String url, final String city, final LatLng latLng) {
        model.findChargingStationsByCondition(url, city, latLng, new HomeListener() {
            @Override
            public void success(String jsonstring) {
                view.stopProgressDialog();
                if (jsonstring.equals("error")) {
                    view.show("当前城市无充电站");
                } else {
                    try {
                        view.getStationsByUpdate(jsonstring);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
                view.stopProgressDialog();
            }
        });
    }

}
