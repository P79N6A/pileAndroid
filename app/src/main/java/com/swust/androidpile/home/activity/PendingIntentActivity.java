package com.swust.androidpile.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alipay.sdk.app.EnvUtils;
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

public class PendingIntentActivity extends AppCompatActivity implements ApplyBillView, View.OnClickListener {

    private ApplyBillPresenter applyBillPresenter;
    private ApplyBillBizImpl applyBillBiz;
    private static final String TAG = "PendingIntentActivity";

    /**
     * 支付宝支付业务：入参app_id
     */
    private static final int SDK_PAY_FLAG = 1;
    private Button alipayBT;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_intent);
        initView();
        initListener();
        initPresenter();
        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);//声明是沙箱支付宝操作
    }

    private void initView() {
        alipayBT = (Button) findViewById(R.id.alipayBT);
        backIV = (ImageView) findViewById(R.id.back);
    }

    private void initListener() {
        alipayBT.setOnClickListener(this);
        backIV.setOnClickListener(this);
    }

    private void initPresenter() {
        applyBillPresenter = new ApplyBillPresenter();
        applyBillBiz = new ApplyBillBizImpl();
        applyBillPresenter.setViewAndModel(this, this, applyBillBiz);
    }

    @Override
    public void show(String msg) {
        ToastUtil.showToast(this, msg);//目前是系统故障才会显示
    }

    /**
     * 接收订单信息
     *
     * @param msg
     */
    @Override
    public void getChargingBillInfo(String msg) {
        LogUtil.sop(TAG, msg);
        JsonParser parser = new JsonParser();
        final JsonObject obj = (JsonObject) parser.parse(msg);

        new Thread(new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(PendingIntentActivity.this);
                Map<String, String> result = alipay.payV2(
                        obj.get("orderInfo").getAsString(), true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void getMoneyBillInfo(String msg) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alipayBT://申请订单信息
                applyBillPresenter.applyChargingBill(Url.getInstance().getPath(17));
                break;
            case R.id.back://退出当前活动页
                finish();
                break;
            default:
                break;
        }
    }

    //得到订单信息，进行支付
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    LogUtil.sop(TAG, resultInfo);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    LogUtil.sop(TAG, resultStatus);
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtil.showToast(PendingIntentActivity.this, "支付成功");
                        finish();//退出当前活动页
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtil.showToast(PendingIntentActivity.this, "支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}
