package com.swust.androidpile.home.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.home.citypiles.CityPilesActivity;
import com.swust.androidpile.home.fragment.ListFragmentItem;
import com.swust.androidpile.home.fragment.NativeStationFragment;
import com.swust.androidpile.home.fragment.QueryAroundFragment;
import com.swust.androidpile.home.model.QueryAroundBiz;
import com.swust.androidpile.home.model.QueryAroundBizImpl;
import com.swust.androidpile.home.presenter.QueryAroundPresenter;
import com.swust.androidpile.home.view.QueryAroundView;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 电桩查询首页
 */
public class QueryActivity extends Activity implements View.OnClickListener, QueryAroundView {

    private static final String TAG = "QueryActivity";
    public QueryAroundFragment qf = new QueryAroundFragment();//查询所有电桩信息的主类
    private Button updateBT;
    private ListFragmentItem item;
    private QueryAroundPresenter queryAroundPresenter;
    private String otherCity;
    private SweetAlertDialog pDialog;
    private TextView input_edittext;//城市名TextView
    private TextView detailTV;//列表TextView
    private QueryAroundBizImpl queryAroundModel = new QueryAroundBizImpl();
    private boolean hasNetwork = true;
    private JsonArray stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_queryactivityfragment);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityStorage.getInstance().addActivity(this);
    }

    private void init() {
        initPresenter();
        initView();
        initListener();
        initMap();
    }

    private void initPresenter() {
        queryAroundPresenter = new QueryAroundPresenter();
        queryAroundModel = new QueryAroundBizImpl();
        queryAroundPresenter.setViewAndModel(this, this, queryAroundModel);
    }

    /**
     * 初始化地图
     */
    public void initMap() {
        //没断网
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.frame1, qf);
        ft.commit();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        updateBT = (Button) findViewById(R.id.updateBT);//刷新按钮
        input_edittext = (TextView) findViewById(R.id.input_edittext);
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        detailTV = (TextView) findViewById(R.id.detail_list);
        detailTV.setOnClickListener(this);//列表监听
        findViewById(R.id.poisearch_back).setOnClickListener(this);//返回监听
        findViewById(R.id.input_edittext).setOnClickListener(this);//城市列表监听
        updateBT.setOnClickListener(this);//刷新按钮
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateBT://刷新页面
                try {
                    String cityName = input_edittext.getText().toString();
                    if (!cityName.equals("")) {
                        showProgressDialog();
                        LogUtil.sop(TAG, "cityName->" + cityName);
                        showProgressDialog();//开启等候弹出框
                        String url = Url.getInstance().getPath(2);
                        if (cityName.equals("济源市")) {
                            queryAroundPresenter.findChargingStationsByUpdate(url, "河南省", qf.lp);
                        } else {
                            queryAroundPresenter.findChargingStationsByUpdate(url, cityName, qf.lp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.poisearch_back://返回
                finish();
                break;
            case R.id.detail_list://顶部导航栏右侧按钮“列表”监听
                TextView text = (TextView) findViewById(R.id.detail_list);
                String name = text.getText().toString();
                if (qf.array != null) {//当值还没返回的时候不能操作
                    if (name.equals("列表")) {
                        try {
                            item = new ListFragmentItem(qf.array, qf.lp);
                            updateBT.setVisibility(View.INVISIBLE);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.hide(qf);
                            ft.add(R.id.frame1, item);
                            ft.commit();
                            text.setText("地图");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.remove(item);
                        ft.show(qf).commit();
                        text.setText("列表");
                        updateBT.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtil.showToast(this, "该城市无此电桩");
                }
                break;
            case R.id.input_edittext://搜索其他城市电桩
                Intent intent = new Intent(QueryActivity.this, CityPilesActivity.class);
                startActivityForResult(intent, 0x101);
                break;
        }
    }

    /**
     * 等用户选择城市后会返回城市名
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x101 && requestCode == 0x101) {
            otherCity = data.getStringExtra("city");
            String url = Url.getInstance().getPath(2);
//            qf.lp = new LatLng(35.058317, 112.611328);//112.611328,35.058317
            if (hasNetwork) {
                //有网络
                if (otherCity.equals("济源市")) {
                    queryAroundPresenter.findChargingStationsByCondition(url, "河南省", qf.lp);
                } else {
                    queryAroundPresenter.findChargingStationsByCondition(url, otherCity, qf.lp);
                }
            } else {
                //列表赋值
                if (stations != null) {
                    //城市名赋值
                    input_edittext.setText(otherCity);
                    if (otherCity.equals("济源市")) {
                        otherCity = "河南省";
                    }
                    //电站数据赋值
                    for (int i = 0; i < stations.size(); i++) {
                        if (stations.get(i).getAsJsonObject().get("city").getAsString().equals(otherCity)) {
                            JsonArray piles = stations.get(i).getAsJsonObject().get("data").getAsJsonArray();
//                            Log.i("test", "piles="+piles.toString());
                            NativeStationFragment nativeStationFragment = new NativeStationFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("piles", piles.toString());
                            bundle.putString("flag", "true");
                            nativeStationFragment.setArguments(bundle);//传递该电站下的电桩数组
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.replace(R.id.frame1, nativeStationFragment);
                            ft.commit();
                            break;
                        }
                    }
                }//离线电站不为空的情况
            }
        }
    }

    @Override
    public void show(String jsonstring) {
        ToastUtil.showToast(this, jsonstring);
    }

    @Override
    public void showProgressDialog() {
        //弹出框适配视图
        if (pDialog == null) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        } else {
            pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
            pDialog.setTitleText("请稍后！");
            pDialog.show();
        }
    }

    @Override
    public void stopProgressDialog() {
        pDialog.dismiss();
    }

    @Override
    public void getStations(String jsonstring) {
        LogUtil.sop(TAG, "城市那边过来的数据->" + jsonstring);
        //顶部导航栏城市名重写
        input_edittext.setTextColor(getResources().getColor(R.color.white));
        if (otherCity.equals("河南省")) {
            input_edittext.setText("济源市");//所选的城市名
        } else {
            input_edittext.setText(otherCity);//所选的城市名
        }
        try {
            qf.aMap.clear();//将原有兴趣点清除
            qf.getStations(jsonstring);//添加数据到地图
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getStationsByUpdate(String jsonstring) {
        LogUtil.sop(TAG, "刷新那边过来的数据->" + jsonstring);
        try {
            qf.aMap.clear();//将原有兴趣点清除
            qf.getStations(jsonstring);//添加数据到地图
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}