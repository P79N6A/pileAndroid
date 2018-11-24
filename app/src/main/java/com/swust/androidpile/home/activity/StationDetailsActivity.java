package com.swust.androidpile.home.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.swust.androidpile.R;
import com.swust.androidpile.home.fragment.LocationFragment;
import com.swust.androidpile.home.fragment.PileFragment;
import com.swust.androidpile.home.fragment.StationFragment;
import com.swust.androidpile.main.ActivityStorage;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StationDetailsActivity extends Activity implements OnClickListener {

    String array_one;//单个电站/电桩信息
    private TextView pileText;
    private TextView stationText;
    private StationFragment sf = new StationFragment();//电桩界面
    private PileFragment pf = new PileFragment();//电桩界面
    private int stateInt = 0;//电站为0，电桩为1
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_framelayout);
        try {
            init();
//            cb.stopProgress();//通知前一个页面，创建页面结束，停止进度页面
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        initView();
        initListener();
        ActivityStorage.getInstance().addActivity(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        pileText = (TextView) findViewById(R.id.pile);
        stationText = (TextView) findViewById(R.id.station);

        //默认显示电站framgent
        array_one = getIntent().getStringExtra("array_one");//单个电站/电桩信息
        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
        //向StationFragment传递点击的第index个电站
        Bundle bundle = new Bundle();
        bundle.putString("array_one", array_one);
//        sf.setArguments(bundle);
//        ft1.replace(R.id.frame1, sf);
        pf.setArguments(bundle);
        ft1.replace(R.id.frame1, pf);
        ft1.commit();
    }

    /**
     * 初始化控件监听器
     */
    private void initListener() {
        findViewById(R.id.image1).setOnClickListener(this);
//        findViewById(R.id.station).setOnClickListener(this);
//        findViewById(R.id.pile).setOnClickListener(this);
    }

    public void stopProgress() {
        pDialog.dismiss();
    }

    public void showProgress() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
        pDialog.setTitleText("请稍后！");
        pDialog.show();
    }

    @Override
    public void onClick(View v) {
//        int index = this.getResources().getColor(R.color.main);
//        FragmentTransaction ft1 = getFragmentManager().beginTransaction();
//        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.image1://返回监听
                finish();
                break;
//            case R.id.station://顶部电站监听
//                if (stateInt == 1) {
//                    pileText.setBackgroundColor(getResources().getColor(R.color.main));
//                    pileText.setTextColor(Color.WHITE);
//                    stationText.setBackgroundColor(Color.WHITE);
//                    stationText.setTextColor(getResources().getColor(R.color.main));
//                    //向StationFragment传递点击的第index个电站
//                    bundle.putString("array_one", array_one);
//                    sf.setArguments(bundle);
//                    ft1.replace(R.id.frame1, sf);
//                    ft1.commit();
//                    stateInt = 0;//切换状态
//                }
//                break;
//            case R.id.pile://顶部电桩监听
//                if (stateInt == 0) {
//                    stationText.setBackgroundColor(getResources().getColor(R.color.main));
//                    stationText.setTextColor(Color.WHITE);
//                    pileText.setBackgroundColor(Color.WHITE);
//                    pileText.setTextColor(getResources().getColor(R.color.main));
//                    //向StationFragment传递点击的第index个电站
//                    bundle.putString("array_one", array_one);
//                    pf.setArguments(bundle);
//                    ft1.replace(R.id.frame1, pf);
//                    ft1.commit();
//                    stateInt = 1;//切换状态
//                }
//                break;
        }
    }

    /********************这里是回调监听定位********************/
    private static CallBack cb;

    public static interface CallBack {
        void stopProgress();
    }

    public static void setCallBack(CallBack callBack) {
        cb = cb;
    }
}
