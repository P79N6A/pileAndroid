package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.swust.androidpile.R;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.AdviceBizImpl;
import com.swust.androidpile.mine.model.AdviceBiz;
import com.swust.androidpile.mine.presenter.AdvicePresenter;
import com.swust.androidpile.mine.view.MineView;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的建议
 *
 * @author Administrator
 */
public class AdviceActivity extends Activity implements OnClickListener, MineView {

    private List<String> adviceList = new ArrayList<String>();
    private AdvicePresenter advicePresenter = new AdvicePresenter();
    private AdviceBiz mineAdviceModel = new AdviceBizImpl();
    private EditText ac;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_advice);
        initPresenter();
        initView();
        initListener();
    }

    /**
     * 初始化事件分发器
     */
    private void initPresenter() {
        advicePresenter.setViewAndModel(this, mineAdviceModel, this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ac = (EditText) findViewById(R.id.advice_content);
        phone = getIntent().getStringExtra("phone");
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        findViewById(R.id.image1).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1://顶部导航栏返回图标监听
                finish();
                break;
            case R.id.submit://提交按钮监听
                final String advice_content = ac.getText().toString();
                if (advice_content.equals("")) {
                    ToastUtil.showToast(AdviceActivity.this, "请填写建议内容！");
                } else if (advice_content.length() > 200) {
                    ToastUtil.showToast(AdviceActivity.this, "建议内容字数超过200！");
                } else if (adviceList.contains(advice_content)) {
                    ToastUtil.showToast(AdviceActivity.this, "请勿重复提交建议内容！");
                } else {
                    String url = Url.getInstance().getPath(10);
                    advicePresenter.mineAdvice(url, phone, advice_content);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("MineAdviceActivity");
    }


    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
        adviceList.add(ac.getText().toString());
        finish();
    }

    @Override
    public void activityFinish() {
        finish();
    }

    @Override
    public void noMoreRecode(String str) {

    }
}
