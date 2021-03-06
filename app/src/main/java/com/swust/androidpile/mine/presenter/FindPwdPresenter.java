package com.swust.androidpile.mine.presenter;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.mine.fragment.MineFragment;
import com.swust.androidpile.mine.model.LoginBiz;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.view.FindPwdView;
import com.swust.androidpile.utils.TokenUtil;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public class FindPwdPresenter {
    private FindPwdView view;
    private LoginBiz model;
    private Context context;

    /**
     * 初始化V层与M层
     *
     * @param view
     * @param model
     * @param context
     */
    public void setViewAndModel(FindPwdView view, LoginBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 验证码验证
     *
     * @param url
     * @param code
     * @param phone
     */
    public void loginIdentify(final String url, final String code, final String phone) {
        model.loginIdentify(url, code, phone, new MineListener() {
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
                    case 3://登录成功
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
                        //更新token
                        TokenUtil.updateToken(token, context);//更新token
                        //更新MineFramgent用户名
                        MineFragment.MINEFRAGMENT_NAMETV.setText(name);
                        MineFragment.MINEFRAGMENT_NAMETV.setTextColor(context.getResources().getColor(R.color.white));
                        //跳转到重修修改密码页面
                        view.jump();
                        break;
                    case 4:
                        view.show("该手机号尚未注册");
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
     * 请求验证码
     */
    public void loginCode(final String url, final String phone) {
        model.loginCode(url, phone, new MineListener() {
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
