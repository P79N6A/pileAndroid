package com.swust.androidpile.mine.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChargeCostActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private List<Map<String, String>> listItem;
    private SimpleAdapter adapter;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge_cost);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        initData();
        initView();
        initListener();
    }

    private void initListener() {
        backIV.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        String jsonstring = intent.getStringExtra("recode");
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = (JsonObject) parser.parse(jsonstring);
        JsonArray array = jsonObj.get("trade").getAsJsonArray();
        listItem = new ArrayList<Map<String, String>>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("notify_time", "时间  " + ((JsonObject) array.get(i)).get("notify_time").getAsString());
            map.put("total_amount", "金额  " + ((JsonObject) array.get(i)).get("total_amount").getAsString() + "元");
            map.put("subject", "商品名  " + ((JsonObject) array.get(i)).get("subject").getAsString());
            map.put("trade_no", "订单号  " + ((JsonObject) array.get(i)).get("trade_no").getAsString());
            listItem.add(map);
        }
    }

    private void initView() {
        //返回列表
        backIV = (ImageView) findViewById(R.id.backIV);
        //充值记录列表
        listView = (ListView) findViewById(R.id.listView1);
        adapter = new SimpleAdapter(this, listItem, R.layout.view_mine_chargecostadapter,
                new String[]{"notify_time", "total_amount", "subject", "trade_no"},
                new int[]{R.id.notify_time, R.id.total_amount, R.id.subject, R.id.trade_no});
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIV:
                finish();
                break;
        }
    }
}
