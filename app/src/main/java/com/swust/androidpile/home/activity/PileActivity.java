package com.swust.androidpile.home.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.R.color;
import com.swust.androidpile.dao.ChargingDao;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.Notes;
import com.swust.androidpile.home.model.ChargingBiz;
import com.swust.androidpile.home.model.ChargingBizImpl;
import com.swust.androidpile.home.presenter.ChargingPresenter;
import com.swust.androidpile.home.view.ChargingView;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.qr.activity.QRActivity;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PileActivity extends Activity implements OnClickListener, ChargingView {

    private static final String TAG = "PileActivity";
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
    private ArrayList<String> list;
    private Button port1;
    private Button port2;
    private UserDao dao;
    private ImageView back;
    private ChargingDao chargingDao;
    private ChargingPresenter chargingPresenter;
    private ChargingBiz chargingModel;
    private ProgressDialog progressDialog;
    public static String nodes = null;//充电参数
    private StringBuilder sb = new StringBuilder();
    private TextView pile_notes;
    private SweetAlertDialog pDialog;
    private TextView pile_state;
    private Button button0;
    private RelativeLayout hideView;
    private TextView text5;
    private boolean fastOrNot = false;//默认是慢桩
    private String pileType;
    private TextView hideTip;
    private View layout;
    private TextView textView;
    private AlertDialog dialog;
    private boolean countFlag = false;
    private int countIndex = 90;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pilelist2);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityStorage.getInstance().addActivity(this);
    }

    private void init() {
        initView();
        initPresenter();
        initData();
        hideSoftInput();
        initPhone();
        setListener();
        isChargingOrNot();
    }

    private void initView() {
        text5 = (TextView) findViewById(R.id.textView55);
        pile_state = (TextView) findViewById(R.id.pile_state);
        charging_tip = (TextView) findViewById(R.id.charging_tip);
        chargingStyleView = (TextView) findViewById(R.id.button1);//默认充电方式
        hideView = (RelativeLayout) findViewById(R.id.hideView);//充电值隐藏框
        button0 = (Button) findViewById(R.id.autoBT);
        button1 = (Button) findViewById(R.id.button1);
        hideTip = (TextView) findViewById(R.id.hideTip);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        port1 = (Button) findViewById(R.id.port1);//端口01
        port2 = (Button) findViewById(R.id.port2);//端口02
        stop_scan = (Button) findViewById(R.id.stop_scan);//结束充电按钮
        scan = (Button) findViewById(R.id.scan);//开始充电按钮
        back = (ImageView) findViewById(R.id.back);
        pile_notes = (TextView) findViewById(R.id.pile_notes);
    }

    private void initPresenter() {
        chargingPresenter = new ChargingPresenter();
        chargingModel = new ChargingBizImpl();
        chargingPresenter.setViewAndModel(this, this, chargingModel);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = getIntent().getStringArrayListExtra("pileInfo");
        gateId = list.get(4);    //电桩所属的网关ID
        pileId = list.get(1); //电桩ID
        pileType = list.get(2);//电桩类型
        LogUtil.sop(TAG, "电桩类型"+pileType);
        chargingDao = ChargingDao.getInstance(this);
        dao = UserDao.getInstance(this);

        ((TextView) findViewById(R.id.textView22)).setText(list.get(1));    //电桩编号

        //配置电桩类型与右侧的快、慢图标
        ((TextView) findViewById(R.id.textView44)).setText(pileType);    //电桩类型
        if (pileType.split("直流").length == 2) {
            ((TextView) findViewById(R.id.pile_type)).setText("快");
            fastOrNot = true;//快桩
        }

        /*开始电桩状态设置*/
        String state = list.get(3);
        text5.setText(state);
        //隐藏非空闲的端口、充电选项，以及右上角的提示
        String[] split = state.split("---");
        if (split[0].equals("端口1:空闲") && split[1].equals("端口2:空闲")) {
            LogUtil.sop(TAG,"双端口空闲");
        } else if (split[0].equals("端口1:空闲")) {
            port2.setVisibility(View.GONE);//1口空闲
            LogUtil.sop(TAG,"端口1空闲");
        } else if (split[1].equals("端口2:空闲")) {
            port1.setVisibility(View.GONE);//2口空闲
            LogUtil.sop(TAG,"端口2空闲");
        } else {
            findViewById(R.id.linear9).setVisibility(View.GONE);//所有充电操作的界面
            charging_tip.setVisibility(View.GONE);//右上角充电提示关闭
            pile_state.setText("占用中");//电桩状态
            LogUtil.sop(TAG,"双端口占用");
        }
        //隐藏交流电桩时，辅助电源电压的选择
        if (!fastOrNot) {
            findViewById(R.id.pileTypeTV).setVisibility(View.GONE);
            findViewById(R.id.linear8).setVisibility(View.GONE);
        }
        /*结束电桩状态设置*/
//        ((TextView) findViewById(R.id.textView7)).setText(list.get(0));    //充电站名
    }

    /**
     * 隐藏充电预设值软键盘
     */
    private void hideSoftInput() {
        input_value = ((EditText) findViewById(R.id.input_value));
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input_value.getWindowToken(), 0);
    }

    /**
     * 初始化dao并获得电话号码
     */
    private void initPhone() {
        phone = dao.findUser().getPhone();
    }

    /**
     * 设置监听
     */
    private void setListener() {
        back.setOnClickListener(this);        //返回按钮监听
        button0.setOnClickListener(this);    //自动充满监听
        button1.setOnClickListener(this);    //时间充监听
        button2.setOnClickListener(this);    //电量充监听
        button3.setOnClickListener(this);    //金额充监听
        button4.setOnClickListener(this);    //进度充监听
        button5.setOnClickListener(this);    //辅助电源电压12V监听
        button6.setOnClickListener(this);    //辅助电源电压24V监听
        port1.setOnClickListener(this);    //端口01监听
        port2.setOnClickListener(this);    //端口02监听
        scan.setOnClickListener(this);        //扫码监听
        stop_scan.setOnClickListener(this);  //结束充电监听
        charging_tip.setOnClickListener(this);//充电提示监听
        pile_notes.setOnClickListener(this);//充电参数查询监听
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
            case R.id.port1:    //辅助电源电压24V
                port1();
                break;
            case R.id.port2:    //辅助电源电压24V
                port2();
                break;
            case R.id.charging_tip:    //辅助电源电压24V
                charging_tip();
                break;
            case R.id.scan://开启充电
                try {
                    scan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.stop_scan://结束充电
                try {
                    stop_scan();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.pile_notes:
                pile_notes();
                break;
            default:
                break;
        }
    }

    /**
     * 充电参数查询
     */
    private void pile_notes() {
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
     * 执行停止充电操作
     */
    private void stop_scan() {
        //设置“停止充电”按钮动画
        Animation anim = new ScaleAnimation(1, 1.4f, 1, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        stop_scan.startAnimation(anim);
        //向服务器发送停止充电指令
        url = Url.getInstance().getPath(20);//之前是14，现在20
//        String data = ChargingDao.getInstance(this).findCharging().getPhoneDBData();
//        LogUtil.sop(TAG,"data="+data);
        chargingPresenter.stopCharging(url, phone, null);
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
            ToastUtil.showToast(PileActivity.this, "请选择充电方式");
        } else if (button_ev == null) {
            ToastUtil.showToast(PileActivity.this, "请选择辅助电源电压");
        } else if (button_port == null) {
            ToastUtil.showToast(PileActivity.this, "请选择充电端口");
        } else {
            if (buttonStyleLastId == 0) {//自动充满
                inputString = "0";
                inputHexString = "00";
                chargingPresenter.isLegalOrNot(legalOrNot, buttonStyleLastId, charging_type, inputString, inputHexString);
            } else {
                inputString = input_value.getText().toString();
                if (inputString.equals("")) {
                    ToastUtil.showToast(PileActivity.this, "请输入充电预设值");
                } else {
                    chargingPresenter.isLegalOrNot(legalOrNot, buttonStyleLastId, charging_type, inputString, inputHexString);
                }
            }
        }
    }

    /**
     * 判断是否正在充电，在充电则显示“停止充电”界面，不在充电则显示“扫二维码”界面
     */
    private void isChargingOrNot() {
        if (chargingDao.findCharging() != null) {
            //在充电，显示“停止充电”界面
            scan.setVisibility(View.GONE);
            stop_scan.setVisibility(View.VISIBLE);
        } else {
            scan.setVisibility(View.VISIBLE);
            stop_scan.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getHttpQueue().cancelAll("scan");
        MyApplication.getHttpQueue().cancelAll("stop_scan");
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

    /*自动充满*/
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

    private void port1() {
        button_port = "01";
        port2.setTextColor(this.getResources().getColor(color.black));
        port1.setTextColor(this.getResources().getColor(color.red));
    }

    private void port2() {
        button_port = "02";
        port1.setTextColor(this.getResources().getColor(color.black));
        port2.setTextColor(this.getResources().getColor(color.red));
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
                Intent intent = new Intent(PileActivity.this, TipActivity.class);
                startActivity(intent);
            }
        }, 100);
    }

    @Override
    public void show(String jsonstring) {
        ToastUtil.showToast(this, jsonstring);
    }

    @Override
    public void isLegalOrNot(List<Object> list) {
        legalOrNot = (boolean) list.get(0);
        buttonStyleLastId = (int) list.get(1);
        charging_type = (String) list.get(2);
        inputString = (String) list.get(3);
        inputHexString = (String) list.get(4);
        //向服务器发送充电请求
        if (legalOrNot) {
            try {
                JsonObject obj = new JsonObject();
                obj.addProperty("phone", phone);
                obj.addProperty("gateid", gateId);
                obj.addProperty("pileid", pileId);
                obj.addProperty("port", button_port);
                obj.addProperty("style", charging_type);
                obj.addProperty("prevalue", inputHexString);
                if (button_ev == null) {
                    button_ev = "00";
                    obj.addProperty("ev", "00");
                } else {
                    obj.addProperty("ev", button_ev);
                }
                url = Url.getInstance().getPath(15);
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
                        ((button_ev.equals("01")) ? "12V" : "24V") + "\n电桩编号=" + pileId + "\n充电端口=" + button_port);
                QRActivity.nodes = sb.toString();
                chargingPresenter.startCharging(url, obj.toString(), QRActivity.nodes);//返回startCharging(JsonObject obj)方法
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtil.showToast(this, "请求开启充电失败！");
            }
        }
    }

    @Override
    public void stopProgress() {
        dialog.dismiss();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x101) {
                textView.setText("" + countIndex--);//给界面设置请求时长
                if (countIndex >= 0) {
                    countFlag = true;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0x101);
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
                    handler.sendEmptyMessage(0x101);
                    countFlag = true;
                    dialog.show();
                }
            }, 0);
        } else {
            ToastUtil.showToast(this, "开启充电中，请稍后...");
        }
    }

    @Override
    public void startCharging(JsonObject obj) {
        //将“开启充电”按钮变为“停止充电”
        scan.setVisibility(View.GONE);
        stop_scan.setVisibility(View.VISIBLE);

//        ActivityStorage.getInstance().deleteAll();//删除所有activity,回到主界面
        JsonObject scheduleObj = new JsonObject();
        Intent intent;
        if (fastOrNot) {
            //直流充电桩的情况
            scheduleObj.addProperty("SOC", "0");
            scheduleObj.addProperty("out_v", "0");
            scheduleObj.addProperty("out_i", "0");
            scheduleObj.addProperty("phone", phone);
            scheduleObj.addProperty("pileid", list.get(1));
            scheduleObj.addProperty("port", button_port);
            intent = new Intent(PileActivity.this, ScheduleActivity.class);
            intent.putExtra("jsonstring", scheduleObj.toString());
            LogUtil.sop(TAG,"进入进度页面前的数据==" + scheduleObj.toString());
            startActivity(intent);
        } else {
            //交流充电桩的情况
            scheduleObj.addProperty("out_v", "0");
            scheduleObj.addProperty("out_i", "0");
            scheduleObj.addProperty("phone", phone);
            scheduleObj.addProperty("pileid", list.get(1));
            scheduleObj.addProperty("port", button_port);
            //充电开启时间设置为当前时间
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//定义格式，不显示毫秒
//            Timestamp ts = new Timestamp(System.currentTimeMillis());
//            String str = df.format(ts);
            scheduleObj.addProperty("trans_minutes", "0");
            intent = new Intent(PileActivity.this, WatchActivity.class);
            intent.putExtra("jsonstring", scheduleObj.toString());
            LogUtil.sop(TAG,"进入进度页面前的数据==" + scheduleObj.toString());
            startActivity(intent);
        }
    }

    @Override
    public void stopCharging() {
        scan.setVisibility(View.VISIBLE);
        stop_scan.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭handler
        handler.removeCallbacksAndMessages(null);
    }
}