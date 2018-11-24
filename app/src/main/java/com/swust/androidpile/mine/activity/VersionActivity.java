package com.swust.androidpile.mine.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.swust.androidpile.R;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.main.UpdateManager;
import com.swust.androidpile.mine.model.MineListener;
import com.swust.androidpile.utils.IMEIUtil;
import com.swust.androidpile.utils.ThreeDESUtil;
import com.swust.androidpile.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VersionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView versionTV;
    private Button update;
    private ImageView backIV;
    private static final String TAG = "VersionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        initView();
        initListener();
        initVersionCode();
    }

    private void initListener() {
        update.setOnClickListener(this);
        backIV.setOnClickListener(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        versionTV = (TextView) findViewById(R.id.versionTV);
        update = (Button) findViewById(R.id.updateBT);
        backIV = (ImageView) findViewById(R.id.back);
    }

    /**
     * 初始化版本号
     */
    private void initVersionCode() {
        try {
            String versionName = this.getPackageManager().getPackageInfo("com.swust.androidpile", 0).versionName;
            versionTV.setText("V" + versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.updateBT://检查版本更新
                new UpdateManager(view.getContext()).checkUpdate(TAG);
//                test();
                break;
            case R.id.back://返回键监听
                finish();
                break;
            default:
                break;
        }
    }

    private void test() {
        String url = "http://192.168.0.119:8080/ChargingPile/test.action";
        Map<String, String> map = new HashMap<String, String>();
        map.put("phone", "17309045310");
        JSONObject jsob = new JSONObject(map);

        //volley网络请求框架拦截器
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsob,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i("test", jsonObject.toString());
                        try {
                            String phone = jsonObject.getString("phone");
                            Log.i("test", "phone=" + phone);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        MyApplication.getHttpQueue().add(request);
    }
}