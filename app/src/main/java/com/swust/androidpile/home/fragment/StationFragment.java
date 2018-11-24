package com.swust.androidpile.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.navi.model.NaviLatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.home.navi.GPSNaviActivity;
import com.swust.androidpile.utils.LogUtil;

import java.text.DecimalFormat;

public class StationFragment extends Fragment implements OnClickListener {

    private static final String TAG = "StationFragment";
    JsonObject array_one;//一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_station1, container, false);
        initView();
        initListener();
        return view;
    }

    private void initView() {
        setUpPile(view);//pileQuery等值初始化
        setUp(view);//非listview部分的初始化
    }

    private void initListener() {
        view.findViewById(R.id.linear3).setOnClickListener(this);//导航监听
    }

    /**
     * 得到一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
     *
     * @param view
     * @return
     */
    private void setUpPile(View view) {
        Bundle bundle = getArguments();
        JsonParser parser = new JsonParser();
        array_one = (JsonObject) parser.parse(bundle.getString("array_one"));//一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
    }

    /**
     * 初始化页面信息
     *
     * @param v 一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
     */
    private void setUp(View v) {
        try {
            LogUtil.sop(TAG,array_one.toString());
            JsonObject station = (JsonObject) array_one.get("station");
            //电站站名
            TextView textView7 = (TextView) v.findViewById(R.id.textView7);
            textView7.setText(station.get("stationname").getAsString());
            //运营商家
            TextView textView11 = (TextView) v.findViewById(R.id.textView11);
            textView11.setText("河南远大电力设备有限公司");
            //运营商家
            TextView textView13 = (TextView) v.findViewById(R.id.textView13);
            textView13.setText(station.get("start_time").getAsString() + "--" + station.get("end_time").getAsString());
            //电站地址
            TextView textView2 = (TextView) v.findViewById(R.id.textView2);
            textView2.setText("地址：" + station.get("address").getAsString());
            //电桩数目
            TextView textView4 = (TextView) v.findViewById(R.id.textView4);
            textView4.setText("总数：" + station.get("capacity").getAsInt() + "个  |" +
                    "  空闲数：" + station.get("relaxPiles").getAsInt() + "个");
            //电站距离
            TextView textView5 = (TextView) v.findViewById(R.id.textView5);
            double distance = array_one.get("distance").getAsDouble();
            if (distance > 1000) {
                DecimalFormat df = new DecimalFormat("0.00");
                distance = Double.parseDouble(df.format(distance / 1000));
                textView5.setText("距离当前定位点共：" + distance + "KM");
            } else {
                textView5.setText("距离当前定位点共：" + distance + "M");
            }
        } catch (Exception e) {
            LogUtil.sop(TAG,e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear3://导航监听
                JsonObject station = (JsonObject) array_one.get("station");
                Intent intent = new Intent(getActivity(), GPSNaviActivity.class);
                //先纬度latitude，后精度longitude
                NaviLatLng mEndLatlng = new NaviLatLng(station.get("lat").getAsDouble(), station.get("lng").getAsDouble());
                NaviLatLng mStartLatlng = new NaviLatLng(array_one.get("lat").getAsDouble(), array_one.get("lng").getAsDouble());
                intent.putExtra("mEndLatlng", mEndLatlng);
                intent.putExtra("mStartLatlng", mStartLatlng);
                startActivity(intent);
                break;
        }
    }

}
