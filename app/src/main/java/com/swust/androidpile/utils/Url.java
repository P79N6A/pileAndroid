package com.swust.androidpile.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Url {

    private static ReentrantLock lock = new ReentrantLock();

    //设置类为单例类
    private static Url url;

    private Url() {
    }

    private String getEnvironment(String environment){
        Map<String,String> map = new HashMap<>();
        map.put("dev","http://192.168.1.7:8080/ChargingPile/");
        map.put("test","http://47.95.210.136:9080/ChargingPile/");
        map.put("pro","http://47.95.210.136:8080/ChargingPile/");
        return map.get(environment);
    }

    public static Url getInstance() {
        lock.lock();
        if (url == null) {
            url = new Url();
        }
        lock.unlock();
        return url;
    }

    //定义各种URL
    public String getPath(int path) {
        Map<Integer,String> mapSuffix = new HashMap<>();
        mapSuffix.put(0,"downloadapp/version.html");//更新检测
        mapSuffix.put(2,"queryAroundPile");//充电站查询
        mapSuffix.put(5,"message");//系统消息
        mapSuffix.put(6,"userLogin");//账户密码登录
        mapSuffix.put(7,"identify");//”注册/登录“申请验证码
        mapSuffix.put(8,"register");//注册按钮监听
        mapSuffix.put(9,"sure");//短信验证码登录
        mapSuffix.put(10,"userAdvice");//我的建议
        mapSuffix.put(11,"findCost");//充电记录查询
        mapSuffix.put(12,"findMoney");//余额查询
        mapSuffix.put(13,"modifyPwd");//修改密码
        mapSuffix.put(15,"scan");//发送开始充电请求
        mapSuffix.put(16,"reModifyPwd");//忘记密码时重新更新密码
        mapSuffix.put(17,"applyChargingBill");//申请充电订单信息
        mapSuffix.put(18,"applyMoneyBill");//申请充值订单信息
        mapSuffix.put(19,"schedule");//充电进度查询
        mapSuffix.put(20,"stopCharging");//停止充电指令（以后唯一的调用接口）
        mapSuffix.put(21,"findChargeCost");//充值记录查询
        Log.i("test",getEnvironment(Configure.URLFLAG) + mapSuffix.get(path));
        return getEnvironment(Configure.URLFLAG) + mapSuffix.get(path);
        //远程环境
//        if (Configure.URLFLAG == "pro") {
//            switch (path) {
//                case 0://更新检测
//                    return "http://47.95.210.136:8080/ChargingPile/downloadapp/version.html";
//                case 2://充电站查询
//                    return "http://47.95.210.136:8080/ChargingPile/queryAroundPile";
//                case 5://系统消息
//                    return "http://47.95.210.136:8080/ChargingPile/message";
//                case 6://账户密码登录
//                    return "http://47.95.210.136:8080/ChargingPile/userLogin";
//                case 7://”注册/登录“申请验证码
//                    return "http://47.95.210.136:8080/ChargingPile/identify";
//                case 8://注册按钮监听
//                    return "http://47.95.210.136:8080/ChargingPile/register";
//                case 9://短信验证码登录
//                    return "http://47.95.210.136:8080/ChargingPile/sure";
//                case 10://我的建议
//                    return "http://47.95.210.136:8080/ChargingPile/userAdvice";
//                case 11://充电记录查询
//                    return "http://47.95.210.136:8080/ChargingPile/findCost";
//                case 12://余额查询
//                    return "http://47.95.210.136:8080/ChargingPile/findMoney";
//                case 13://修改密码
//                    return "http://47.95.210.136:8080/ChargingPile/modifyPwd";
//                case 15://发送开始充电请求
//                    return "http://47.95.210.136:8080/ChargingPile/scan";
//                case 16://忘记密码时重新更新密码
//                    return "http://47.95.210.136:8080/ChargingPile/reModifyPwd";
//                case 17://申请充电订单信息
//                    return "http://47.95.210.136:8080/ChargingPile/applyChargingBill";
//                case 18://申请充值订单信息
//                    return "http://47.95.210.136:8080/ChargingPile/applyMoneyBill";
//                case 19://充电进度查询
//                    return "http://47.95.210.136:8080/ChargingPile/schedule";
//                case 20://停止充电指令（以后唯一的调用接口）
//                    return "http://47.95.210.136:8080/ChargingPile/stopCharging";
//                case 21://充值记录查询
//                    return "http://47.95.210.136:8080/ChargingPile/findChargeCost";
//
//                case 22://附近私有电桩查询
//                    return "http://192.168.124.24:8080/AHPWeb/orderInfo";
//                case 23://预约
//                    return "http://192.168.124.24:8080/AHPWeb/order";
//                case 24://预约记录查询
//                    return "http://192.168.124.24:8080/AHPWeb/orderRecode";
//                case 25://商户查询电桩
//                    return "http://192.168.124.24:8080/AHPWeb/orderUpdateInfo";
//                case 26://商户更新卡状态
//                    return "http://192.168.124.24:8080/AHPWeb/orderUpdate";
//                default:
//                    break;
//            }
//        } else {//测试环境
//            switch (path) {
//                case 0://更新检测
//                    return "http://192.168.124.24:8080/downloadapp/version.html";
//                case 2://充电站查询
//                    return "http://192.168.124.24:8080/ChargingPile/queryAroundPile";
//                case 5://系统消息
//                    return "http://192.168.124.24:8080/ChargingPile/message";
//                case 6://账户密码登录
//                    return "http://192.168.124.24:8080/ChargingPile/userLogin";
//                case 7://”注册/登录“申请验证码
//                    return "http://192.168.124.24:8080/ChargingPile/identify";
//                case 8://注册按钮监听
//                    return "http://192.168.124.24:8080/ChargingPile/register";
//                case 9://短信验证码登录
//                    return "http://192.168.124.24:8080/ChargingPile/sure";
//                case 10://我的建议
//                    return "http://192.168.124.24:8080/ChargingPile/userAdvice";
//                case 11://充电记录查询
//                    return "http://192.168.124.24:8080/ChargingPile/findCost";
//                case 12://余额查询
//                    return "http://192.168.124.24:8080/ChargingPile/findMoney";
//                case 13://修改密码
//                    return "http://192.168.124.24:8080/ChargingPile/modifyPwd";
//                case 15://发送开始充电请求
//                    return "http://192.168.124.24:8080/ChargingPile/scan";
//                case 16://忘记密码时重新更新密码
//                    return "http://192.168.124.24:8080/ChargingPile/reModifyPwd";
//                case 17://申请充电订单信息
//                    return "http://192.168.124.24:8080/ChargingPile/applyChargingBill";
//                case 18://申请充值订单信息
//                    return "http://192.168.124.24:8080/ChargingPile/applyMoneyBill";
//                case 19://轮询充电进度查询
//                    return "http://192.168.124.24:8080/ChargingPile/schedule";
//                case 20://充电进度页面关闭充电
//                    return "http://192.168.124.24:8080/ChargingPile/stopCharging";
//                case 21://充值记录查询
//                    return "http://192.168.124.24:8080/ChargingPile/findChargeCost";
//
//                case 22://附近私有电桩查询
//                    return "http://192.168.124.24:8080/AHPWeb/orderInfo";
//                case 23://预约
//                    return "http://192.168.124.24:8080/AHPWeb/order";
//                case 24://预约记录查询
//                    return "http://192.168.124.24:8080/AHPWeb/orderRecode";
//                case 25://商户查询电桩
//                    return "http://192.168.124.24:8080/AHPWeb/orderUpdateInfo";
//                case 26://商户更新卡状态
//                    return "http://192.168.124.24:8080/AHPWeb/orderUpdate";
//                case 27:
//                    return "http://192.168.124.24:8080/ChargingPile/nativeStations";
//                default:
//                    break;
//            }
//        }
//        return null;
    }
}
