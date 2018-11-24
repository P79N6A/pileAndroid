package com.swust.androidpile.qr.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.qr.activity.QRActivity;
import com.swust.androidpile.qr.model.QRBiz;
import com.swust.androidpile.qr.model.QRListener;
import com.swust.androidpile.qr.view.QRView;

/**
 * Created by Administrator on 2017/5/1 0001.
 */

public class QRPresenter {

    private QRBiz qrBiz;
    private QRView qrView;
    private Context context;

    public QRPresenter(Context context, QRView qrView, QRBiz qrBiz) {
        this.context = context;
        this.qrView = qrView;
        this.qrBiz = qrBiz;
    }

    /**
     * 开启充电请求
     *
     * @param url
     * @param data
     */
    public void scan(String url, String data, final String notes) {
        qrView.showProgress();//开启进度条
        qrBiz.scan(url, data, new QRListener() {
            @Override
            public void success(String jsonstring) {
                qrView.stopProgress();//关闭弹出框
                QRActivity.nodes = null;
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(jsonstring);
                int codeFlag = obj.get("code").getAsInt();
                switch (codeFlag) {
                    case 0://开启充电成功
                        qrView.show("开启充电成功！");
                        try {
                            String packageData = obj.get("packageData").getAsString();
                            qrBiz.addChargingInfo(packageData, context);//添加充电数据包
                            qrBiz.addNotes(notes, context);//添加充电参数
                            QRActivity.nodes = notes;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        qrView.scan_success();
                        break;
                    case 1://充电已经结束
                        qrView.show("充电已经结束！");
                        break;
                    case 2://socket连接失败
                        qrView.show("socket连接失败！");
                        break;
                    case 3://尚未绑定账户
                        qrView.show("尚未绑定账户！");
                        break;
                    case 4://其他人已经预约该电桩
                        qrView.show("其他人已经预约该电桩！");
                        break;
                    case 5://设备不可充
                        qrView.show("设备不可充！");
                        break;
                    case 10://充电口已占用
                        qrView.show("充电口已占用！");
                        break;
                    case 11://电表通讯故障
                        qrView.show("电表通讯故障！");
                        break;
                    case 13://电缆故障
                        qrView.show("电缆故障！");
                        break;
                    case 14://接触器故障
                        qrView.show("接触器故障！");
                        break;
                    case 15://电缆故障
                        qrView.show("绝缘故障！");
                        break;
                    case 16://BMS通信故障
                        qrView.show("BMS通信故障！");
                        break;
                    case 31://其他故障
                        qrView.show("其他故障！");
                        break;
                    case 32://余额不足
                        qrView.show("余额不足！");
                        break;
                    default:
                        qrView.show("其他故障！");
                        break;
                }
            }

            @Override
            public void failed() {
                qrView.show("网络请求失败！");
                qrView.stopProgress();
            }
        });
    }

    /**
     * 停止远程充电
     *
     * @param url
     * @param data
     */
    public void stop_scan(String url, String phone, String data) {
        qrView.showProgress();
        qrBiz.stop_scan(url, phone, data, new QRListener() {
            @Override
            public void success(String jsonstring) {
                qrView.stopProgress();
                QRActivity.nodes = null;//清除充电参数
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(jsonstring);
                int codeFlag = obj.get("code").getAsInt();
                ChargingDao chargingDao = ChargingDao.getInstance(context);
                switch (codeFlag) {
                    case 0://停止充电成功
                    case 1://充电已结束
                        qrView.show("停止充电成功！");
                        qrView.stop_success();
                        chargingDao.deleteCharging();
                        chargingDao.deleteNotes();
                        break;
                    case 2://socket连接失败！
                        qrView.show("socket连接失败！");
                        break;
                    case 10://充电口已占用
                        qrView.show("充电口已占用！");
                        break;
                    case 11://电表通讯故障
                        qrView.show("电表通讯故障！");
                        break;
                    case 13://电缆故障
                        qrView.show("电缆故障！");
                        break;
                    case 14://接触器故障
                        qrView.show("接触器故障！");
                        break;
                    case 15://绝缘故障
                        qrView.show("绝缘故障！");
                        break;
                    case 16://BMS通信故障
                        qrView.show("BMS通信故障！");
                        break;
                    case 31://其他故障
                        qrView.show("其他故障！");
                        break;
                    default:
                        qrView.show("其他故障！");
                        break;
                }
            }

            @Override
            public void failed() {
                qrView.stopProgress();
                qrView.show("网络请求失败！");
            }
        });
    }


}
