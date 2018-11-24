package com.swust.androidpile.mine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.alipay.PayResult;
import com.swust.androidpile.home.model.ApplyBillBizImpl;
import com.swust.androidpile.home.presenter.ApplyBillPresenter;
import com.swust.androidpile.home.view.ApplyBillView;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.utils.ToastUtil;

import java.util.Map;

public class RechargeActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, ApplyBillView {

    private static final String TAG = "RechargeActivity";
    private TextView moneyPay;
    private AutoCompleteTextView otherMoney;
    private Button tenTV;
    private Button fiftyTV;
    private Button hundred1TV;
    private Button hundred3TV;
    private Button lastButton;
    private ApplyBillPresenter applyBillPresenter;
    private ApplyBillBizImpl applyBillBiz;
    private static final int SDK_PAY_FLAG = 1;
    private Button starPay;
    private ImageView backIV;
    private double money = Double.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        initView();
        initListener();
        initPresenter();
        com.swust.androidpile.main.ActivityStorage.getInstance().addActivity(this);
//        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//声明是沙箱支付宝操作
    }

    private void initView() {
        moneyPay = (TextView) findViewById(R.id.moneyPay);
        otherMoney = (AutoCompleteTextView) findViewById(R.id.otherMoney);
        tenTV = (Button) findViewById(R.id.button1);
        fiftyTV = (Button) findViewById(R.id.button2);
        hundred1TV = (Button) findViewById(R.id.button3);
        hundred3TV = (Button) findViewById(R.id.button4);
        starPay = (Button) findViewById(R.id.starPay);
        backIV = (ImageView) findViewById(R.id.backIV);
        //初始化最后点击的按钮样式
        lastButton = tenTV;
    }

    private void initListener() {
        moneyPay.setOnClickListener(this);
        tenTV.setOnClickListener(this);
        fiftyTV.setOnClickListener(this);
        hundred1TV.setOnClickListener(this);
        hundred3TV.setOnClickListener(this);
        otherMoney.addTextChangedListener(this);
        starPay.setOnClickListener(this);
        backIV.setOnClickListener(this);
    }

    private void initPresenter() {
        applyBillPresenter = new ApplyBillPresenter();
        applyBillBiz = new ApplyBillBizImpl();
        applyBillPresenter.setViewAndModel(this, this, applyBillBiz);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backIV:
                finish();
                break;
            case R.id.button1:
                lastButton.setTextColor(getResources().getColor(R.color.black));
                tenTV.setTextColor(getResources().getColor(R.color.red));
                lastButton = tenTV;

                otherMoney.setText("");
                moneyPay.setText("10");
                moneyPay.setTextColor(Color.RED);
                money = 10;
                break;
            case R.id.button2:
                lastButton.setTextColor(getResources().getColor(R.color.black));
                fiftyTV.setTextColor(getResources().getColor(R.color.red));
                lastButton = fiftyTV;

                otherMoney.setText("");
                moneyPay.setText("50");
                moneyPay.setTextColor(Color.RED);
                money = 50;
                break;
            case R.id.button3:
                lastButton.setTextColor(getResources().getColor(R.color.black));
                hundred1TV.setTextColor(getResources().getColor(R.color.red));
                lastButton = hundred1TV;

                otherMoney.setText("");
                moneyPay.setText("100");
                moneyPay.setTextColor(Color.RED);
                money = 100;
                break;
            case R.id.button4:
                lastButton.setTextColor(getResources().getColor(R.color.black));
                hundred3TV.setTextColor(getResources().getColor(R.color.red));
                lastButton = hundred3TV;

                otherMoney.setText("");
                moneyPay.setText("300");
                moneyPay.setTextColor(Color.RED);
                money = 300;
                break;
            case R.id.starPay:
                if (money == Double.MAX_VALUE) {
                    ToastUtil.showToast(this, "请输入充值金额");
                } else {
                    LogUtil.sop(TAG,"money=" + money);
                    applyBillPresenter.applyMoneyBill(Url.getInstance().getPath(18), money);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = otherMoney.getText().toString();
        if (text != null && !text.equals("")) {
            lastButton.setTextColor(getResources().getColor(R.color.black));
            moneyPay.setText(otherMoney.getText());
            moneyPay.setTextColor(Color.RED);
            try {
                money = Double.parseDouble(otherMoney.getText().toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                ToastUtil.showToast(this, "数值格式不正确！");
                money = Double.MAX_VALUE;
            }
        }
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this
                    .getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void show(String msg) {
        ToastUtil.showToast(this, msg);//目前是系统故障才会显示
    }

    @Override
    public void getChargingBillInfo(String msg) {

    }

    @Override
    public void getMoneyBillInfo(String msg) {
        LogUtil.sop(TAG, "下发的充电订单-->" + msg);
        JsonParser parser = new JsonParser();
        final JsonObject obj = (JsonObject) parser.parse(msg);

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
                Map<String, String> result = alipay.payV2(obj.get("orderInfo").getAsString(), true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    //得到订单信息，进行支付
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            LogUtil.sop(TAG,"支付结果："+msg.obj);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     *对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        Intent getIntent = getIntent();
                        getIntent.putExtra("moneyPay", money);
                        setResult(0x110, getIntent);

                        Intent intent = new Intent(RechargeActivity.this,BillDetailActivity.class);
                        intent.putExtra("jsonstring",resultInfo);
                        startActivity(intent);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
    };
}
