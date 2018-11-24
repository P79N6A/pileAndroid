package com.swust.androidpile.home.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.home.model.ChargingListener;
import com.swust.androidpile.home.model.ChargingBiz;
import com.swust.androidpile.home.view.ChargingView;
import com.swust.androidpile.qr.activity.QRActivity;

import java.util.List;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class ChargingPresenter {

    private ChargingView view;
    private ChargingBiz model;
    private Context context;

    public void setViewAndModel(Context context, ChargingView view, ChargingBiz model) {
        this.context = context;
        this.view = view;
        this.model = model;
    }

    /**
     * 判断填写的充电数据是否正确
     *
     * @param legalOrNot
     * @param buttonStyleLastId
     * @param charging_type
     * @param inputString
     * @param inputHexString
     */
    public void isLegalOrNot(boolean legalOrNot, int buttonStyleLastId, String charging_type, String inputString, String inputHexString) {
        model.isLegalOrNot(legalOrNot, buttonStyleLastId, charging_type, inputString, inputHexString, new ChargingListener() {
            @Override
            public void success(String json) {
                view.show(json);
            }

            @Override
            public void failed(String json) {
                view.show(json);
            }

            @Override
            public void isLegalOrNot(List<Object> list) {
                view.isLegalOrNot(list);
            }
        });
    }

    /**
     * 开始远程充电
     *
     * @param url
     * @param data
     */
    public void startCharging(final String url, final String data, final String notes) {
        view.showProgress();
        model.startCharging(url, data, new ChargingListener() {
            @Override
            public void success(String json) {
                Log.i("test", "开启充电返回值->" + json);
                QRActivity.nodes = null;
                view.stopProgress();//关闭弹出框
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int codeFlag = obj.get("code").getAsInt();
                switch (codeFlag) {
                    case 0://开启充电成功
                        view.show("开启充电成功！");
                        try {
                            String packageData = obj.get("packageData").getAsString();
                            model.addChargingInfo(packageData, context);//添加充电数据包
                            model.addNotes(notes, context);//添加充电参数
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        view.startCharging(obj);
                        break;
                    case 1://充电已经结束
                        view.show("充电已经结束！");
                        break;
                    case 2://socket连接失败
                        view.show("socket连接失败！");
                        break;
                    case 3://尚未绑定账户
                        view.show("尚未绑定账户！");
                        break;
                    case 4://其他人已经预约该电桩
                        view.show("其他人已经预约该电桩！");
                        break;
                    case 5://设备不可充
                    case Integer.MAX_VALUE://设备不可充
                        view.show("设备不可充！");
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
                    case 31://其他故障
                        view.show("其他故障！");
                        break;
                    case 32://余额不足
                        view.show("余额不足");
                        break;
                    default:
                        view.show("其他故障！");
                        break;
                }
            }

            @Override
            public void failed(String json) {
                view.stopProgress();
                view.show(json);
            }

            @Override
            public void isLegalOrNot(List<Object> list) {

            }
        });
    }

    /**
     * 停止远程充电
     *
     * @param url
     * @param phone
     * @param data
     */
    public void stopCharging(String url, String phone, String data) {
        view.showProgress();//关闭弹出框
        model.stopCharging(url, phone, data, new ChargingListener() {
            @Override
            public void success(String json) {
                Log.i("test", "结束充电返回值->" + json);
                view.stopProgress();//关闭弹出框
                QRActivity.nodes = null;//清楚充电参数
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int codeFlag = obj.get("code").getAsInt();
                ChargingDao chargingDao = ChargingDao.getInstance(context);
                switch (codeFlag) {
                    case 0://停止充电成功
                    case 1://充电已结束
                        view.show("停止充电成功！");
                        chargingDao.deleteCharging();
                        chargingDao.deleteNotes();
                        view.stopCharging();//设置“充电”与“结束充电“的转换
                        break;
                    case 2://socket连接失败！
                        view.show("socket连接失败！");
                        break;
                    case 6://直流充电准备中，提示用户排队等待
                        view.show("直流充电准备中，提示用户排队等待！");
                        break;
                    case 7://直流充电准备中，提示用户排队等待
                        view.show("网关通讯异常！");
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
                view.show(json);
                view.stopProgress();
            }

            @Override
            public void isLegalOrNot(List<Object> list) {

            }
        });
    }
}
