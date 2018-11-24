package com.swust.androidpile.mine.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.mine.activity.ActivityStorage;
import com.swust.androidpile.mine.fragment.MineFragment;
import com.swust.androidpile.mine.model.LoginBiz;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.TokenUtil;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public class LoginPresenter {
    private MineView view;
    private LoginBiz model;
    private Context context;

    /**
     * 初始化V层与M层
     *
     * @param view
     * @param model
     * @param context
     */
    public void setViewAndModel(MineView view, LoginBiz model, Context context) {
        this.view = view;
        this.model = model;
        this.context = context;
    }

    /**
     * 短信验证码登录
     *
     * @param url
     * @param code
     * @param phone
     */
    public void loginIdentify(final String url, final String code, final String phone) {
        model.loginIdentify(url, code, phone, new MineListener() {
            @Override
            public void success(String json) {
//                Log.i("test",json);
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
//                        LogUtil.sop("test", "phone=" + phone);
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
                        ActivityStorage.exit();//退出所有活动窗口
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
     * 请求登录验证码
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

    /**
     * 账号密码登录
     *
     * @param url
     * @param phone
     * @param password
     */
    public void login(final String url, final String phone, final String password) {
        model.login(context, url, phone, password, new MineListener() {
            @Override
            public void success(String json) {
                LogUtil.sop("test", "--->json");
                JsonParser parser = new JsonParser();
                JsonObject obj = (JsonObject) parser.parse(json);
                int flag = obj.get("flag").getAsInt();
                switch (flag) {
                    case 0://尚未注册
                        view.show("尚未注册");
                        break;
                    case 1://密码不正确
                        view.show("密码错误，请重新输入");
                        break;
                    case 2://登陆成功
                        UserDao userDao = new UserDao(context);
                        User user = userDao.findUser();
                        String name = obj.get("name").getAsString();
                        String token = obj.get("token").getAsString();
                        if (user == null) {
                            userDao.addUser(new User(name, phone));
                        } else {
                            //有可能登录另外一个账号，但是手机只存一个账号的，所以要先删除再增加而非更新用户
                            userDao.deleteUser(user);
                            userDao.addUser(new User(name, phone));
                        }
                        //更新token
                        TokenUtil.updateToken(token, context);
                        //更新MineFramgent用户名
                        if (MineFragment.MINEFRAGMENT_NAMETV != null) {
                            MineFragment.MINEFRAGMENT_NAMETV.setText(name);
                            MineFragment.MINEFRAGMENT_NAMETV.setTextColor(context.getResources().getColor(R.color.white));
                        }
                        //视图渲染
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
