package com.swust.androidpile.wxapi;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.wxpay.*;
import com.tencent.mm.opensdk.constants.*;
import com.tencent.mm.opensdk.modelbase.*;
import com.tencent.mm.opensdk.modelmsg.*;
import com.tencent.mm.opensdk.openapi.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler, View.OnClickListener {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private TextView total_amount;
    private TextView timestamp;
    private TextView out_trade_no;
    private ImageView backIV;

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
//        Log.i("test", "onCreate");
        init();
    }

    private void init() {
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);

        initView();
        initListener();
    }

    private void initView() {
        total_amount = (TextView) findViewById(R.id.total_amount);
        timestamp = (TextView) findViewById(R.id.timestamp);
        out_trade_no = (TextView) findViewById(R.id.out_trade_no);
        backIV = (ImageView) findViewById(R.id.backIV);
    }

    private void initListener() {
        backIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIV://返回按钮，退出到余额显示页面
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        setIntent(intent);
//        api.handleIntent(intent, this);
//        Log.i("test", "onNewIntent");
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.i("test", "微信支付结果, errCode = " + resp.errCode);
        PayActivity.orderJson.addProperty("errCode", resp.errCode);
        if (resp.errCode != 0) {
            finish();
        } else {
//            Log.i("test","total_amount="+PayActivity.orderJson.get("money").getAsString()+"元");
            total_amount.setText(PayActivity.orderJson.get("money").getAsString() + "元");
            out_trade_no.setText(PayActivity.orderJson.get("out_trade_no").getAsString());

            String regex = "yyyy-MM-dd HH:mm:ss";
            Date date = new Date(Long.parseLong(PayActivity.orderJson.get("timestamp").getAsString()) * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat(regex);
            timestamp.setText(formatter.format(date));
        }
    }

    @Override
    public void finish() {
        super.finish();
//        Log.i("test", "WXPayEntryActivity is finishing");
//        ActivityStorage.getInstance().deleteAll();
    }
}