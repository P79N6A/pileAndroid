package com.swust.androidpile.home.fragment;

import android.annotation.SuppressLint;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.NaviLatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.home.activity.StationDetailsActivity;
import com.swust.androidpile.home.navi.GPSNaviActivity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

@SuppressLint("ValidFragment")
public class ListFragmentItem extends ListFragment implements StationDetailsActivity.CallBack {
    ListView listView;
    SimpleAdapter adapter;
    JsonArray array;//当前城市的电站/电桩信息
    LatLng ll;// 定位的经纬度
    private SweetAlertDialog pDialog;

    /**
     * 构造函数，从QueryAroundFragment.java类中，获取定位的经纬度以及当前城市的电站/电桩信息
     *
     * @param array 当前城市的电站/电桩信息
     * @param ll    定位的经纬度
     */
    public ListFragmentItem(JsonArray array, LatLng ll) {
        this.array = array;
        this.ll = ll;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_item, container, false);
        try {
            setUp(view);
//            StationDetailsActivity.setCallBack(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    //当预约后这些变量都要更新一下
    @Override
    public void onResume() {
        super.onResume();
        //setUp(getView());
    }

    private void setUp(View view) {
        int len = array.size();
        listView = (ListView) view.findViewById(android.R.id.list);
        listView.setSelector(new ColorDrawable());//设置点击listview不会变色
        String[] textView3 = new String[len];//电站名
        double[] distance = new double[len];//距离
        int[] textView4 = new int[len];//总桩数
        int[] textView5 = new int[len];//空桩数
        for (int i = 0; i < len; i++) {
            JsonObject array_one = (JsonObject) array.get(i);
            JsonObject json_station = (JsonObject) (array_one.get("station"));
            textView3[i] = json_station.get("stationname").getAsString();
            distance[i] = array_one.get("distance").getAsDouble();
            //距离格式化，将小数点固定为1位！
            DecimalFormat df = new DecimalFormat("0.0");
            distance[i] = Double.parseDouble(df.format(distance[i]));

            try {
                //这里数据库有可能没有添加值
                textView4[i] = json_station.get("capacity").getAsInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
            textView5[i] = json_station.get("relaxPiles").getAsInt();
        }
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < textView3.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("textView3", "电站:" + textView3[i]);
            if (distance[i] > 1000) {
                DecimalFormat df = new DecimalFormat("0.00");
                distance[i] = Double.parseDouble(df.format(distance[i] / 1000));
                map.put("textView2", "距离:" + distance[i] + "KM");
            } else {
                map.put("textView2", "距离:" + distance[i] + "M");
            }
            map.put("textView4", "总桩数:" + textView4[i]);
            map.put("textView5", "空桩数:" + textView5[i]);
            listItems.add(map);
        }
        adapter = new MySimpleAdapter(getActivity(), listItems, R.layout.fragment_home_listfragment,
                new String[]{"textView3", "textView2", "textView4", "textView5"},
                new int[]{R.id.textView3, R.id.textView2, R.id.textView4, R.id.textView5});
        listView.setAdapter(adapter);
    }//end for setUp(View view)

    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                               int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            final int num = position;//对应的第num个充电站
            //导航监听
            v.findViewById(R.id.naviTV).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    JsonObject array_one = (JsonObject) array.get(num);
                    JsonObject json_station = (JsonObject) (array_one.get("station"));
                    Intent intent = new Intent(getActivity(), GPSNaviActivity.class);
                    NaviLatLng mEndLatlng = new NaviLatLng(json_station.get("lat").getAsDouble(),
                            json_station.get("lng").getAsDouble());
                    NaviLatLng mStartLatlng = new NaviLatLng(ll.latitude, ll.longitude);
                    intent.putExtra("mEndLatlng", mEndLatlng);
                    intent.putExtra("mStartLatlng", mStartLatlng);
                    startActivity(intent);
                }
            });
            v.findViewById(R.id.pileDetailRL).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), StationDetailsActivity.class);
                    JsonObject obj = (JsonObject) array.get(num);
                    obj.addProperty("lat", ll.latitude);
                    obj.addProperty("lng", ll.longitude);
                    intent.putExtra("array_one", obj.toString());
                    startActivity(intent);
                }
            });
            return v;
        }
    }

    @Override
    public void stopProgress() {
        pDialog.dismiss();
    }

    public void showProgress() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
        pDialog.setTitleText("刷新中...");
        pDialog.show();
    }
}