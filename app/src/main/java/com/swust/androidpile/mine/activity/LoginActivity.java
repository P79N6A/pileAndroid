package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.LoginBizImpl;
import com.swust.androidpile.mine.model.LoginBiz;
import com.swust.androidpile.mine.presenter.LoginPresenter;
import com.swust.androidpile.mine.view.MineView;

public class LoginActivity extends Activity implements OnClickListener, MineView {

    private UserDao userDao;
    private User user;
    private LoginPresenter loginPresent = new LoginPresenter();
    private LoginBiz loginModel = new LoginBizImpl();
    private String phone;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine_login);
        initPresent();
        initListener();
        initView();
    }

    /**
     * 初始化事件分发器
     */
    private void initPresent() {
        loginPresent.setViewAndModel(this, loginModel, this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setResult(0x101, getIntent());    //设置返回的结果码，并返回调用该Activity的Activity
        userDao = new UserDao(this);
        user = userDao.findUser();
        EditText text1 = (EditText) findViewById(R.id.editText1);//手机号码
        //“手机号码”如果手机中有数据，则进行初始化
        if (user != null) {
            phone = user.getPhone();
            text1.setText(phone);
        }
    }

    /**
     * 初始化控件监听
     */
    private void initListener() {
        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.textView1).setOnClickListener(this);
        findViewById(R.id.textView2).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("LoginActivity");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.login://登陆
                EditText phoneET = (EditText) findViewById(R.id.editText1);
                EditText pwdET = (EditText) findViewById(R.id.editText2);
                phone = phoneET.getText().toString();//手机号码
                pwd = pwdET.getText().toString();//密码
                //手机号码必须为11位整数，密码不能低于6位且不能高于20位
                if ("".equals(phone) || "".equals(pwd)) {
                    ToastUtil.showToast(this, "请填写完整资料");
                } else if (phone.length() != 11) {
                    ToastUtil.showToast(this, "手机号码为11位整数");
                } else {
                    String url = Url.getInstance().getPath(6);
                    try {
                        loginPresent.login(url, phone, pwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.register://注册
                if (user == null) {
                    ActivityStorage.getMap().put(this.getClass().getName(), this);
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(this, "该手机号码已注册，请直接登录！");
                }
                break;
            case R.id.textView1://短信验证码登录
                LogUtil.sop("test", "短信验证码登录");
                ActivityStorage.getMap().put(this.getClass().getName(), this);
                Intent intent = new Intent(LoginActivity.this, IdentifyingActivity.class);
                startActivity(intent);
                break;
            case R.id.textView2://忘记密码
                ActivityStorage.getMap().put(this.getClass().getName(), this);
                Intent intent1 = new Intent(LoginActivity.this, FindPwdActivity.class);
                if (user != null)
                    intent1.putExtra("phone", user.getPhone());
                startActivity(intent1);
                break;
        }
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
    }

    @Override
    public void activityFinish() {
        //验证密码强度，更新软件后添加的
        if (pwd.length() < 6) {
            Toast.makeText(this, "密码强度低（长度低于6位！）", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(new Intent(this, ModifyPwdActivity.class));
            intent.putExtra("phone", phone);
            startActivity(intent);//前往更新密码
        }
        finish();//销毁该活动页
    }

    @Override
    public void noMoreRecode(String str) {

    }

}
