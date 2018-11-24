package com.swust.androidpile.main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.swust.androidpile.R;
import com.swust.androidpile.community.fragment.CommunityFragment;
import com.swust.androidpile.home.fragment.HomeFragment;
import com.swust.androidpile.mine.fragment.MineFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * fragment属性只有homeF会在init方法中被创建，其他的在onClick方法中被监听创建
     */
    //    QRFragment qrF;
    HomeFragment homeF;//
    CommunityFragment communityF;
    MineFragment mineF;

    /**
     * 下面的图片视图作用：底部导航栏的图片被点击后图片颜色加深，其他图片全部变潜
     */
    private ImageView home;
    private ImageView mine;
    private ImageView community;
    private ImageView image;
    //    private ImageView qr;
    private static final String TAG = "MainActivity";

    /**
     * 开场动画
     */
    private Animation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initView();//内部有软件更新程序
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
//        qr.setOnClickListener(this);
        home.setOnClickListener(this);
        mine.setOnClickListener(this);
        community.setOnClickListener(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        home = (ImageView) findViewById(R.id.home);
        mine = (ImageView) findViewById(R.id.mine);
//        qr = (ImageView) findViewById(R.id.qr);
        community = (ImageView) findViewById(R.id.community);

        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.startanimation);//1.5秒后消失
        image = (ImageView) findViewById(R.id.startIv);

        //开场图片展示2秒后启动图片淡化动画
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image.startAnimation(scaleAnimation);
            }
        }, 2000);//2000

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image.setVisibility(View.INVISIBLE);
                //主页图片点击状态
                home.setImageResource(R.mipmap.icon_home_focus);
                //初始默认指向主页
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                hideAllFragment(ft);    //隐藏底部导航栏所有的fragment
                if (homeF == null) {
                    homeF = new HomeFragment();
                    ft.add(R.id.frame1, homeF, "HomeFragment")
                            .commit();
                } else {
                    ft.show(homeF).commit();
                }
                findViewById(R.id.bottomBar).setVisibility(View.VISIBLE);
//		        scaleAnimation.cancel();
                //查询是否有版本更新
                identifyVersion();
            }
        }, 2800);//2800
    }

    /**
     * 版本更新
     */
    private void identifyVersion() {
        new UpdateManager(this).checkUpdate(TAG);
    }

    /**
     * 1.在init()之后之后执行
     * 2.在活动页被消除后，附加在其上的所有fragment当再次被激活时，会逐个执行该方法，避免被再次创建
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (homeF == null && fragment instanceof HomeFragment) {
            homeF = (HomeFragment) fragment;
        } else if (communityF == null && fragment instanceof CommunityFragment) {
            communityF = (CommunityFragment) fragment;
        } else if (mineF == null && fragment instanceof MineFragment) {
            mineF = (MineFragment) fragment;
        }
    }

    /**
     * 隐藏所有fragment
     *
     * @param ft
     */
    public void hideAllFragment(FragmentTransaction ft) {
        if (homeF != null) {
            ft.hide(homeF);
        }
//        if (qrF != null) {
//            ft.hide(qrF);
//        }
        if (communityF != null) {
            ft.hide(communityF);
        }
        if (mineF != null) {
            ft.hide(mineF);
        }
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        hideAllFragment(ft);    //将所有的fragment隐藏
        switch (view.getId()) {
            case R.id.home:
                reverseImage(0);    //底部导航栏所有图标设置为灰色，将home(用数字0表示)对应的图标设置为绿色
                function1(ft, homeF, 0);
                break;
//            case R.id.qr:
//                reverseImage(1);
//                function1(ft, qrF, 1);
//                break;
            case R.id.community:
                reverseImage(2);
                function1(ft, communityF, 2);
                break;
            case R.id.mine:
                reverseImage(3);
                function1(ft, mineF, 3);
                break;
            default:
                break;
        }
    }//onClick结束

    /**
     * 如果对应的fragment存在，则将其显示到activity中；否则将其添加到activity中
     */
    public void function1(FragmentTransaction ft, Fragment fragment, int index) {
        if (fragment == null) {
            switch (index) {
                case 0:
                    fragment = new HomeFragment();
                    ft.add(R.id.frame1, fragment, "HomeFragment");
                    break;
//                case 1:
//                    fragment = new QRFragment();
//                    ft.add(R.id.frame1, fragment, "QRFragment");
//                    break;
                case 2:
                    fragment = new CommunityFragment();
                    ft.add(R.id.frame1, fragment, "CommunityFragment");
                    break;
                case 3:
                    fragment = new MineFragment();
                    ft.add(R.id.frame1, fragment, "MineFragment");
                    break;
                default:
                    break;
            }
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }

    /**
     * 底部导航栏图片转换
     */
    public void reverseImage(int i) {
        //所有图片先全部设置为原始状态
        home.setImageResource(R.mipmap.icon_home);
//        qr.setImageResource(R.mipmap.icon_qr);
        community.setImageResource(R.mipmap.icon_community);
        mine.setImageResource(R.mipmap.icon_mine);
        //传入的I代表第i个图片，将这个图片设置为点击状态
        switch (i) {
            case 0://home
                home.setImageResource(R.mipmap.icon_home_focus);
                break;
//            case 1://qr
//                qr.setImageResource(R.mipmap.icon_qr_focus);
//                break;
            case 2://community
                community.setImageResource(R.mipmap.icon_community_focus);
                break;
            case 3://mine
                mine.setImageResource(R.mipmap.icon_mine_focus);
                break;
            default:
                break;
        }
    }

    //退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //if no more history in stack
            if (this.getFragmentManager().getBackStackEntryCount() == 0) {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确定退出?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.cancel();
                                finish();
                            }
                        })
                        .show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}