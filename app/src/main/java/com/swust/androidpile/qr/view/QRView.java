package com.swust.androidpile.qr.view;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public interface QRView {

    void show(String str);

    void showProgress();

    void stopProgress();

    void scan_success();

    void stop_success();

    void stop_failed();

}
