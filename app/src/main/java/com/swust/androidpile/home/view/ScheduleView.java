package com.swust.androidpile.home.view;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public interface ScheduleView {
    void showProgress();
    void stopProgress();
    void show(String str);
    void startActivity(String jsonstring);
    void reStartSchedule(String jsonstring);

    /**
     * 关闭充电成功
     */
    void stopSuccess();

    /**
     * 轮询进度信息，充电结束操作
     */
    void isNotCharging();
}
