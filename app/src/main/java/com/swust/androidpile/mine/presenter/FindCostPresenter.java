package com.swust.androidpile.mine.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.swust.androidpile.mine.activity.ChargeCostActivity;
import com.swust.androidpile.mine.activity.CostActivity;
import com.swust.androidpile.mine.model.FindCostBiz;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.view.MineView;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class FindCostPresenter {
    private MineView view;
    private FindCostBiz model;
    private Context context;

    public void setViewAndModel(MineView view, FindCostBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 充电记录查询
     *
     * @param url
     * @param phone
     */
    public void findCost(String url, final String phone, final int count) {
        model.findCost(url, phone, count, new MineListener() {
            @Override
            public void success(String json) {
//                Log.i("test",json);
                view.activityFinish();//结束弹出框
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int flag = obj.get("flag").getAsInt();
                switch (flag) {
                    case 0:
                        view.show("尚未绑定账户");
                        break;
                    case 1:
                        view.show("无充电记录");
                        break;
                    case 2:
                        Intent intent = new Intent(context, CostActivity.class);
                        intent.putExtra("recode", obj.get("recode").getAsJsonArray().toString());
                        intent.putExtra("phone", phone);
                        context.startActivity(intent);
                        break;
                    case 3:
                        view.show(obj.get("recode").getAsJsonArray().toString());
                        break;
                    case 4:
                        view.noMoreRecode("我也是有底线的");
                    default:
                        break;
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
                view.activityFinish();//关闭弹出框
            }
        });
    }

    /**
     * 查询充值记录
     *
     * @param url
     * @param phone
     */
    public void findChargeCost(String url, String phone) {
        model.findChargeCost(url, phone, new MineListener() {
            @Override
            public void success(String json) {
                Log.i("test",json);
                view.activityFinish();//结束弹出框
                try {
                    JsonParser parser = new JsonParser();
                    JsonObject jsonObj = (JsonObject) parser.parse(json);
                    boolean flag = jsonObj.get("flag").getAsBoolean();
                    if (flag) {
                        Intent intent = new Intent(context, ChargeCostActivity.class);
                        intent.putExtra("recode", json);
                        context.startActivity(intent);
                    } else {
                        view.show("尚未充值！");
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
                view.activityFinish();//关闭弹出框
            }
        });
    }
}
