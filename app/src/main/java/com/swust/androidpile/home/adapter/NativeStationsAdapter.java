package com.swust.androidpile.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;

/**
 * Created by Administrator on 2018/1/14/014.
 */

public class NativeStationsAdapter extends RecyclerView.Adapter<NativeStationsAdapter.ViewHolder> {

    private JsonArray jsonArray;

    public NativeStationsAdapter(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public NativeStationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_nativestationsadapter, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NativeStationsAdapter.ViewHolder holder, int position) {
        JsonObject jsonObject = jsonArray.get(position).getAsJsonObject();
        holder.stationName.setText("电站名：" + jsonObject.get("stationname").getAsString());
        holder.stationType.setText("电站类型：" + jsonObject.get("stationtype").getAsString());
        holder.stationNum.setText("  电桩总数：" + jsonObject.get("capacity").getAsString());
        holder.stationAddr.setText("电站地址：" + jsonObject.get("address").getAsString());
    }

    @Override
    public int getItemCount() {
        return jsonArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView stationName;//电站名
        TextView stationType;//电站类型
        TextView stationNum;//电站内部电桩总数
        TextView stationAddr;//电站地址

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.stationName = (TextView) view.findViewById(R.id.stationName);
            this.stationType = (TextView) view.findViewById(R.id.stationType);
            this.stationNum = (TextView) view.findViewById(R.id.stationNum);
            this.stationAddr = (TextView) view.findViewById(R.id.stationAddr);
        }
    }
}
