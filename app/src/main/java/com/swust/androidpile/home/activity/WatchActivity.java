package com.swust.androidpile.home.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.entity.Notes;
import com.swust.androidpile.home.model.ScheduleBiz;
import com.swust.androidpile.home.model.ScheduleBizImpl;
import com.swust.androidpile.home.presenter.SchedulePresenter;
import com.swust.androidpile.home.view.ScheduleView;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.qr.activity.QRActivity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WatchActivity extends AppCompatActivity implements ScheduleView {

    private static final String TAG = "WatchActivity";
    private TextView timeTV;
    private String phone;
    private SweetAlertDialog pDialog;
    private TextView out_vTV;
    private TextView out_iTV;
    private long timestamp;//首次开启充电的时间（毫秒）
    private int index = 0;
    private Button stopBT;
    private ScheduleBiz scheduleBiz;
    private SchedulePresenter schedulePresenter;
    private String jsonstring;
    private JsonParser parser;
    private JsonObject obj;
    private String out_v;
    private String out_i;
    private String port;
    private String pileid;
    public ScheduledExecutorService service;//暂时每次进入这个界面都创建一次
    private long trans_time;
    private int timeIndex = 0;//
    private TextView pileidTV;
    private TextView portTV;
    private String hour;//时
    private String minute;//分
    private String second;//秒
    private String trans_minutes;
    private TextView argTV;
    private Notes n;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityStorage.getInstance().addActivity(this);
    }

    private void init() {
        initView();
        initData();
        initPresenter();
    }

    private void initView() {
        timeTV = (TextView) findViewById(R.id.time);//充电时长
        out_vTV = (TextView) findViewById(R.id.out_v);//充电电压
        out_iTV = (TextView) findViewById(R.id.out_i); //充电电流
        stopBT = (Button) findViewById(R.id.stopBT);//停止充电按钮
        pileidTV = (TextView) findViewById(R.id.pileidTV);//电桩号
        portTV = (TextView) findViewById(R.id.portTV);//端口号
        argTV = (TextView) findViewById(R.id.argTV);//充电参数
    }

    private void initData() {
        jsonstring = getIntent().getStringExtra("jsonstring");
        LogUtil.sop(TAG, jsonstring);
        parser = new JsonParser();
        obj = (JsonObject) parser.parse(jsonstring);

        out_v = obj.get("out_v").getAsString();//充电电压
        out_i = obj.get("out_i").getAsString();//充电电流
        phone = obj.get("phone").getAsString();//手机号码
        pileid = obj.get("pileid").getAsString();//电桩编号
        port = obj.get("port").getAsString();//充电端口
        trans_minutes = obj.get("trans_minutes").getAsString();//交易入库时间

        //界面赋值操作
        out_vTV.setText("电压：" + out_v + "V");//界面电压赋值
        out_iTV.setText("电流：" + out_i + "A");//界面电流赋值
        pileidTV.setText("桩号：" + pileid);//桩号赋值
        portTV.setText("端口：" + port);//端口赋值

        timeUpdate();//刷新界面数据
    }

    private void initPresenter() {
        scheduleBiz = new ScheduleBizImpl();
        schedulePresenter = new SchedulePresenter();
        schedulePresenter.setViewAndModel(this, this, scheduleBiz, 2);//数字2代表非首次轮询进度

        /**
         * 关闭充电监听
         */
        stopBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showProgress();//设置弹出框
                    schedulePresenter.stopCharging(Url.getInstance().getPath(20), phone);
                    //返回stopSuccess()
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.sop(TAG, "关闭充电故障！");
                }
            }
        });

        argTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (n == null)
                    n = ChargingDao.getInstance(WatchActivity.this).findNotes();
                if (n != null) {
                    new SweetAlertDialog(WatchActivity.this)
                            .setTitleText("充电参数")
                            .setContentText(n.getNotes())
                            .show();
                } else {
                    ToastUtil.showToast(WatchActivity.this, "尚未充电");
                }
            }
        });

        /*轮询充电进度开始*/
        Runnable runnable = new Runnable() {
            public void run() {
                String url = Url.getInstance().getPath(19);
                try {
                    schedulePresenter.schedule(url, phone);
                } catch (Exception e) {
                    LogUtil.sop(TAG, "轮询进度出现故障！");
                    e.printStackTrace();
                }
            }
        };

        if (service == null || service.isShutdown()) {
            service = Executors
                    .newSingleThreadScheduledExecutor();
        }
        //60秒后开始首次请求，以后轮询为30秒一次
        service.scheduleAtFixedRate(runnable, 60, 30, TimeUnit.SECONDS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("schedulePile");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭计时器
        if (service != null) {
            if (!service.isShutdown()) {
                service.shutdown();
                LogUtil.sop(TAG, "------------------------计时器关闭------------------------");
                ActivityStorage.getInstance().deleteAll();//回退到初始界面
            }
        }
    }

    @Override
    public void showProgress() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
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
        ToastUtil.showToast(this, str);
    }

    @Override
    public void startActivity(String jsonstring) {

    }

    //正在充电中，更新充电进度信息
    @Override
    public void reStartSchedule(String jsonstring) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(jsonstring);
        out_i = obj.get("out_i").getAsString();
        out_v = obj.get("out_v").getAsString();
        pileid = obj.get("pileid").getAsString();//电桩编号
        port = obj.get("port").getAsString();//充电端口
        trans_minutes = obj.get("trans_minutes").getAsString();//交易入库时间

        out_vTV.setText("电压：" + out_v + "V");//界面电压赋值
        out_iTV.setText("电流：" + out_i + "A");//界面电流赋值
        //界面赋值操作
        timeUpdate();
    }

    /**
     * 刷新界面时间
     */
    private void timeUpdate() {
        int tm = Integer.parseInt(trans_minutes);
        int h = tm / 60;//小时
        int m = tm % 60;//去除小时候后的分钟
        if (0 <= tm && tm < 10) {
            minute = "0" + tm;
            hour = "00";
        } else if (10 <= tm && tm < 60) {
            minute = "" + tm;
            hour = "00";
        } else if (60 <= tm) {
            if (0 <= h && h < 10) {
                hour = "0" + h;
            } else {
                hour = "" + h;
            }
            if (0 <= m && m < 10) {
                minute = "0" + m;
            } else {
                minute = "" + m;
            }
        }

        timeTV.setText(hour + "时" + minute + "分");//给界面设置充电时长
    }

    @Override
    public void stopSuccess() {
        LogUtil.sop(TAG, "关闭充电成功！");
        if (service != null) {
            if (!service.isShutdown()) {
                service.shutdown();
                LogUtil.sop(TAG, "------------------------计时器关闭------------------------");
                ActivityStorage.getInstance().deleteAll();//回退到初始界面
            }
        }
         /*清除充电缓存*/
        ChargingDao chargingDao = ChargingDao.getInstance(this);
        chargingDao.deleteCharging();
        chargingDao.deleteNotes();
        QRActivity.nodes = null;
    }

    /**
     * 轮询进度信息，充电结束操作
     */
    @Override
    public void isNotCharging() {
        LogUtil.sop(TAG, "充电结束，自动退出！");
        if (service != null) {
            if (!service.isShutdown()) {
                service.shutdown();
                LogUtil.sop(TAG, "------------------------计时器关闭------------------------");
                ActivityStorage.getInstance().deleteAll();//回退到初始界面
            }
        }
         /*清除充电缓存*/
        ChargingDao chargingDao = ChargingDao.getInstance(this);
        chargingDao.deleteCharging();
        chargingDao.deleteNotes();
        QRActivity.nodes = null;
    }

}
