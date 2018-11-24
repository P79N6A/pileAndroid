package com.swust.androidpile.qr.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swust.androidpile.R;
import com.swust.androidpile.alipay.PayResult;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.home.model.ApplyBillBizImpl;
import com.swust.androidpile.home.presenter.ApplyBillPresenter;
import com.swust.androidpile.home.view.ApplyBillView;
import com.swust.androidpile.qr.activity.QRActivity;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class QRFragment extends Fragment implements OnClickListener, ApplyBillView {

	private static final String TAG = "QRFragment";
	View vv;
	private TextView button1;
	private UserDao userDao;
	private int flag;
	private TextView alipay;
	private ApplyBillPresenter applyBillPresenter;
	private ApplyBillBizImpl applyBillBiz;
	private static final int SDK_PAY_FLAG = 1;
	private SweetAlertDialog pDialog;
	private boolean flag1=false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.fragment_qr, container,false);
		init(v);
		return v;
	}

	public void init(View v) {
		vv=v;
		//设置“点我扫码”button监听
		button1 = (TextView) v.findViewById(R.id.button1);
		button1.setOnClickListener(this);

		alipay = (TextView) v.findViewById(R.id.qr_alipay);
		alipay.setOnClickListener(this);

		applyBillPresenter = new ApplyBillPresenter();
		applyBillBiz = new ApplyBillBizImpl();
		applyBillPresenter.setViewAndModel(vv.getContext(), this, applyBillBiz);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.button1:
				initButton1();
				break;
			case R.id.qr_alipay:
				alipay();
				break;
		}
	}

	private void alipay() {
//		pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
//		pDialog.getProgressHelper().setBarColor( getResources().getColor(R.color.barprogress));
//		pDialog.setTitleText("请稍后！");
//		pDialog.show();
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				pDialog.dismiss();
//			}
//		},2000);

//		applyBillPresenter.applyChargingBill(Url.getInstance().getPath(17));
	}

	private void initButton1() {
		userDao = new UserDao(vv.getContext());
		flag = userDao.findLoginState();
		button1.setAlpha(0.5f);
		if(flag==1){
			//透明度设置
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					button1.setAlpha(1f);
					//跳转到扫码充电界面
					Intent intent = new Intent(getActivity(),QRActivity.class);
					startActivity(intent);
				}
			}, 100);
		}else{
			new Handler().postDelayed(new Runnable(){
				@Override
				public void run() {
					button1.setAlpha(1f);
					//没有登陆，提示请先登陆
					ToastUtil.showToast(vv.getContext(), "请先登陆");
				}
			}, 100);
		}
	}

	@Override
	public void show(String msg) {

	}

	@Override
	public void getChargingBillInfo(String msg) {
		LogUtil.sop(TAG,"msg="+msg);
		JsonParser parser = new JsonParser();
		final JsonObject obj = (JsonObject) parser.parse(msg);

		new Thread(new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(getActivity());
				Map<String, String> result = alipay.payV2(
						obj.get("orderInfo").getAsString(), true);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}).start();
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
					LogUtil.sop(TAG,"resultInfo= " + resultInfo);
					String resultStatus = payResult.getResultStatus();
					// 判断resultStatus 为9000则代表支付成功
					LogUtil.sop(TAG, "resultStatus=" + resultStatus);
					if (TextUtils.equals(resultStatus, "9000")) {
						// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
						ToastUtil.showToast(vv.getContext(), "支付成功");
					} else {
						// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
						ToastUtil.showToast(vv.getContext(), "支付失败");
					}
					break;
				}
				default:
					break;
			}
		}
	};

	@Override
	public void getMoneyBillInfo(String msg) {

	}
}
