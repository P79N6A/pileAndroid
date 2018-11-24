package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.swust.androidpile.R;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.RegisterBizImpl;
import com.swust.androidpile.mine.model.RegisterBiz;
import com.swust.androidpile.mine.presenter.RegisterPresenter;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity implements OnClickListener, MineView {

    Handler handler;
    Button identify; //获取验证码
    int index = 60;//获取验证码的计时器
    boolean flag1 = true;//获取验证码时用
    boolean flag2 = true;//获取验证码时用
    private EditText nameET;//用户名文本框
    private EditText pwdET;//密码文本框
    private EditText codeET;//验证码文本框
    private EditText phoneET;//电话号码文本框
    private RegisterPresenter registerPresent = new RegisterPresenter();
    private RegisterBiz registerModel = new RegisterBizImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_registerfragment);
        ActivityStorage.getMap().put(this.getClass().getName(), this);
        initPresenter();
        initView();
        initListener();
        initHandler();
    }

    /**
     * 初始化事件分发器
     */
    private void initPresenter() {
        registerPresent.setViewAndModel(this, registerModel, this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        identify = (Button) findViewById(R.id.identify);
        //用户名
        nameET = (EditText) findViewById(R.id.editText1);
        //密码
        pwdET = (EditText) findViewById(R.id.editText2);
        //验证码
        codeET = (EditText) findViewById(R.id.editText3);
        //电话号码
        phoneET = (EditText) findViewById(R.id.editText4);
    }

    /**
     * 初始化控件监听
     */
    private void initListener() {
        identify.setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
    }

    /**
     * 初始化计时器
     */
    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x101) {
                    if (index == 0) {
                        flag1 = true;
                        flag2 = false;
                        index = 60;
                        identify.setBackgroundColor(getResources().getColor(R.color.main));
                        identify.setText("获取验证码");
                    } else {
                        identify.setText("" + index--);
                    }
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("RegisterActivity_identify");
        MyApplication.getHttpQueue().cancelAll("RegisterActivity_register");
    }

    @Override
    public void onClick(View v) {
        String code, phone, pwd, name;
        switch (v.getId()) {
            case R.id.back://点击“返回图标”监听
                finish();
                break;
            case R.id.identify://点击“获取验证码”监听
                phone = phoneET.getText().toString();
                if (flag1) {
                    if (isLegalPhone(phone)) {
                        calTime();//验证码60秒开始倒数
                        try {
                            String url = Url.getInstance().getPath(7);
                            registerPresent.registerCode(url, phone);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showToast(this, "请输入11位手机号码");
                    }
                }
                break;
            case R.id.register://点击"注册"监听
                name = nameET.getText().toString();
                pwd = pwdET.getText().toString();
                code = codeET.getText().toString();
                phone = phoneET.getText().toString();
                if ("".equals(name) || "".equals(pwd)) {
                    ToastUtil.showToast(this, "用户名或密码不可为空");
                } else if (name.length() > 20) {
                    ToastUtil.showToast(this, "用户名长度不得高于20位");
                } else if (pwd.length() < 6 || pwd.length() > 20) {
                    ToastUtil.showToast(this, "密码长度6-20位");
                } else if (!isLegalPhone(phone)) {
                    ToastUtil.showToast(this, "手机号码为11位整数");
                } else if (!isLegalIdentify(code)) {
                    ToastUtil.showToast(this, "验证码为6位整数");
                } else {
                    try {
                        String url = Url.getInstance().getPath(8);
                        registerPresent.register(url, code, phone, pwd, name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    /**
     * 验证码倒计时60秒设置
     */
    private void calTime() {
        try {
            flag1 = false;
            identify.setBackgroundColor(getResources().getColor(R.color.identify));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (flag2) {
                        Message m = handler.obtainMessage(); // 获取一个Message
                        m.what = 0x101;
                        handler.sendMessage(m); // 发送消息
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    flag2 = true;
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证手机号码是否正确
     *
     * @param str 手机号码
     * @return
     */
    public boolean isLegalPhone(String str) {
        Pattern p = Pattern.compile("(\\d{11})");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 验证验证码是否正确
     *
     * @param str 验证码
     * @return
     */
    public boolean isLegalIdentify(String str) {
        Pattern p = Pattern.compile("(\\d{6})");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
    }

    @Override
    public void activityFinish() {

    }

    @Override
    public void noMoreRecode(String str) {

    }
}
