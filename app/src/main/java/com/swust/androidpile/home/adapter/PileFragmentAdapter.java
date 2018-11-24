package com.swust.androidpile.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.swust.androidpile.R;

import java.util.List;
import java.util.Map;

/**
 * 作者：Chen Yixing on 2017/7/18.
 * 邮箱：1663268062@qq.com
 * 版本：V1.0
 */

/**
 * 单个电站所有电桩适配器
 */
public class PileFragmentAdapter extends BaseAdapter {
    private Context context;
    List<Map<String, Object>> listItems;
    int layout;
    String[] from;
    int[] to;

    public PileFragmentAdapter(Context context, List<Map<String, Object>> listItems,
                               int layout, String[] from, int[] to) {
        this.context = context;
        this.listItems = listItems;
        this.layout = layout;
        this.from = from;
        this.to = to;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).
                inflate(layout, null);

        TextView pileid = (TextView) convertView.findViewById(to[0]);//桩号
        TextView pileType = (TextView) convertView.findViewById(to[1]);//类型
        TextView pileState = (TextView) convertView.findViewById(to[2]);//状态
        Map<String, Object> map = (Map<String, Object>) getItem(position);
        pileid.setText(map.get(from[0]).toString());
        pileType.setText(map.get(from[1]).toString());
        pileState.setText(map.get(from[2]).toString());

        String[] split = map.get(from[2]).toString().split("---");
        if (split[0].equals("端口1:空闲") || split[1].equals("端口2:空闲")) {
            pileState.setTextColor(context.getResources().getColor(R.color.green));
        }else{
            pileState.setTextColor(context.getResources().getColor(R.color.black));
        }
        return convertView;
    }

}
