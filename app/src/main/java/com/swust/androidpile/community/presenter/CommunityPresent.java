package com.swust.androidpile.community.presenter;

import android.content.Context;
import android.util.Log;

import com.swust.androidpile.community.model.SystemMsgBizImpl;
import com.swust.androidpile.community.model.SystemMsgListener;
import com.swust.androidpile.community.model.SystemMsgBiz;
import com.swust.androidpile.community.view.CommunityView;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class CommunityPresent {

    SystemMsgBizImpl model;
    CommunityView view;
    Context context;

    public void setViewAndModel(CommunityView view, SystemMsgBizImpl model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 请求获取系统消息
     *
     * @param url
     */
    public void findMsg(final String url) {
        model.findMsg(url, new SystemMsgListener() {
            @Override
            public void success(String json) {
//                Log.i("test",json);
                view.success(json);
            }

            @Override
            public void failed() {
                view.fail();
            }
        });
    }

    /**
     * 获取用户电话号码
     *
     * @param context
     */
    public void getPhone(Context context) {
        view.getPhone(model.getPhone(context));
    }

//    public void initData(List<Map<String, Object>> listItems) {
//        listItems = model.initData(listItems);
//        view.initData(listItems);
//    }

}
