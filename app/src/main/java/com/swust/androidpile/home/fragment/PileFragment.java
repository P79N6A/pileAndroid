package com.swust.androidpile.home.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.home.activity.PileActivity;
import com.swust.androidpile.home.adapter.PileFragmentAdapter;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PileFragment extends Fragment {

    private static final String TAG = "PileFragment";
    private ListView listView;
    private ArrayList<String> pileInfo = new ArrayList<String>();
    JsonArray pileArray;//一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
    String gateid;//网关号
    private String stationName;    //充电站名
    View vv;
    private TextView pileTypeTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_listview, container, false);
        vv = view;
        try {
            init(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void init(View view) {
        setUp();//初始化参数
        setUpListView(view);//电桩列表赋值
        setListViewListener();
    }

    /**
     * 获取前一个页面传递过来的电桩信息
     */
    private void setUp() {
        Bundle bundle = getArguments();
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(bundle.getString("array_one"));//一个电站的所有信息，包括内部的电桩、当前定位点的经纬度
        pileArray = obj.get("pile").getAsJsonArray();
        gateid = ((JsonObject) (obj.get("station"))).get("gateid").getAsString();//网关号
        stationName = ((JsonObject) (obj.get("station"))).get("stationname").getAsString();//电站名
    }

    /**
     * 列表点击事件监听，前往充电界面
     */
    private void setListViewListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                UserDao dao = UserDao.getInstance(getActivity());
                User user = dao.findUser();
                if (user != null) {
                    JsonObject obj = (JsonObject) pileArray.get(arg2);
                    pileInfo.clear();
                    pileInfo.add(stationName);//电站名
                    pileInfo.add(obj.get("pileid").getAsString());//桩号
                    pileInfo.add(obj.get("type_name").getAsString());//类型
                    pileInfo.add(obj.get("state_name").getAsString());//状态
                    pileInfo.add(gateid);//网关
                    Intent intent = new Intent(getActivity(), PileActivity.class);
                    LogUtil.sop(TAG,"info=" + pileInfo);
                    intent.putStringArrayListExtra("pileInfo", pileInfo);
                    startActivity(intent);
                } else {
                    ToastUtil.showToast(getActivity(), "请先登录");
                }
            }
        });
    }

    /**
     * 给底部的ListView适配
     *
     * @param v
     */
    private void setUpListView(View v) {
        listView = (ListView) v.findViewById(android.R.id.list);
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        int len = pileArray.size();
        String[] text1 = new String[len];//电桩编号
        String[] text2 = new String[len];//电桩类型
        String[] text3 = new String[len];//电桩状态
        for (int i = 0; i < len; i++) {
            JsonObject pile = (JsonObject) pileArray.get(i);
            text1[i] = pile.get("pileid").getAsString();
            text2[i] = pile.get("type_name").getAsString();
            text3[i] = pile.get("state_name").getAsString();
        }
        for (int i = 0; i < len; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text1", text1[i]);
            map.put("text2", text2[i]);
            map.put("text3", text3[i]);
            listItems.add(map);
        }
        PileFragmentAdapter adapter = new PileFragmentAdapter(getActivity(), listItems, R.layout.view_home_pilelist,
                new String[]{"text1", "text2", "text3"},
                new int[]{R.id.textView22, R.id.textView44, R.id.textView55});
        listView.setAdapter(adapter);
    }

    /**
     * 自定义适配器
     *
     * @author Administrator
     */
    private class MySimpleAdapter extends SimpleAdapter {
        public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                               int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
//            try {
//                if(position==1){
//                    TextView tv = (TextView) convertView.findViewById(R.id.pileidTV);
//                    tv.setTextColor(getResources().getColor(R.color.red));
//                }
//            } catch (Resources.NotFoundException e) {
//                e.printStackTrace();
//            }
            return v;
        }

    }

}