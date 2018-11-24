package com.swust.androidpile.home.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.swust.androidpile.R;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.ToastUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LocationFragment extends BaseMapFragment implements LocationSource,
        AMapLocationListener {

    private static final String TAG = "LocationFragment";
    public OnLocationChangedListener mListener;
    public AMapLocationClient mlocationClient;
    public AMapLocationClientOption mLocationOption;
    public String city;
    public LatLng lp;//定位点经纬度

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (lp == null) {
            init();
        }
        return view;
    }

    public void init() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
//        //设置定位蓝点的icon图标方法，需要用到BitmapDescriptor类对象作为参数。
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(
//                BitmapDescriptorFactory.fromBitmap(
//                        BitmapFactory.decodeResource(getResources(), R.mipmap.icon_zidingyi)));
//        myLocationStyle.strokeColor(R.color.white);
//        myLocationStyle.radiusFillColor(R.color.white);
//        myLocationStyle.strokeWidth(0);
//        aMap.setMyLocationStyle(myLocationStyle);
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        deactivate();//停止定位
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                //定位测试，输出当前维度、经度及城市名--》31.541521、？、  绵阳市
                LogUtil.sop(TAG, "纬度：" + String.valueOf(amapLocation.getLatitude()) + "，"
                        + "经度：" + String.valueOf(amapLocation.getLongitude()) + ",城市名：" + amapLocation.getCity()
                        + ",城市地址：" + amapLocation.getAddress() + ",城市码元：" + amapLocation.getCityCode() + ",定位详情" + amapLocation.getLocationDetail());
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                city = amapLocation.getCity();
                lp = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (lp != null) {
                    mlocationClient.stopLocation();//取到了初始经纬度，所以停止定位
//                    LatLngBounds bounds = LatLngBounds.builder().include(lp).build();
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));  //地图的缩放级别：3--19
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(lp));
                    callBack.getInfo(lp, city, aMap);//回调坐标、城市名、地图对象
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                LogUtil.sop(TAG, errText);
                ToastUtil.showToast(getActivity(), "定位失败，请检查网络");
                deactivate();//停止定位
                //前台提示定位失败
                callBack.getError();
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
//设置定位监听
            mlocationClient.setLocationListener(this);
//设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
//设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
// 开始定位
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /********************这里是回调监听定位********************/
    private CallBack callBack;

    public interface CallBack {
        void getInfo(LatLng lp, String cityName, AMap aMap);

        void getError();
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


}