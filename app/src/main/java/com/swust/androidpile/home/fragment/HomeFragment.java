package com.swust.androidpile.home.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.home.activity.QueryActivity;
import com.swust.androidpile.home.activity.ScheduleActivity;
import com.swust.androidpile.home.activity.WatchActivity;
import com.swust.androidpile.home.model.ScheduleBiz;
import com.swust.androidpile.home.model.ScheduleBizImpl;
import com.swust.androidpile.home.presenter.SchedulePresenter;
import com.swust.androidpile.home.view.ScheduleView;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.mine.activity.LoginActivity;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.TokenUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.model.FindCostBizImpl;
import com.swust.androidpile.mine.presenter.FindCostPresenter;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.qr.activity.QRActivity;
import com.swust.androidpile.utils.ToastUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 首页
 */
public class HomeFragment extends Fragment implements View.OnClickListener, ScheduleView, MineView {

    private static final String TAG = "HomeFragment";
    private View view;
    private ImageView chargingIV;
    private ImageView scheduleIV;
    private SchedulePresenter schedulePresenter;
    private ScheduleBiz scheduleBiz;
    private User user;
    private AlertDialog dialog;
    private ImageIndicatorView imageIndicatorView;
    private AutoPlayManager autoBrocastManager;
    private ImageView billIV;
    private UserDao userDao;
    private FindCostPresenter findCostPresenter;
    private FindCostBizImpl findCostModel;
    private LinearLayout chargingLL;
    private LinearLayout scheduleLL;
    private LinearLayout billLL;
    private SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();

        /*测试阶段*/
//        Intent intent = new Intent(getActivity(), WatchActivity.class);
//        startActivity(intent);
        /*测试阶段*/

        initData();
        initListener();
        initPresenter();
        return view;
    }

    private void initData() {
        userDao = new UserDao(view.getContext());
        user = userDao.findUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (imageIndicatorView != null) {
            //设置循环播放
            autoBrocastManager.loop();
        }
    }

    /**
     * 初始化控制器
     */
    private void initPresenter() {
        schedulePresenter = new SchedulePresenter();
        findCostPresenter = new FindCostPresenter();
        scheduleBiz = new ScheduleBizImpl();
        findCostModel = new FindCostBizImpl();
        schedulePresenter.setViewAndModel(getActivity(), this, scheduleBiz, 1);
        findCostPresenter.setViewAndModel(this, findCostModel, view.getContext());
    }

    /**
     * 初始化视图
     */
    private void initView() {
        chargingIV = (ImageView) view.findViewById(R.id.chargingIV);
        scheduleIV = (ImageView) view.findViewById(R.id.scheduleIV);
        billIV = (ImageView) view.findViewById(R.id.billIV);
        chargingLL = (LinearLayout) view.findViewById(R.id.chargingLL);
        scheduleLL = (LinearLayout) view.findViewById(R.id.scheduleLL);
        billLL = (LinearLayout) view.findViewById(R.id.billLL);

        //设置图片轮播特效
        imageIndicatorView = (ImageIndicatorView) view.findViewById(R.id.indicate_view);
        Integer[] resArray = new Integer[]{R.mipmap.flipper1, R.mipmap.flipper3};
        imageIndicatorView.setupLayoutByDrawable(resArray);
        imageIndicatorView.setIndicateStyle(ImageIndicatorView.INDICATE_USERGUIDE_STYLE);
        imageIndicatorView.show();

        autoBrocastManager = new AutoPlayManager(imageIndicatorView);
        autoBrocastManager.setBroadcastEnable(true);
        //指定循环的次数
//        autoBrocastManager.setBroadCastTimes(2);
        //设置开始时间和间隔时间
        autoBrocastManager.setBroadcastTimeIntevel(1000, 1000);
        //设置循环播放
        autoBrocastManager.loop();
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        chargingIV.setOnClickListener(this);
        scheduleIV.setOnClickListener(this);
        billIV.setOnClickListener(this);
        chargingLL.setOnClickListener(this);
        scheduleLL.setOnClickListener(this);
        billLL.setOnClickListener(this);
    }

    @Override
    public void showProgress() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
        pDialog.setTitleText("请稍后！");
        pDialog.show();
    }

    @Override
    public void stopProgress() {
        pDialog.dismiss();
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(view.getContext(), str);
    }

    /**
     * 第一次进入充电进度页面
     *
     * @param jsonstring
     */
    @Override
    public void startActivity(String jsonstring) {
        Log.i("test", "充电进度查询返回值:" + jsonstring);
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(jsonstring);
        String type = obj.get("type").getAsString();//电桩类型
        Intent intent = null;
        if (type.equals("DC")) {
            //直流充电
            intent = new Intent(view.getContext(), ScheduleActivity.class);
            intent.putExtra("jsonstring", jsonstring);
            startActivity(intent);
        } else {
            Log.i("test","--->");
            //交流充电
            intent = new Intent(view.getContext(), WatchActivity.class);
            intent.putExtra("jsonstring", jsonstring);
            startActivity(intent);
        }
    }

    /**
     * 充电进度页面轮询操作
     *
     * @param jsonstring
     */
    @Override
    public void reStartSchedule(String jsonstring) {

    }

    @Override
    public void stopSuccess() {

    }

    @Override
    public void isNotCharging() {

    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("schedulePile");
    }

    @Override
    public void onClick(View v) {
        boolean flag = TokenUtil.checkToken(getActivity());
        if (!flag) {
            //用户没有登录，前往登录页面9
            startActivityForResult(new Intent(view.getContext(), LoginActivity.class), 0x101);
        } else {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.chargingLL:
                case R.id.chargingIV://充电站查询
                    intent = new Intent(view.getContext(), QueryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.billLL:
                case R.id.billIV://扫码充电
                    if (user == null) {
                        ToastUtil.showToast(view.getContext(), "请先注册");
                    } else {
                        //跳转到扫码充电界面
                        intent = new Intent(getActivity(), QRActivity.class);
                        startActivity(intent);
                    }
                    break;
                case R.id.scheduleLL:
                case R.id.scheduleIV://充电进度查询
                    if (user == null) {
                        ToastUtil.showToast(view.getContext(), "请先注册");
                    } else {
                        showProgress();
                        String phone = user.getPhone();//获取用户电话号码
                        String url = Url.getInstance().getPath(19);
                        schedulePresenter.schedule(url, phone);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        user = userDao.findUser();
    }

    @Override
    public void activityFinish() {
        pDialog.dismiss();
    }

    @Override
    public void noMoreRecode(String str) {

    }
}
