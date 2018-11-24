package com.swust.androidpile.mine.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.model.ModifyPwdBiz;
import com.swust.androidpile.mine.view.MineView;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class ModifyPwdPresenter {
    private MineView view;
    private ModifyPwdBiz model;
    private Context context;

    public void setViewAndModel(MineView view, ModifyPwdBiz model, Context context){
        this.view=view;
        this.model=model;
        this.context = context;
    }

    /**
     * 修改密码
     * @param url
     * @param phone
     * @param oldPwd
     * @param newPwd
     */
    public void modifyPwd(String url, final String phone, final String oldPwd, final String newPwd){
        model.modifyPwd(url,phone,oldPwd,newPwd,new MineListener(){
            @Override
            public void success(String json) {
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int flag = obj.get("flag").getAsInt();
                switch (flag) {
                    case 0:
                        view.show("尚未注册");
                        break;
                    case 1:
                        view.show("密码不正确");
                        break;
                    case 2:
                        view.activityFinish();
                        break;
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
            }
        });
    }

    /**
     * 忘记密码时修改密码
     * @param url
     * @param phone
     * @param newPwd
     */
    public void reModifyPwd(String url, final String phone, final String newPwd){
        model.reModifyPwd(url,phone,newPwd,new MineListener(){
            @Override
            public void success(String json) {
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int flag = obj.get("flag").getAsInt();
                switch (flag) {
                    case 0:
                        view.show("尚未注册");
                        break;
                    case 1:
                        view.show("密码不正确");
                        break;
                    case 2:
                        view.activityFinish();
                        break;
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
            }
        });
    }
}
