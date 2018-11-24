package com.swust.androidpile.home.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.home.model.ChargingListener;
import com.swust.androidpile.home.model.HomeListener;
import com.swust.androidpile.home.model.ScheduleBiz;
import com.swust.androidpile.home.view.ScheduleView;
import com.swust.androidpile.qr.activity.QRActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/30 0030.
 */

public class SchedulePresenter {

    private ScheduleView view;
    private ScheduleBiz model;
    private Context context;
    private int index;

    public void setViewAndModel(Context context, ScheduleView view, ScheduleBiz model, int index) {
        this.context = context;
        this.view = view;
        this.model = model;
        this.index = index;
    }

    /**
     * 充电进度页面发起的停止充电请求
     *
     * @param url
     * @param phone
     */
    public void stopCharging(String url, String phone) {
        model.stopCharging(url, phone, new ChargingListener() {
            @Override
            public void success(String json) {
                Log.i("test", "进度页面结束充电返回值->" + json);
                view.stopProgress();//关闭弹出框
                QRActivity.nodes = null;
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int codeFlag = obj.get("code").getAsInt();
                ChargingDao chargingDao = ChargingDao.getInstance(context);
                switch (codeFlag) {
                    case 0://停止充电成功（主动关闭）
                    case 1://充电已结束（被动关闭）
                        view.show("停止充电成功！");
                        chargingDao.deleteCharging();
                        chargingDao.deleteNotes();
                        view.stopSuccess();
                        break;
                    case 2://socket连接失败！
                        view.show("socket连接失败！");
                        break;
                    case 6://直流充电准备中，提示用户排队等待
                        view.show("直流充电准备中，提示用户排队等待！");
                        break;
                    case 10://充电口已占用
                        view.show("充电口已占用！");
                        break;
                    case 11://电表通讯故障
                        view.show("电表通讯故障！");
                        break;
                    case 13://电缆故障
                        view.show("电缆故障！");
                        break;
                    case 14://接触器故障
                        view.show("接触器故障！");
                        break;
                    case 15://电缆故障
                        view.show("绝缘故障！");
                        break;
                    case 16://BMS通信故障
                        view.show("BMS通信故障！");
                        break;
                    case 20://用户
                        view.show("BMS通信故障！");
                        break;
                    case 32://余额不足
                        view.show("余额不足");
                        break;
                    case 31://其他故障
                    case Integer.MAX_VALUE://设备不可充
                        view.show("其他故障！");
                        break;
                    default:
                        view.show("其他故障！");
                        break;
                }
            }

            @Override
            public void failed(String json) {
                view.stopProgress();//关闭弹出框
                view.show(json);
            }

            @Override
            public void isLegalOrNot(List<Object> list) {

            }
        });
    }

    /**
     * 查询充电进度
     *
     * @param url
     * @param phone
     */
    public void schedule(String url, String phone) {
        model.schedule(url, phone, new HomeListener() {
            @Override
            public void success(String json) {
//                if (index != 1) {
//                    ToastUtil.showToast(context, "轮询进度信息如下所示：\n" + json);
//                }
                Log.i("test", "轮询返回值=" + json);

                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                boolean flag = obj.get("flag").getAsBoolean();
                if (flag == false) {
                    //代表没在充电
                    view.show("请先充电！");
                    if (index == 1) {//首次查询进度信息
                        view.stopProgress();
                    } else {//轮询进度信息
                        view.isNotCharging();
                    }
                } else {
                    if (index == 1) {
                        view.stopProgress();//关闭弹出框
                        view.startActivity(json);//第一次充电查询进度
                    } else {
                        view.reStartSchedule(json);//充电进度页面更新
                    }
                }
            }

            @Override
            public void failed() {
                view.stopProgress();
                view.show("网络请求失败！");
            }
        });
    }


}
