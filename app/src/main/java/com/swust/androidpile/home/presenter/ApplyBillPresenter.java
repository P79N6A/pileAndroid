package com.swust.androidpile.home.presenter;

import android.content.Context;
import android.util.Log;

import com.swust.androidpile.home.model.ApplyBillBiz;
import com.swust.androidpile.home.model.ApplyBillListener;
import com.swust.androidpile.home.view.ApplyBillView;

/**
 * Created by chen on 2017/4/30 0030.
 */

public class ApplyBillPresenter {

    private ApplyBillView view;
    private ApplyBillBiz model;
    private Context context;

    public void setViewAndModel(Context context, ApplyBillView view, ApplyBillBiz model) {
        this.context = context;
        this.view = view;
        this.model = model;
    }

    /**
     * 申请充电订单
     *
     * @param url
     */
    public void applyChargingBill(String url) {
        String phone = model.findUser(context).getPhone();
        model.applyChargingBill(url, phone, new ApplyBillListener() {
            @Override
            public void success(String str) {
                view.getChargingBillInfo(str);
            }

            @Override
            public void failed() {
                view.show("系统故障!");
            }
        });
    }

    /**
     * 申请充值订单
     *
     * @param url
     * @param money
     */
    public void applyMoneyBill(String url, final double money) {
        String phone = model.findUser(context).getPhone();
        model.applyMoneyBill(url, phone, money, new ApplyBillListener() {
            @Override
            public void success(String str) {
//                Log.i("test",str);
                view.getMoneyBillInfo(str);
            }

            @Override
            public void failed() {
//                Log.i("test","系统故障!");
                view.show("系统故障!");
            }
        });
    }

}
