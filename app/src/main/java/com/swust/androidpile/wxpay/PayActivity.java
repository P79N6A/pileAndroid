package com.swust.androidpile.wxpay;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.home.model.ApplyBillBizImpl;
import com.swust.androidpile.home.presenter.ApplyBillPresenter;
import com.swust.androidpile.home.view.ApplyBillView;
import com.swust.androidpile.main.ActivityStorage;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;
import com.swust.androidpile.utils.Url;
import com.tencent.mm.opensdk.constants.*;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class PayActivity extends Activity implements View.OnClickListener, TextWatcher {

    public static JsonObject orderJson;//订单信息
    private IWXAPI iwxapi; //微信支付api
    private static final String TAG = "RechargeActivity";
    private TextView moneyPay;
    private AutoCompleteTextView otherMoney;
    private Button tenTV;
    private Button fiftyTV;
    private Button hundred1TV;
    private Button hundred3TV;
    private Button lastButton;
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
        ActivityStorage.getInstance().addActivity(this);
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
                    LogUtil.sop(TAG, "money=" + money);
                    String url = "http://47.95.210.136:8080/ChargingPile/wxpay";
//                    String url = "http://192.168.124.24:8080/ChargingPile/wxpay";
                    UserDao dao = new UserDao(this);
                    User user = dao.findUser();
                    if (user != null) {
                        order(url, user.getPhone());
                    } else {
                        ToastUtil.showToast(this, "请先登陆/注册");
                    }
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
//        //软键盘消失
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    private void order(String url, final String phone) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonstring) {
                        Log.i("test", jsonstring);
                        toWXPay(jsonstring);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError arg0) {
                Log.i("test", "error");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                JsonObject obj = new JsonObject();
                obj.addProperty("phone", phone);
                obj.addProperty("money", String.valueOf(money));
                map.put("jsonstring", obj.toString());
                return map;
            }
        };
        request.setTag("PayActivity");
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(PayActivity.this).add(request);
    }

    /**
     * 调起微信支付的方法
     **/
    private void toWXPay(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        final JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
        String code = jsonObject.get("code").getAsString();
        if (code.equals("SUCCESS")) {
            iwxapi = WXAPIFactory.createWXAPI(this, null); //初始化微信api
            iwxapi.registerApp(Constants.APP_ID); //注册appid  appid可以在开发平台获取
            orderJson = jsonObject.get("order").getAsJsonObject();
            orderJson.addProperty("money", money);
            Runnable payRunnable = new Runnable() {  //这里注意要放在子线程
                @Override
                public void run() {
                    PayReq request = new PayReq(); //调起微信APP的对象
                    //下面是设置必要的参数，也就是前面说的参数,这几个参数从何而来请看上面说明
                    request.appId = orderJson.get("appid").getAsString();
                    request.partnerId = orderJson.get("partnerid").getAsString();
                    request.nonceStr = orderJson.get("noncestr").getAsString();
                    request.timeStamp = orderJson.get("timestamp").getAsString();
                    request.packageValue = orderJson.get("package").getAsString();
                    request.prepayId = orderJson.get("prepayid").getAsString();
                    request.sign = orderJson.get("sign").getAsString();
                    iwxapi.sendReq(request);//发送调起微信的请求
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        } else {
            ToastUtil.showToast(PayActivity.this, "请求支付订单失败！");
        }
    }

    @Override
    public void finish() {
        Log.i("test", "PayActivity is finishing");
        Intent getIntent = getIntent();
        getIntent.putExtra("moneyPay", money);
        if (orderJson != null) {
            getIntent.putExtra("errCode", orderJson.get("errCode").getAsInt());
        } else {
            getIntent.putExtra("errCode", -1);
        }
        setResult(0x111, getIntent);
        super.finish();
    }
}
