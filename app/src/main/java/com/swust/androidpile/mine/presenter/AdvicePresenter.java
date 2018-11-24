package com.swust.androidpile.mine.presenter;

import android.content.Context;

import com.swust.androidpile.mine.model.AdviceBiz;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.view.MineView;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class AdvicePresenter {
    private MineView view;
    private AdviceBiz model;
    private Context context;

    public void setViewAndModel(MineView view, AdviceBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 修改密码
     *
     * @param url
     * @param phone
     * @param advice
     */
    public void mineAdvice(String url, final String phone, final String advice) {
        model.mineAdvice(url, phone, advice, new MineListener() {
            @Override
            public void success(String json) {
                if (json.equals("0")) {
                    view.show("提交成功");
                } else {
                    view.show("提交失败");
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
            }
        });
    }
}
