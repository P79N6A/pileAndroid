package com.swust.androidpile.home.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.home.model.ScheduleBiz;
import com.swust.androidpile.home.model.ScheduleBizImpl;
import com.swust.androidpile.home.presenter.SchedulePresenter;
import com.swust.androidpile.home.view.ScheduleBottom;
import com.swust.androidpile.home.view.ScheduleTop;
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

public class ScheduleActivity extends Activity implements ScheduleView {

    private static final String TAG = "ScheduleActivity";
    protected String jsonstring;
    protected JsonParser parser;
    protected JsonObject obj;
    protected String soc;
    protected String out_i;
    protected String out_v;
    private Handler handler;
    private int count = 0;
    private ScheduleTop schedule1Top;
    private ScheduleBottom scheduleBottom;
    private String phone;
    private ScheduleBiz scheduleBiz;
    private SchedulePresenter schedulePresenter;
    private SchedulePresenter sp;
    public ScheduledExecutorService service;//暂时每次进入这个界面都创建一次
    private SweetAlertDialog pDialog;
    private TextView query_pileid;
    private TextView query_port;
    private String pileid;
    private String port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_home_schedule);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityStorage.getInstance().addActivity(this);
    }

    /**
     * 初始化
     */
    private void init() {
        initView();
        initStopCharging();
        scheduleBiz = new ScheduleBizImpl();
        schedulePresenter = new SchedulePresenter();
        schedulePresenter.setViewAndModel(this, this, scheduleBiz, 2);//数字2代表非首次轮询进度

        jsonstring = getIntent().getStringExtra("jsonstring");
        LogUtil.sop(TAG, jsonstring);
        parser = new JsonParser();
        obj = (JsonObject) parser.parse(jsonstring);

        soc = obj.get("SOC").getAsString();//充电进度
        out_v = obj.get("out_v").getAsString();//充电电压
        out_i = obj.get("out_i").getAsString();//充电电流
        phone = obj.get("phone").getAsString();//手机号码
        pileid = obj.get("pileid").getAsString();//电桩编号
        port = obj.get("port").getAsString();//充电端口

        schedule1Top.setInvilate(soc);//时针表刷新进度
        scheduleBottom.setInvilate(out_v, out_i);//刷新电压、电流值
        query_pileid.setText("桩号：" + pileid);//电桩赋值
        query_port.setText("端口：" + port);//端口赋值

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

    private void initView() {
        schedule1Top = (ScheduleTop) findViewById(R.id.schedule1Top);
        scheduleBottom = (ScheduleBottom) findViewById(R.id.schedule1Bottom);
        query_pileid = (TextView) findViewById(R.id.query_pileid);
        query_port = (TextView) findViewById(R.id.query_port);

        //隐藏状态栏

    }

    private void initStopCharging() {
        sp = new SchedulePresenter();
        scheduleBiz = new ScheduleBizImpl();
        sp.setViewAndModel(this, this, scheduleBiz, 2);

        //监听结束充电按钮
        findViewById(R.id.stopBT).setOnClickListener(new OnClickListener() {
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
    }

    //正在充电中，更新充电进度页面信息
    @Override
    public void reStartSchedule(String jsonstring) {
        JsonParser parser = new JsonParser();
        JsonObject obj = (JsonObject) parser.parse(jsonstring);
        soc = obj.get("SOC").getAsString();
        out_i = obj.get("out_i").getAsString();
        out_v = obj.get("out_v").getAsString();
        schedule1Top.setInvilate(soc);
        scheduleBottom.setInvilate(out_v, out_i);
    }

    /**
     * 关闭充电成功
     */
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
        LogUtil.sop(TAG,"充电结束，自动退出！");
        if (service != null) {
            if (!service.isShutdown()) {
                service.shutdown();
                LogUtil.sop(TAG,"------------------------计时器关闭------------------------");
                ActivityStorage.getInstance().deleteAll();//回退到初始界面
            }
        }
        /*清除充电缓存*/
        ChargingDao chargingDao = ChargingDao.getInstance(this);
        chargingDao.deleteCharging();
        chargingDao.deleteNotes();
        QRActivity.nodes = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (service != null) {
            if (!service.isShutdown()) {
                service.shutdown();
                LogUtil.sop(TAG,"------------------------计时器关闭------------------------");
                ActivityStorage.getInstance().deleteAll();//回退到初始界面
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("schedulePile");
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

}
