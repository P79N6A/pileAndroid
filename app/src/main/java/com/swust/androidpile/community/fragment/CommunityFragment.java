package com.swust.androidpile.community.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.community.activity.MessageArrayActivity;
import com.swust.androidpile.community.adapter.CommunityFragmentAdapter;
import com.swust.androidpile.community.model.SystemMsgBizImpl;
import com.swust.androidpile.community.presenter.CommunityPresent;
import com.swust.androidpile.community.view.CommunityView;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.mine.activity.LoginActivity;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.TokenUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.activity.AdviceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityFragment extends Fragment implements CommunityFragmentAdapter.MyItemClickListener, CommunityView {

    private View view;
    List<Map<String, Object>> list;
    CommunityFragmentAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private CommunityPresent present = new CommunityPresent();
    private SystemMsgBizImpl model = new SystemMsgBizImpl();
    private JsonArray msgArray;
    private String phone;//用户建议模块需要传递电话号码

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community, container, false);
        initView();
        initPresent();
        initListener();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        /*----------------------RecyclerView适配--------------------------*/
        int[] imageIds = {R.mipmap.notice_item1, R.mipmap.notice_item2};
        String[] msgTV = {"", ""};
        String[] titles = {"系统消息", "用户建议"};
        String[] contents = {"请下拉刷新", "我们的进步离不开您的建议"};
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageIds[i]);
//            map.put("msgTV", msgTV[i]);
            map.put("title", titles[i]);
            map.put("content", contents[i]);
            list.add(map);
        }
        RecyclerView myRecyclerView = (RecyclerView) view.findViewById(R.id.msgRV);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);//设置方向
        myRecyclerView.setLayoutManager(manager);
        adapter = new CommunityFragmentAdapter(list, this);
        myRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        /*----------------------------获取下拉组件--------------------------*/
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fresh);
    }

    /**
     * 初始化事件分发器
     */
    private void initPresent() {
        present.setViewAndModel(this, model, view.getContext());
        present.getPhone(view.getContext());
    }

    /**
     * 列表下滑事件监听
     */
    private void initListener() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                present.findMsg(Url.getInstance().getPath(5));
            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {
        boolean flag = TokenUtil.checkToken(getActivity());
//        LogUtil.sop("test", "flag=" + flag);
        switch (postion) {
            case 0://“系统消息”点击监听
                if (msgArray == null) {
                    ToastUtil.showToast(view.getContext(), "下拉刷新获取消息！");
                } else {
                    Intent intent1 = new Intent(getActivity(), MessageArrayActivity.class);
                    intent1.putExtra("msgArray", msgArray.toString());
                    startActivity(intent1);
                }
                break;
            case 1://“用户建议”点击监听
//                LogUtil.sop("test", "phone=" + phone);
                if (!flag) {
                    //用户没有登录，前往登录页面
                    startActivityForResult(new Intent(view.getContext(), LoginActivity.class), 0x101);
                } else {
                    UserDao userDao = new UserDao(getActivity());
                    User user = userDao.findUser();
                    phone = user.getPhone();
                    if (phone != null) {
                        Intent intentAdvice = new Intent(getActivity(), AdviceActivity.class);
                        intentAdvice.putExtra("phone", phone);
                        startActivity(intentAdvice);
                    } else {
                        //前往登录界面
                        startActivityForResult(new Intent(view.getContext(), LoginActivity.class), 0x101);
                    }
                }
                break;
        }
    }

    @Override
    public void fail() {
        ToastUtil.showToast(view.getContext(), "网络请求失败！");
        refreshLayout.setRefreshing(false);//结束下拉刷新
    }

    @Override
    public void success(String json) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(json);
        boolean flag = obj.get("flag").getAsBoolean();
        if (flag) {
            ToastUtil.showToast(view.getContext(), "刷新成功！");
            msgArray = obj.get("msg").getAsJsonArray();
            int len = msgArray.size();
//            list.get(0).put("msgTV", "" + len);
            list.get(0).put("content", "点我查看消息");
            adapter.notifyDataSetChanged();
        } else {
            ToastUtil.showToast(getActivity(), "暂无消息");
        }
        refreshLayout.setRefreshing(false);//结束下拉刷新
    }

    @Override
    public void show(String str) {

    }

    @Override
    public void getPhone(String phone) {
        this.phone = phone;
    }
}