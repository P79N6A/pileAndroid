package com.swust.androidpile.mine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.main.*;
import com.swust.androidpile.main.ActivityStorage;

public class BillDetailActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView total_amount;
    private TextView timestamp;
    private TextView trade_no;
    private TextView out_trade_no;
    private ImageView backIV;
    private Button continueBT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_detail);
        init();
    }

    private void init() {
        initView();
        initListener();
        initData();
        ActivityStorage.getInstance().addActivity(this);
    }

    private void initListener() {
        backIV.setOnClickListener(this);
        continueBT.setOnClickListener(this);
    }

    private void initView() {
        total_amount = (TextView)findViewById(R.id.total_amount);
        timestamp = (TextView)findViewById(R.id.timestamp);
        trade_no = (TextView)findViewById(R.id.trade_no); 
        out_trade_no = (TextView)findViewById(R.id.out_trade_no);
        backIV = (ImageView)findViewById(R.id.backIV);
        continueBT = (Button)findViewById(R.id.continueBT);
    }


    private void initData() {
        Intent intent = getIntent();
        String jsonsttring = intent.getStringExtra("jsonstring");
        JsonParser parser = new JsonParser();
        JsonObject first = (JsonObject)parser.parse(jsonsttring);
        JsonObject second = first.get("alipay_trade_app_pay_response").getAsJsonObject();

        total_amount.setText(second.get("total_amount").getAsString());
        timestamp.setText(second.get("timestamp").getAsString());
        trade_no.setText(second.get("trade_no").getAsString());
        out_trade_no.setText(second.get("out_trade_no").getAsString());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIV://返回按钮，退出到余额显示页面
                com.swust.androidpile.main.ActivityStorage.getInstance().deleteAll();
                break;
            case R.id.continueBT:
                finish();//返回到上一个界面，即充值界面
                break;
        }
    }
}
