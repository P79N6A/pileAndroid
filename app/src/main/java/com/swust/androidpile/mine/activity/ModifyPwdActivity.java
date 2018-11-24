package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.swust.androidpile.R;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.ModifyPwdBizImpl;
import com.swust.androidpile.mine.model.ModifyPwdBiz;
import com.swust.androidpile.mine.presenter.ModifyPwdPresenter;
import com.swust.androidpile.mine.view.MineView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModifyPwdActivity extends Activity implements OnClickListener, MineView {

    private EditText oldPwdET;
    private EditText newPwdET;
    private EditText identifyPwdET;
    private String oldPwd;
    private String newPwd;
    private String identifyPwd;
    private String phone;
    private String name;
    private ModifyPwdPresenter modifyPwdPresenter = new ModifyPwdPresenter();
    private ModifyPwdBiz modyfyPwdModel = new ModifyPwdBizImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_modifypassword);
        initPresenter();
        initView();
        initListener();
    }

    /**
     * 初始化事件分发器
     */
    private void initPresenter() {
        modifyPwdPresenter.setViewAndModel(this, modyfyPwdModel, this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        oldPwdET = (EditText) findViewById(R.id.editText3);//旧密码
        newPwdET = (EditText) findViewById(R.id.editText4);//新密码
        identifyPwdET = (EditText) findViewById(R.id.editText5);//确认密码
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        findViewById(R.id.sure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure:
                initData();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化数据并正则表达式确认，通过后向服务器发送请求
     */
    private void initData() {
        oldPwd = oldPwdET.getText().toString();//旧密码
        newPwd = newPwdET.getText().toString();//新密码
        identifyPwd = identifyPwdET.getText().toString();//确认密码
        if ("".equals(oldPwd) || "".equals(newPwd) || "".equals(identifyPwd)) {
            ToastUtil.showToast(this, "请填写完整内容");
        } else if (oldPwd.length() < 6 || newPwd.length() < 6 || identifyPwd.length() < 6) {
            ToastUtil.showToast(this, "密码长度不得低于6位");
        } else if (oldPwd.length() > 20 || newPwd.length() > 20 || identifyPwd.length() > 20) {
            ToastUtil.showToast(this, "密码长度不得高于6位");
        } else if (oldPwd.equals(newPwd)) {
            ToastUtil.showToast(this, "新密码与旧密码不能一致");
        } else if (!newPwd.equals(identifyPwd)) {
            ToastUtil.showToast(this, "新密码与确认密码不一致");
        } else {
            //发起更改密码请求
            String url = Url.getInstance().getPath(13);
            modifyPwdPresenter.modifyPwd(url, phone, oldPwd, newPwd);
        }
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
    }

    @Override
    public void activityFinish() {
        finish();
    }

    @Override
    public void noMoreRecode(String str) {

    }
}
