package com.swust.androidpile.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.navi.model.NaviLatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.home.adapter.NativeStationsAdapter;
import com.swust.androidpile.home.navi.GPSNaviActivity;
import com.swust.androidpile.utils.LogUtil;

import java.text.DecimalFormat;

public class NativeStationFragment extends Fragment {

    private static final String TAG = "NativeStationFragment";
    private View view;
    private TextView tipTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_nativestations, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        tipTV = (TextView) view.findViewById(R.id.tipTV);
        String flag = getArguments().getString("flag");
        if (flag.equals("true")) {
            tipTV.setVisibility(View.GONE);
            JsonParser parser = new JsonParser();
            JsonArray piles = parser.parse(getArguments().getString("piles")).getAsJsonArray();

            RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.nativeStationFL);
            LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);//设置方向
            myRecyclerView.setLayoutManager(manager);
            myRecyclerView.setAdapter(new NativeStationsAdapter(piles));
        }
    }


}
