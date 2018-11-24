package com.swust.androidpile.mine.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.mine.activity.ActivityStorage;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.model.RegisterBiz;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.utils.TokenUtil;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class RegisterPresenter {
    private MineView view;
    private RegisterBiz model;
    private Context context;

    /**
     * 初始化V层与M层
     *
     * @param view
     * @param model
     * @param context
     */
    public void setViewAndModel(MineView view, RegisterBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 请求注册
     *
     * @param url
     * @param code
     * @param phone
     * @param pwd
     * @param name
     */
    public void register(final String url, final String code, final String phone,
                         final String pwd, final String name) {
        model.register(url, code, phone, pwd, name, new MineListener() {
            @Override
            public void success(String json) {
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                switch (obj.get("flag").getAsInt()) {
                    case 0:
                        view.show("验证码不存在");
                        break;
                    case 1:
                        view.show("验证码时间过期");
                        break;
                    case 2:
                        view.show("验证码输入错误");
                        break;
                    case 3:
                        String name = obj.get("name").getAsString();//用户姓名
                        String token = obj.get("token").getAsString();//登录期限token
                        UserDao userDao = new UserDao(context);
                        User user = userDao.findUser();
                        if (user == null) {
                            userDao.addUser(new User(name, phone));
                        } else {
                            userDao.deleteUser(user);//有可能登录另外一个账号，但是手机只存一个账号的，所以要先删除再增加而非更新用户
                            userDao.addUser(new User(name, phone));
                        }
                        TokenUtil.updateToken(token, context);//更新token
                        ActivityStorage.exit();
                        break;
                    case 4:
                        view.show("该手机号已注册，请直接登陆！");
                        break;
                    case 5:
                        view.show("注册失败，新卡不足！");
                        break;
                }
            }

            @Override
            public void failed() {
                view.show("网络请求失败");
            }
        },context);
    }

    /**
     * 请求登录验证码
     */
    public void registerCode(final String url, final String phone) {
        model.registerCode(url, phone, new MineListener() {
            @Override
            public void success(String json) {
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                switch (obj.get("flag").getAsInt()) {
                    case 0:
                        view.show("验证码下发中");
                        break;
                    case 1:
                        view.show("获取验证码失败");
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
