package com.swust.androidpile.mine.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.swust.androidpile.R;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.wxpay.Constants;
import com.swust.androidpile.wxpay.PayActivity;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.text.DecimalFormat;

public class AccountInfoActivity extends Activity implements OnClickListener {

    private static final String TAG = "AccountInfoActivity";
    private double money;
    private TextView textMoney;
    private ImageView image1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_account_info);
        initView();
        initListener();
    }

    private void initView() {
        textMoney = (TextView) findViewById(R.id.money);
        image1 = (ImageView) findViewById(R.id.image1);

        money = getIntent().getDoubleExtra("money", -1);
        textMoney.setText("" + money);
    }

    private void initListener() {
        image1.setOnClickListener(this);
        findViewById(R.id.linear3).setOnClickListener(this);
        findViewById(R.id.linear4).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image1:
                finish();
                break;
            case R.id.linear3://"支付宝充值"监听
//                ToastUtil.showToast(this, "敬请期待！");
                Intent intent = new Intent(AccountInfoActivity.this, RechargeActivity.class);
                startActivityForResult(intent, 0x101);
                break;
            case R.id.linear4://"微信充值"监听
                IWXAPI iwxapi = WXAPIFactory.createWXAPI(this, null); //初始化微信api
                iwxapi.registerApp(Constants.APP_ID); //注册appid  appid可以在开发平台获取
                boolean loginFlag = iwxapi.isWXAppInstalled();//判断是否安装了微信
                if (loginFlag) {
                    Intent intent1 = new Intent(AccountInfoActivity.this, PayActivity.class);
                    startActivityForResult(intent1, 0x102);
                } else {
                    ToastUtil.showToast(this, "请先安装微信客户端！");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0x110) {//支付宝充值界面返回
            double moneyPay = data.getDoubleExtra("moneyPay", -1);
            LogUtil.sop(TAG, "moneyPay=" + moneyPay + " ,money=" + money);
            //如果支付了金额，则需要修改页面中的金额
            if (moneyPay != -1) {
                DecimalFormat df = new DecimalFormat("0.00");
                textMoney.setText(String.valueOf(Double.parseDouble(df.format((money + moneyPay)))));
                money = money + moneyPay;
            }
        } else if (resultCode == 0x111) {//微信支付界面返回
            int errCode = data.getIntExtra("errCode", -1);
            if (errCode != -1 && errCode == 0) {
                double moneyPay = data.getDoubleExtra("moneyPay", -1);
                LogUtil.sop(TAG, "moneyPay=" + moneyPay + " ,money=" + money);
                if (moneyPay != Double.MAX_VALUE) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    textMoney.setText(String.valueOf(Double.parseDouble(df.format((money + moneyPay)))));
                    money = money + moneyPay;
                }
            }
        }
    }
}
