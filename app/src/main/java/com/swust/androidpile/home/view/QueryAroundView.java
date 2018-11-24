package com.swust.androidpile.home.view;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public interface QueryAroundView {

    void show(String jsonstring);

    void showProgressDialog();

    void stopProgressDialog();

    void getStations(String jsonstring);

    void getStationsByUpdate(String jsonstring);

}
