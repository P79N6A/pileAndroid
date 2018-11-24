package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.swust.androidpile.R;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.FindCostBiz;
import com.swust.androidpile.mine.model.FindCostBizImpl;
import com.swust.androidpile.mine.presenter.FindCostPresenter;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.mine.view.SwipeRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CostActivity extends Activity implements MineView {

    private static final String TAG = "CostActivity";
    private String message;
    private ListView listView;
    private SimpleAdapter adapter;
    private List<Map<String, String>> listItem;
    private int count = 0;//请求页
    private boolean hasMore = true;
    private SwipeRefreshView swipeRefreshView;
    private String phone;
    private FindCostPresenter presenter;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_listview);
        initData();
        initView();
        initPresenter();
        initListener();
    }

    /**
     * 初始化适配器数据
     */
    private void initData() {
        url = Url.getInstance().getPath(11);//请求消费记录地址

        Intent intent = getIntent();
        String jsonstring = intent.getStringExtra("recode");
        phone = intent.getStringExtra("phone");
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(jsonstring);
        listItem = new ArrayList<Map<String, String>>();
        for (int i = 0; i < array.size(); i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("costTime", "开启  " + ((JsonObject) array.get(i)).get("costTime").getAsString());
            //            map.put("endTime", "结束  " + ((JsonObject) array.get(i)).get("endTime").getAsString());
            map.put("allTime", "时长  " + ((JsonObject) array.get(i)).get("allTime").getAsString());
            map.put("stationName", "地点  " + ((JsonObject) array.get(i)).get("stationName").getAsString()
                    + "  (" + ((JsonObject) array.get(i)).get("pileNumber").getAsString() + ")");
            map.put("cost", "金额  " + ((JsonObject) array.get(i)).get("cost").getAsFloat() + "元");
            map.put("port", "端口  " + ((JsonObject) array.get(i)).get("port").getAsString());
            if(((JsonObject) array.get(i)).get("acWast").getAsFloat() < 0.0 ||
                    ((JsonObject) array.get(i)).get("dcWast").getAsFloat() < 0.0){
                continue;
            }
            map.put("acWast", "交流电消耗  " + ((JsonObject) array.get(i)).get("acWast").getAsString());
            map.put("dcWast", "直流电消耗  " + ((JsonObject) array.get(i)).get("dcWast").getAsString());
            listItem.add(map);
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        swipeRefreshView = (SwipeRefreshView) findViewById(R.id.srl);
        listView = (ListView) findViewById(R.id.listView1);
        adapter = new SimpleAdapter(this, listItem, R.layout.view_mine_costadapter,
                new String[]{"costTime", "allTime", "stationName", "cost", "port", "acWast", "dcWast"},
                new int[]{R.id.costTime, R.id.allTime, R.id.stationName, R.id.cost, R.id.port, R.id.acWast, R.id.dcWast});
        listView.setAdapter(adapter);
    }

    /**
     * 初始化控制器
     */
    private void initPresenter() {
        presenter = new FindCostPresenter();
        FindCostBiz biz = new FindCostBizImpl();
        presenter.setViewAndModel(this, biz, this);
    }

    /**
     * 初始化控件监听器
     */
    public void initListener() {
        //给顶部导航栏设置返回监听
        findViewById(R.id.image1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        // （官方自带）下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                //记录上一次列表记录数
//                lastNum = listItem.size();
//                LogUtil.sop(TAG,"lastNum="+lastNum+" ,noNum="+noNum);
                if (hasMore) {
                    // TODO 获取数据
                    count++;
                    presenter.findCost(url, phone, count);//请求第count页数据
                } else {
                    swipeRefreshView.setRefreshing(false);
                    swipeRefreshView.setLoading(false);
                    ToastUtil.showToast(CostActivity.this, "我也是有底线的");
                }
//                if (noNum == lastNum) {
//                    //服务器那边没数据了，不要去请求了
//                    ToastUtil.showToast(CostActivity.this, "我也是有底线的");
//                    swipeRefreshView.setRefreshing(false);
//                } else {
//                    presenter.findCost(url, phone, count);//请求第count页数据
//                }
            }
        });


        // 设置上拉加载更多
        swipeRefreshView.setOnLoadListener(new SwipeRefreshView.OnLoadListener() {
            @Override
            public void onLoad() {
                /**
                 * 之所以用等待线程来做，是为了让用户可以看到底部等待进度条，否则进度条很快消失，因为网络请求很快
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        //记录上一次列表记录数
//                        lastNum = listItem.size();
                        if (hasMore) {
                            // TODO 获取数据
                            count++;
                            presenter.findCost(url, phone, count);//请求第count页数据
                        } else {
                            swipeRefreshView.setRefreshing(false);
                            swipeRefreshView.setLoading(false);
                            ToastUtil.showToast(CostActivity.this, "我也是有底线的");
                        }
//                        if (noNum == lastNum) {
//                            //服务器那边没数据了，不要去请求了
//                            ToastUtil.showToast(CostActivity.this, "我也是有底线的");
//                            swipeRefreshView.setLoading(false);
//                        } else {
//                            presenter.findCost(url, phone, count);//请求第count页数据
//                        }
                    }
                }, 1200);
            }
        });
    }

    @Override
    public void show(String str) {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(str);
//        JsonObject obj = (JsonObject) parser.parse(str);
//        int flag = obj.get("flag").getAsInt();
        try {
            for (int i = 0; i < array.size(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("costTime", "开启  " + ((JsonObject) array.get(i)).get("costTime").getAsString());
                //            map.put("endTime", "结束  " + ((JsonObject) array.get(i)).get("endTime").getAsString());
                map.put("allTime", "时长  " + ((JsonObject) array.get(i)).get("allTime").getAsString());
                map.put("stationName", "地点  " + ((JsonObject) array.get(i)).get("stationName").getAsString()
                        + "  (" + ((JsonObject) array.get(i)).get("pileNumber").getAsString() + ")");
                map.put("cost", "金额  " + ((JsonObject) array.get(i)).get("cost").getAsFloat() + "元");
                map.put("port", "端口  " + ((JsonObject) array.get(i)).get("port").getAsString());
                if(((JsonObject) array.get(i)).get("acWast").getAsFloat() < 0.0 ||
                        ((JsonObject) array.get(i)).get("dcWast").getAsFloat() < 0.0){
                    continue;
                }
                map.put("acWast", "交流电消耗  " + ((JsonObject) array.get(i)).get("acWast").getAsString());
                map.put("dcWast", "直流电消耗  " + ((JsonObject) array.get(i)).get("dcWast").getAsString());

                listItem.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        noNum = listItem.size();//记录当前列表记录数
        adapter.notifyDataSetChanged();//更新消费记录列表
        // 加载完数据设置为不刷新状态，将下、上拉进度收起来
        swipeRefreshView.setRefreshing(false);
        swipeRefreshView.setLoading(false);
    }

    @Override
    public void noMoreRecode(String str) {
        ToastUtil.showToast(this, str);
        swipeRefreshView.setRefreshing(false);
        swipeRefreshView.setLoading(false);
        hasMore = false;
    }

    @Override
    public void activityFinish() {

    }
}
