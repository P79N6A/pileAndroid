package com.swust.androidpile.mine.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.swust.androidpile.R;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.ModifyPwdBiz;
import com.swust.androidpile.mine.model.ModifyPwdBizImpl;
import com.swust.androidpile.mine.presenter.ModifyPwdPresenter;
import com.swust.androidpile.mine.view.MineView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReModifyPwdActivity extends AppCompatActivity implements MineView {

    private String phone;
    private EditText newPwdET;
    private EditText identifyPwdET;
    private Button sure;
    private String newPwd;
    private String identifyPwd;
    private ModifyPwdPresenter modifyPwdPresenter;
    private ModifyPwdBiz modyfyPwdModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_remodify_pwd);
        ActivityStorage.getMap().put(this.getClass().getName(), this);
        initPresenter();
        initView();
        initListener();
    }

    /**
     * 初始化事件分发器
     */
    private void initPresenter() {
        modifyPwdPresenter = new ModifyPwdPresenter();
        modyfyPwdModel = new ModifyPwdBizImpl();
        modifyPwdPresenter.setViewAndModel(this, modyfyPwdModel, this);
    }

    private void initView() {
        phone = getIntent().getStringExtra("phone");
        newPwdET = (EditText) findViewById(R.id.editText3);//新密码
        identifyPwdET = (EditText) findViewById(R.id.editText4);//确认密码
        sure = (Button) findViewById(R.id.sure);
    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
    }

    /**
     * 初始化数据并正则表达式确认，通过后向服务器发送请求
     */
    private void initData() {
        newPwd = newPwdET.getText().toString();//新密码
        identifyPwd = identifyPwdET.getText().toString();///确认密码
        if("".equals(newPwd) || "".equals(identifyPwd)){
            ToastUtil.showToast(this, "请填写完整内容");
        }else if(!newPwd.equals(identifyPwd)){
            ToastUtil.showToast(this, "新密码与确认密码不一致");
        }else if(newPwd.length()<6 || newPwd.length()>20){
            ToastUtil.showToast(this, "密码长度不得低于6位且不能高于20位");
        }else{
            String url = Url.getInstance().getPath(16);
            modifyPwdPresenter.reModifyPwd(url, phone, newPwd);
        }
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
    }

    @Override
    public void activityFinish() {
        ActivityStorage.exit();
    }

    @Override
    public void noMoreRecode(String str) {

    }
}
