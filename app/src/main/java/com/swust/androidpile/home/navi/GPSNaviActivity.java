package com.swust.androidpile.home.navi;

import android.content.Intent;
import android.os.Bundle;

import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.enums.NaviType;
import com.swust.androidpile.R;

/**
 * 创建时间：11/11/15 18:50
 * 项目名称：newNaviDemo
 *
 * @author lingxiang.wang
 * @email lingxiang.wang@alibaba-inc.com
 * 类说明：
 */

public class GPSNaviActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_navi);
        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        //接收起始与终止坐标，GPS实时导航，默认汽车路径
        Intent intent = getIntent();
        mStartLatlng = intent.getParcelableExtra("mStartLatlng");
        mEndLatlng = intent.getParcelableExtra("mEndLatlng");
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);
    }

//    /**
//     * 如果使用无起点算路，请这样写
//     */
//    private void noStartCalculate() {
//        //无起点算路须知：
//        //AMapNavi在构造的时候，会startGPS，但是GPS启动需要一定时间
//        //在刚构造好AMapNavi类之后立刻进行无起点算路，会立刻返回false
//        //给人造成一种等待很久，依然没有算路成功 算路失败回调的错觉
//        //因此，建议，提前获得AMapNavi对象实例，并判断GPS是否准备就绪
//
//        if (mAMapNavi.isGpsReady())
//            mAMapNavi.calculateDriveRoute(eList, mWayPointList, PathPlanningStrategy.DRIVING_DEFAULT);
//    }

    //算路成功回调，开始导航
    @Override
    public void onCalculateRouteSuccess() {
        dissmissProgressDialog();
        mAMapNavi.startNavi(NaviType.GPS);
    }
}
