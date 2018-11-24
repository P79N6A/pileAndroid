package com.swust.androidpile.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.swust.androidpile.R;
import com.swust.androidpile.mine.adapter.SettingAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SettingActivity extends AppCompatActivity implements SettingAdapter.MyItemClickListener {

    private Intent intent;
    private String name;
    private String phone;
    private SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initData();
    }

    private void initData() {
        intent = getIntent();
        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
    }

    private void initView() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        int[] images = {R.mipmap.mine_safety, R.mipmap.mine_out};
        String[] contents = {"修改密码",  "退出登录"};
        for (int i = 0; i < images.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("imageId", images[i]);
            map.put("content", contents[i]);
            list.add(map);
        }

        RecyclerView settingRV = (RecyclerView) findViewById(R.id.settingRV);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);//设置方向
        settingRV.setLayoutManager(manager);
        adapter = new SettingAdapter(list, this);
        settingRV.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int postion) {
        switch (postion) {
            case 0:
                //修改密码
                mine_safety();
                break;
            case 1:
                //退出登录
                mine_out();
                break;
        }
    }

    /**
     * 修改密码
     */
    private void mine_safety() {
        Intent safety_intent = new Intent(this, ModifyPwdActivity.class);
        safety_intent.putExtra("name", name);
        safety_intent.putExtra("phone", phone);
        startActivity(safety_intent);
    }

    /**
     * 退出登录
     */
    private void mine_out() {
        try {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("退出登录?")
                    .setCancelText("No")
                    .setConfirmText("Yes")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            intent.putExtra("loginFlag", false);
                            setResult(0x103, intent);
                            sweetAlertDialog.cancel();
                            finish();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
