package com.swust.androidpile.home.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.swust.androidpile.R;
import com.swust.androidpile.home.activity.StationDetailsActivity;
import com.swust.androidpile.home.model.QueryAroundBiz;
import com.swust.androidpile.home.model.QueryAroundBizImpl;
import com.swust.androidpile.home.navi.GPSNaviActivity;
import com.swust.androidpile.home.presenter.QueryAroundPresenter;
import com.swust.androidpile.home.view.QueryAroundView;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QueryAroundFragment extends LocationFragment implements LocationFragment.CallBack, QueryAroundView
        , AMap.OnMarkerClickListener, LocationSource, AMapLocationListener {

    private static final String TAG = "QueryAroundFragment";
    private QueryAroundBiz model = new QueryAroundBizImpl();
    public QueryAroundPresenter presenter = new QueryAroundPresenter();
    private View view;
    private ProgressDialog progressDialog;
    private TextView titleName;
    private Marker lastMarker;
    public JsonArray array;
    private android.support.v7.app.AlertDialog dialog;
    private SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        try {
            initPresenter();
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initListener();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        showProgressDialog();
        titleName = (TextView) getActivity().findViewById(R.id.input_edittext);
    }

    /**
     * 初始化事件分发器
     */
    private void initPresenter() {
        presenter.setViewAndModel(view.getContext(), this, model);
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        setCallBack(this);//回调监听定位
    }

    /**
     * 定位回调：获取当前定位点的经纬度以及城市名
     *
     * @param lp
     * @param cityName
     * @param aMap
     */
    @Override
    public void getInfo(LatLng lp, String cityName, AMap aMap) {
        initTitleName(cityName);
        String url = Url.getInstance().getPath(2);
        presenter.findChargingStations(url, cityName, lp);
    }

    @Override
    public void getError() {
        //比如没网定位失败的时候，需要关闭弹出框
        stopProgressDialog();
    }

    private void initTitleName(String cityName) {
        String covertCity = cityName;
        titleName.setTextColor(QueryAroundFragment.this.getResources().getColor(R.color.white));
        if (cityName.equals("河南省")) {
            covertCity = "济源市";
        }
        titleName.setText(covertCity);
    }

    @Override
    public void show(String jsonstring) {
        ToastUtil.showToast(view.getContext(), jsonstring);
    }

    @Override
    public void showProgressDialog() {
        //弹出框适配视图
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
        pDialog.setTitleText("请稍后！");
        pDialog.show();
    }

    @Override
    public void stopProgressDialog() {
        pDialog.dismiss();
    }

    /**
     * 请求电站查询回调：获取充电站信息并添加到地图中去
     *
     * @param jsonstring
     */
    @Override
    public void getStations(String jsonstring) {
        LogUtil.sop(TAG, jsonstring);//所有电桩信息
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(jsonstring);
        array = obj.get("infoArray").getAsJsonArray();
        ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
        for (int i = 0; i < array.size(); i++) {//image=map
            JsonObject json_station = (JsonObject) ((JsonObject) array.get(i)).get("station");
            //经纬度数据库有可能登记错误，需要捕捉
            try {
                double lat = json_station.get("lat").getAsDouble(), lng = json_station.get("lng").getAsDouble();
                if (lat <= 90 && lat >= 0 && lng <= 180 && lng >= 0) {
                    MarkerOptions markerOption = new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .snippet("" + i)//这个用来标记当前兴趣点是第几个电站
                            .position(new LatLng(json_station.get("lat").getAsDouble(), json_station.get("lng").getAsDouble()))
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(),
                                            R.mipmap.ditu)))
                            .draggable(true);
                    markerOptionlst.add(markerOption);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        aMap.moveCamera(CameraUpdateFactory.zoomTo(17));  //地图的缩放级别：3--19
//        aMap.moveCamera(CameraUpdateFactory.changeLatLng(lp));
        List<Marker> markerlst = aMap.addMarkers(markerOptionlst, true);
    }

    @Override
    public void getStationsByUpdate(String jsonstring) {

    }

    /**
     * 兴趣点点击弹出框自定义
     *
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        lastMarker = marker;
        final int index = Integer.parseInt(marker.getSnippet());
        //自定义弹出框
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //自定义布局
        View layout = inflater.inflate(R.layout.view_markactivity_infowindow, null);
        //ListView里面内容的适配
        TextView textView1 = (TextView) layout.findViewById(R.id.textView1);//充电站名
        TextView textView2 = (TextView) layout.findViewById(R.id.textView2);//距离
        TextView textView3 = (TextView) layout.findViewById(R.id.textView3);//收费标准
        TextView textView4 = (TextView) layout.findViewById(R.id.textView4);//总桩数
        TextView textView5 = (TextView) layout.findViewById(R.id.textView5);//空桩数
        final JsonObject json_station = (JsonObject) ((JsonObject) array.get(index)).get("station");
        textView1.setText(json_station.get("stationname").getAsString());
        double distance = ((JsonObject) array.get(index)).get("distance").getAsDouble();
        if (distance > 1000) {
            DecimalFormat df = new DecimalFormat("0.00");
            distance = Double.parseDouble(df.format(distance / 1000));
            textView2.setText("距离:" + distance + "KM");
        } else {
            textView2.setText("距离:" + distance + "M");
        }
        textView3.setText("收费标准:" + json_station.get("rate").getAsString());
        try {
            textView4.setText("总桩数:" + json_station.get("capacity").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        textView5.setText("空闲数:" + json_station.get("relaxPiles").getAsString());

        //进入导航
        layout.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GPSNaviActivity.class);
                //先纬度latitude，后精度longitude
                NaviLatLng mEndLatlng = new NaviLatLng(json_station.get("lat").getAsDouble(), json_station.get("lng").getAsDouble());
                NaviLatLng mStartLatlng = new NaviLatLng(lp.latitude, lp.longitude);
                intent.putExtra("mEndLatlng", mEndLatlng);
                intent.putExtra("mStartLatlng", mStartLatlng);
                startActivity(intent);
            }
        });

        //进入充电桩详情页
        layout.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//pileQuery
                try {
                    Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
                    JsonObject obj = (JsonObject) array.get(index);
                    obj.addProperty("lat", lp.latitude);
                    obj.addProperty("lng", lp.longitude);
                    intent.putExtra("array_one", obj.toString());
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //点击Dialog以外的区域触发事件
        new AlertDialog.Builder(getActivity())
                .setView(layout)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        lastMarker.hideInfoWindow();
                    }
                })
                .show();
        return false;
    }


}
