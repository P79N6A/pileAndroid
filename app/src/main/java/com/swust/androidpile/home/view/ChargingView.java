package com.swust.androidpile.home.view;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by Administrator on 2017/4/9 0009.
 */

public interface ChargingView {

    /**
     * 显示消息
     *
     * @param jsonstring
     */
    void show(String jsonstring);

    /**
     * 验证数据是否填写正确后的返回值
     *
     * @param list
     */
    void isLegalOrNot(List<Object> list);

    void stopProgress();

    void showProgress();

    void startCharging(JsonObject obj);

    void stopCharging();

}
