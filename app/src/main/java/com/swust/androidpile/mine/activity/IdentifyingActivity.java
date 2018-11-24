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
import com.swust.androidpile.mine.model.LoginBizImpl;
import com.swust.androidpile.mine.presenter.LoginPresenter;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IdentifyingActivity extends Activity implements OnClickListener, MineView {

    boolean flag1 = true;//获取验证码时用
    boolean flag2 = true;//获取验证码时用
    Handler handler;
    int index = 60;//获取验证码的计时器
    Button identify; //获取验证码按钮
    private LoginPresenter loginPresent = new LoginPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_identify);
        ActivityStorage.getMap().put(this.getClass().getName(), this);
        initPresent();
        initView();
        initListener();
        initHandler();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        identify = (Button) findViewById(R.id.identify);
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        identify.setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.sure).setOnClickListener(this);
    }

    /**
     * 初始化事件分发器
     */
    private void initPresent() {
        loginPresent.setViewAndModel(this, new LoginBizImpl(), this);
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
                        identify.setText("获取验证码");
                    } else {
                        identify.setText("" + index--);
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        EditText text1 = (EditText) findViewById(R.id.editText1);//手机号码
        EditText text2 = (EditText) findViewById(R.id.editText2);//验证码
        switch (v.getId()) {
            case R.id.back://点击“返回图标”监听
                finish();
                break;
            case R.id.sure://点击“确定”监听
                String sureUrl = Url.getInstance().getPath(9);
                String code = text2.getText().toString();
                String surePhone = text1.getText().toString();
                if (isLegalIdentify(code) && isLegalPhone(surePhone)) {
                    try {
                        loginPresent.loginIdentify(sureUrl, code, surePhone);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.showToast(this, "请输入正确的验证码或手机号码");
                }
                break;
            case R.id.identify://点击“获取验证码”监听
                if (flag1) {
                    String phone = text1.getText().toString();
                    if (isLegalPhone(phone)) {
                        calTime();
                        String url = Url.getInstance().getPath(7);
                        try {
                            loginPresent.loginCode(url, phone);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showToast(this, "请输入正确的手机号码！");
                    }
                }
                break;
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

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("IdentifyingActivity_identify");
        MyApplication.getHttpQueue().cancelAll("IdentifyingActivity_sure");
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
