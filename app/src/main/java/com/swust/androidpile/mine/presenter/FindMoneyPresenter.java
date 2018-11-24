package com.swust.androidpile.mine.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.mine.model.FindMoneyBiz;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.view.MineView;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class FindMoneyPresenter {
    private MineView view;
    private FindMoneyBiz model;
    private Context context;

    public void setViewAndModel(MineView view, FindMoneyBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 请求余额查询
     *
     * @param url
     * @param phone
     */
    public void findMoney(String url, final String phone) {
        model.findMoney(url, phone, new MineListener() {
            @Override
            public void success(String json) {
                view.activityFinish();//结束弹出框
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                Log.i("test", obj.toString());
                if (obj.get("flag").getAsBoolean() == true) {
                    double money = obj.get("money").getAsDouble();
                    if (myMoney != null) {
                        myMoney.getMoney(money);
                    }
                } else {
                    view.show("尚未绑定账户");
                }
            }

            @Override
            public void failed() {
                view.activityFinish();//结束弹出框
                view.show("网络请求失败");
            }
        });
    }

    /**
     * 回调监听返回的余额值
     */
    private Money myMoney;

    public interface Money {
        public void getMoney(double m);
    }

    public void setMyMoney(Money myMoney) {
        this.myMoney = myMoney;
    }


}
