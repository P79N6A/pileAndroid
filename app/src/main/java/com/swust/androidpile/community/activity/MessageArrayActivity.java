package com.swust.androidpile.community.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.community.adapter.MessageArrayAdapter;
import com.swust.androidpile.main.NotificationActivity;
import com.swust.androidpile.mine.activity.LoginActivity;

public class MessageArrayActivity extends Activity implements MessageArrayAdapter.MyItemClickListener {

    private MessageArrayAdapter adapter;
    private JsonArray msgArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        JsonParser parser = new JsonParser();
        msgArray = parser.parse(getIntent().getStringExtra("msgArray")).getAsJsonArray();

        RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.msgRV);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);//设置方向
        myRecyclerView.setLayoutManager(manager);
        adapter = new MessageArrayAdapter(msgArray, this, this);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int postion) {
//        Log.i("test", "position--->" + postion);
        JsonObject msgObj = msgArray.get(postion).getAsJsonObject();
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.putExtra("title", msgObj.get("title").getAsString());
        intent.putExtra("content", msgObj.get("content").getAsString());
        startActivity(intent);
    }
}
