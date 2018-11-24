package com.swust.androidpile.qr.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.R.color;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.Notes;
import com.swust.androidpile.home.activity.ScheduleActivity;
import com.swust.androidpile.home.activity.TipActivity;
import com.swust.androidpile.home.activity.WatchActivity;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.qr.model.QRBiz;
import com.swust.androidpile.qr.model.QRBizImpl;
import com.swust.androidpile.qr.presenter.QRPresenter;
import com.swust.androidpile.qr.view.QRView;
import com.zxing.activity.CaptureActivity;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QRActivity extends Activity implements OnClickListener, QRView {

    private static final String TAG = "QRActivity";
    private EditText input_value;
    private int flag;
    private Button scan;
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private TextView chargingStyleView;    //最后一次充电方式的选择：充电方式选择对应的视图，默认：时间充（时）对应的button5
    private int buttonStyleLastId = Integer.MAX_VALUE;        //充电方式选择中，最后一次的选择对应的整数值。1：时间冲；2：电量冲；3：金额冲；4：进度冲
    private String button_port;    //二维码显示：充电端口选择值
    private String button_ev;    //直流时，用户选定的辅助电源电压值（12V或者24V）
    private String inputString;
    private TextView charging_tip;
    private boolean legalOrNot = true;
    private String inputHexString;
    private String gateId;
    private String pileId;
    private String charging_type;
    private String phone;
    private Button stop_scan;
    private String url;
    private int port;
    private ChargingDao chargingDao;
    private UserDao dao;
    private QRBizImpl qrActivityModel = new QRBizImpl();
    private QRPresenter presenter;
    public static String nodes = null;//充电参数
    public StringBuilder sb = new StringBuilder();//组装充电参数用
    private TextView qr_notes;
    private SweetAlertDialog pDialog;
    private static boolean backflag = false;
    Thread thread;
    private ScheduledExecutorService service;
    private Button button0;
    private RelativeLayout hideView;
    private boolean fastOrNot = false;//默认交流
    private String pileType;
    private TextView hideTip;
    private boolean countFlag = false;
    private AlertDialog dialog;
    private TextView textView;
    private int countIndex = 90;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        try {
            init();
            ActivityStorage.getInstance().addActivity(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        //第一步就启动相机
        Intent intent = new Intent(QRActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0x101);//REQUESTCODE定义一个整型做为请求对象标识
        initPresenter();
        setListener();
        hideSoftInput();
        initPhone();
        isChargingOrNot();
    }

    private void initView() {

    }

    /**
     * 初始化控制器
     */
    private void initPresenter() {
        QRBiz qrBiz = new QRBizImpl();
        presenter = new QRPresenter(this, this, qrBiz);
    }

    /**
     * 判断是否正在充电，在充电则显示“停止充电”界面，不在充电则显示“扫二维码”界面
     */
    private void isChargingOrNot() {
        if (ChargingDao.getInstance(this).findCharging() != null) {
            //在充电，显示“停止充电”界面
            scan.setVisibility(View.GONE);
            stop_scan.setVisibility(View.VISIBLE);
        } else {
            scan.setVisibility(View.VISIBLE);
            stop_scan.setVisibility(View.GONE);
        }
    }

    private void initPhone() {
        dao = new UserDao(this);
        phone = dao.findUser().getPhone();
    }

    private void setListener() {
        input_value = ((EditText) findViewById(R.id.input_value));//输入值
        chargingStyleView = (TextView) findViewById(R.id.button1);//选择充电方式
        hideView = (RelativeLayout) findViewById(R.id.hideView);//充电值隐藏框
        button0 = (Button) findViewById(R.id.autoBT);
        button1 = (Button) findViewById(R.id.button1);
        hideTip = (TextView) findViewById(R.id.hideTip);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        charging_tip = (TextView) findViewById(R.id.charging_tip);
        scan = (Button) findViewById(R.id.scan);
        stop_scan = (Button) findViewById(R.id.stop_scan);
        qr_notes = (TextView) findViewById(R.id.qr_notes);
        findViewById(R.id.back).setOnClickListener(this);

        button0.setOnClickListener(this);    //自动充满
        button1.setOnClickListener(this);    //时间充监听
        button2.setOnClickListener(this);    //电量充监听
        button3.setOnClickListener(this);    //金额充监听
        button4.setOnClickListener(this);    //进度充监听
        button5.setOnClickListener(this);    //辅助电源电压12V监听
        button6.setOnClickListener(this);    //辅助电源电压24V监听
        scan.setOnClickListener(this);        //扫码监听
        stop_scan.setOnClickListener(this); //结束充电监听
        charging_tip.setOnClickListener(this);//充电提示监听
        qr_notes.setOnClickListener(this);//充电参数查询
    }

    /**
     * 隐藏充电预设值软键盘
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input_value.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back://退出当前页面
                finish();
                break;
            case R.id.autoBT:     //自动充满监听
                button0();
                break;
            case R.id.button1:    //时间充监听
                button1();
                break;
            case R.id.button2:    //电量充监听
                button2();
                break;
            case R.id.button3:    //金额充监听
                button3();
                break;
            case R.id.button4:    //进度充监听
                button4();
                break;
            case R.id.button5:    //辅助电源电压12V
                button5();
                break;
            case R.id.button6:    //辅助电源电压24V
                button6();
                break;
            case R.id.charging_tip:    //辅助电源电压24V
                charging_tip();
                break;
            case R.id.scan://请求充电
                try {
                    scan();
                } catch (Exception e) {
                    ToastUtil.showToast(this, "开启充电失败！");
                    e.printStackTrace();
                }
                break;
            case R.id.stop_scan://请求停止充电
                try {
                    stop_scan();
                } catch (Exception e) {
                    ToastUtil.showToast(this, "结束充电失败！");
                    e.printStackTrace();
                }
                break;
            case R.id.qr_notes://充电参数
                qr_notes();
                break;
            default:
                break;
        }
    }

    /**
     * 充电参数
     */
    private void qr_notes() {
        if (nodes != null) {
            new SweetAlertDialog(this)
                    .setTitleText("充电参数")
                    .setContentText(nodes)
                    .show();
        } else {
            Notes n = ChargingDao.getInstance(this).findNotes();
            if (n != null) {
                new SweetAlertDialog(this)
                        .setTitleText("充电参数")
                        .setContentText(n.getNotes())
                        .show();
            } else {
                ToastUtil.showToast(this, "尚未充电");
            }
        }
    }

    /**
     * 充电前的提示内容
     */
    private void charging_tip() {
        charging_tip.setAlpha(0.5f);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                charging_tip.setAlpha(1f);
                //跳转到充电提示界面
                Intent intent = new Intent(QRActivity.this, TipActivity.class);
                startActivity(intent);
            }
        }, 100);
    }

    /**
     * 停止充电
     */
    private void stop_scan() {
        //设置“停止充电”按钮动画
        Animation anim = new ScaleAnimation(1, 1.4f, 1, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        stop_scan.startAnimation(anim);
        //向服务器发送停止充电指令
        url = Url.getInstance().getPath(20);
//        String data = ChargingDao.getInstance(this).findCharging().getPhoneDBData();
//        LogUtil.sop(TAG,"dataPackage="+data);
        presenter.stop_scan(url, phone, null);
    }

    /**
     * 执行扫码操作
     */
    private void scan() {
        //设置“扫二维码”按钮动画
        Animation anim = new ScaleAnimation(1, 1.4f, 1, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        scan.startAnimation(anim);
        //逻辑判断所选择的充电预设值是否正确、充电方式与辅助电源电压是否选择
        if (!fastOrNot) {//慢桩的情况
            button_ev = "00";
        }

        if (buttonStyleLastId == Integer.MAX_VALUE) {
            ToastUtil.showToast(QRActivity.this, "请选择充电方式");
        } else if (button_ev == null) {
            ToastUtil.showToast(QRActivity.this, "请选择辅助电源电压");
        } else if (button_port == null) {
            ToastUtil.showToast(QRActivity.this, "请选择充电端口");
        } else {
            if (buttonStyleLastId == 0) {//自动充满
                inputString = "0";
                inputHexString = "00";
                sendScan();
            } else {
                inputString = input_value.getText().toString();
                if (inputString.equals("")) {
                    ToastUtil.showToast(QRActivity.this, "请输入充电预设值");
                } else {
                    sendScan();//开启充电
                }
            }
        }
    }

    private void sendScan() {
        isLegalOrNot();
        //向服务器发送扫码充电请求
        if (legalOrNot) {
            //组装发送的数据json = phone + gateid + pileid + port + style + prevalue + ev
            JsonObject obj = new JsonObject();
            obj.addProperty("phone", phone);
            obj.addProperty("gateid", gateId);
            obj.addProperty("pileid", pileId);
            obj.addProperty("port", button_port);
            obj.addProperty("style", charging_type);
            obj.addProperty("prevalue", inputHexString);
            obj.addProperty("ev", button_ev);
            sb.delete(0, sb.length());//清空充电参数组装器
            switch (charging_type) {
                case "01":
                    sb.append("充电方式=时间充(时)\n");
                    break;
                case "02":
                    sb.append("充电方式=金额充(元)\n");
                    break;
                case "03":
                    sb.append("充电方式=电量充(KWH)\n");
                    break;
                case "04":
                    sb.append("充电方式=进度充(%)\n");
                    break;
            }
            sb.append("充电预设值=" + inputString + "\n辅助电源电压=" +
                    button_ev + "\n电桩编号=" + pileId + "\n充电端口=" + button_port);
            LogUtil.sop(TAG, sb.toString());
            nodes = sb.toString();
            url = Url.getInstance().getPath(15);
            presenter.scan(url, obj.toString(), nodes);//发送充电请求
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 0x103) {
            //输出扫码结果
            String result = data.getExtras().getString("result");  //网关、电桩编号、充电端口
            LogUtil.sop(TAG, "result=" + result);
            if (result == null) {
                ToastUtil.showToast(this, "尚未扫码");
                finish();
            } else {
                String[] str = result.split(",");
                if (str.length != 4) {
                    ToastUtil.showToast(QRActivity.this, "扫码结果：" + result);
                    finish();
                } else {
                    gateId = str[0];
                    pileId = str[1];
                    button_port = str[2];    //根据扫码结果获取：网关ID、电桩ID、电桩端口号、电桩类型
                    pileType = str[3];//电桩类型
                    if (pileType.equals("AC")) {
                        fastOrNot = false;//交流
                        findViewById(R.id.textView2).setVisibility(View.GONE);
                        findViewById(R.id.linear4).setVisibility(View.GONE);
                    } else {
                        fastOrNot = true;//直流
                    }
                    ((TextView) findViewById(R.id.pileidTV)).setText("电桩号：" + pileId);
                    ((TextView) findViewById(R.id.portTV)).setText("端口号：" + button_port);
                }
            }
        } else {
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("scan");
        MyApplication.getHttpQueue().cancelAll("stop_scan");
    }

    @Override
    protected void onDestroy() {
        backflag = false;
        super.onDestroy();

        //关闭handler
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 判断填写信息是否正确（充电方式，辅助电源电压，充电预设值）
     */
    private void isLegalOrNot() {
        List<Object> list = qrActivityModel.isLegalOrNot(this, legalOrNot, buttonStyleLastId, charging_type, inputString, inputHexString);
        legalOrNot = (boolean) list.get(0);
        buttonStyleLastId = (int) list.get(1);
        charging_type = (String) list.get(2);
        inputString = (String) list.get(3);
        inputHexString = (String) list.get(4);
    }

    /*********************以下都是界面按钮的UI设计*************************/

    /**
     * 设置最后一次选择的充电方式的字体颜色以及所选的buttonStyleLastId
     * 1、最后一次选择的充电方式字体颜色设置为黑色
     * 2、选择当前点击的充电方式视图为最后一次选择的充电方式视图
     * 3、设置最后一次点击的充电方式视图的字体颜色为红色
     */
    public void button1() {//时间充
        input_value.setText("");
        chargingStyleView.setTextColor(this.getResources().getColor(color.black));
        chargingStyleView = (TextView) findViewById(R.id.button1);
        chargingStyleView.setTextColor(this.getResources().getColor(color.red));
        buttonStyleLastId = 1;
        hideView.setVisibility(View.VISIBLE);

        hideTip.setText("时间：");
    }

    /**
     * 自动充满监听
     */
    private void button0() {
        inputString = "0";
        chargingStyleView.setTextColor(this.getResources().getColor(color.black));
        chargingStyleView = (TextView) findViewById(R.id.autoBT);
        chargingStyleView.setTextColor(this.getResources().getColor(color.red));
        buttonStyleLastId = 0;
        hideView.setVisibility(View.GONE);
    }

    /**
     * 功能与button1()类似
     */
    public void button2() {//电量充
        chargingStyleView.setTextColor(this.getResources().getColor(color.black));
        chargingStyleView = (TextView) findViewById(R.id.button2);
        chargingStyleView.setTextColor(this.getResources().getColor(color.red));
        buttonStyleLastId = 3;
    }

    /**
     * 功能与button1()类似
     */
    public void button3() {//金额充
        input_value.setText("");
        chargingStyleView.setTextColor(this.getResources().getColor(color.black));
        chargingStyleView = (TextView) findViewById(R.id.button3);
        chargingStyleView.setTextColor(this.getResources().getColor(color.red));
        buttonStyleLastId = 2;
        hideView.setVisibility(View.VISIBLE);

        hideTip.setText("金额：");
    }

    /**
     * 功能与button1()类似
     */
    public void button4() {//进度充
        chargingStyleView.setTextColor(this.getResources().getColor(color.black));
        chargingStyleView = (TextView) findViewById(R.id.button4);
        chargingStyleView.setTextColor(this.getResources().getColor(color.red));
        buttonStyleLastId = 4;
    }

    /**
     * 辅助电源电压12V监听
     */
    private void button5() {
        button_ev = "01";
        button6.setTextColor(this.getResources().getColor(color.black));
        button5.setTextColor(this.getResources().getColor(color.red));
    }

    /**
     * 辅助电源电压24V监听
     */
    private void button6() {
        button_ev = "02";
        button5.setTextColor(this.getResources().getColor(color.black));
        button6.setTextColor(this.getResources().getColor(color.red));
    }


    @Override
    public void show(String str) {
        ToastUtil.showToast(this, str);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x102) {
                textView.setText("" + countIndex--);//给界面设置请求时长
                if (countIndex >= 0) {
                    countFlag = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0x102);
                        }
                    }, 1000);
                } else {
                    countFlag = false;
                    countIndex = 90;
                    stopProgress();//关闭弹出框
                }
            }
        }
    };

    @Override
    public void showProgress() {
        if (layout == null) {
            layout = getLayoutInflater().inflate(R.layout.view_waitingdialog, null);
            textView = (TextView) layout.findViewById(R.id.textView);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
            dialog = builder.create();
            dialog.setView(layout);
        }
        if (!countFlag) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0x102);
                    countFlag = true;
                    dialog.show();
                }
            }, 0);
        } else {
            ToastUtil.showToast(this, "开启充电中，请稍后...");
        }
    }

    @Override
    public void stopProgress() {
        dialog.dismiss();
    }

    /**
     * 开启充电成功
     */
    @Override
    public void scan_success() {
        scan.setVisibility(View.GONE);
        stop_scan.setVisibility(View.VISIBLE);
        //将“开启充电”按钮变为“停止充电”
        scan.setVisibility(View.GONE);
        stop_scan.setVisibility(View.VISIBLE);
        JsonObject scheduleObj = new JsonObject();
        Intent intent;
        if (fastOrNot) {
            //直流充电桩的情况
            scheduleObj.addProperty("SOC", "0");
            scheduleObj.addProperty("out_v", "0");
            scheduleObj.addProperty("out_i", "0");
            scheduleObj.addProperty("phone", phone);
            scheduleObj.addProperty("pileid", pileId);
            scheduleObj.addProperty("port", button_port);
            intent = new Intent(QRActivity.this, ScheduleActivity.class);
            intent.putExtra("jsonstring", scheduleObj.toString());
            LogUtil.sop(TAG, "PileActivity->进入进度页面前的数据==" + scheduleObj.toString());
            startActivity(intent);
        } else {
            //交流充电桩的情况
            scheduleObj.addProperty("out_v", "0");
            scheduleObj.addProperty("out_i", "0");
            scheduleObj.addProperty("phone", phone);
            scheduleObj.addProperty("pileid", pileId);
            scheduleObj.addProperty("port", button_port);
            //充电开启时间设置为当前时间
            scheduleObj.addProperty("trans_minutes", "0");
            intent = new Intent(QRActivity.this, WatchActivity.class);
            intent.putExtra("jsonstring", scheduleObj.toString());
            LogUtil.sop(TAG, "PileActivity->进入进度页面前的数据==" + scheduleObj.toString());
            startActivity(intent);
        }
    }

    /**
     * 停止充电成功
     */
    @Override
    public void stop_success() {
        scan.setVisibility(View.VISIBLE);
        stop_scan.setVisibility(View.GONE);
    }

    /**
     * 停止充电失败
     */
    @Override
    public void stop_failed() {
        scan.setVisibility(View.VISIBLE);
        stop_scan.setVisibility(View.GONE);
    }
}
