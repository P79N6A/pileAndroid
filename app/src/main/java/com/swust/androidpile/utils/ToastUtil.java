package com.swust.androidpile.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil {

	private static Toast mToast=null;

	public static void showToast(Context context,String msg){
		if(mToast==null){
			mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);//让时间显示的长点
		}else{
			mToast.setText(msg);
		}
		mToast.show();
	}

	private static Float dpDensity; //屏幕密度
	private static Float spDensity; //屏幕密度


	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * @return
	 */
	public static int sp2px(Context context,float spValue) {
		if(spDensity==null){
			spDensity = context.getResources().getDisplayMetrics().scaledDensity;
		}
		return (int) (spValue * spDensity);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dp2px(Context context,float dpValue) {
		if(dpDensity==null){
			dpDensity = context.getResources().getDisplayMetrics().density;
//    		Log.i("test", ""+dpDensity);
		}
		return (int)(dpValue * dpDensity);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dp(Context context,float pxValue) {
		if(dpDensity==null){
			dpDensity = context.getResources().getDisplayMetrics().density;
		}
		return (int)(pxValue / dpDensity + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * @return
	 */
	public static int px2sp(Context context,float pxValue) {
		if(spDensity==null){
			spDensity = context.getResources().getDisplayMetrics().scaledDensity;
		}
		return (int) (pxValue / spDensity + 0.5f);    //44px
	}

}
