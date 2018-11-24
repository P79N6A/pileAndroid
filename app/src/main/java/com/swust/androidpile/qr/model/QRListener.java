package com.swust.androidpile.qr.model;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public interface QRListener {

    void success(String jsonstring);

    void failed();

}
