package com.swust.androidpile.mine.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.utils.LogUtil;
import com.swust.androidpile.utils.TokenUtil;
import com.swust.androidpile.utils.Url;
import com.swust.androidpile.mine.activity.AccountInfoActivity;
import com.swust.androidpile.mine.activity.LoginActivity;
import com.swust.androidpile.mine.activity.SettingActivity;
import com.swust.androidpile.mine.activity.UserInfoActivity;
import com.swust.androidpile.mine.activity.VersionActivity;
import com.swust.androidpile.mine.model.FindCostBiz;
import com.swust.androidpile.mine.model.FindCostBizImpl;
import com.swust.androidpile.mine.model.FindMoneyBiz;
import com.swust.androidpile.mine.model.FindMoneyBizImpl;
import com.swust.androidpile.mine.presenter.FindCostPresenter;
import com.swust.androidpile.mine.presenter.FindMoneyPresenter;
import com.swust.androidpile.mine.view.MineView;
import com.swust.androidpile.mine.view.PullToZoomScrollViewEx;
import com.swust.androidpile.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends Fragment implements View.OnClickListener, MineView, FindMoneyPresenter.Money {

    private View view;
    private PullToZoomScrollViewEx scrollView;
    public static TextView MINEFRAGMENT_NAMETV;
    private CircleImageView userIV;
    private boolean loginFlag;
    private UserDao userDao;
    private User user;
    private String userName;
    private String phone;
    private FindMoneyBiz findMoneyModel;
    private FindMoneyPresenter findMoneyPresenter;
    private FindCostPresenter findCostPresenter;
    private FindCostBiz findCostModel;
    private String url;
    private Intent intent;
    private SweetAlertDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        try {
            initView();
            initPresenter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    private void initPresenter() {
        findMoneyModel = new FindMoneyBizImpl();
        findCostModel = new FindCostBizImpl();
        findMoneyPresenter = new FindMoneyPresenter();
        findCostPresenter = new FindCostPresenter();

        findMoneyPresenter.setViewAndModel(this, findMoneyModel, view.getContext());
        findMoneyPresenter.setMyMoney(this);//余额监听回调
        findCostPresenter.setViewAndModel(this, findCostModel, view.getContext());
    }

    /**
     * 初始化视图
     */
    public void initView() {
        //ScrollView设置
        scrollView = (PullToZoomScrollViewEx) view.findViewById(R.id.scroll_view);
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        int mScreenHeight = localDisplayMetrics.heightPixels;
        int mScreenWidth = localDisplayMetrics.widthPixels;
        LinearLayout.LayoutParams localObject = new LinearLayout.LayoutParams(mScreenWidth, (int) (9.0F * (mScreenWidth / 16.0F)));
        scrollView.setHeaderLayoutParams(localObject);
        scrollView.setHideHeader(false);
        View headView = LayoutInflater.from(view.getContext()).inflate(R.layout.profile_head_view, null, false);
        View zoomView = LayoutInflater.from(view.getContext()).inflate(R.layout.profile_zoom_view, null, false);
        View contentView = LayoutInflater.from(view.getContext()).inflate(R.layout.profile_content_view, null, false);
        scrollView.setHeaderView(headView);//设置顶部视图
        scrollView.setZoomView(zoomView);//设置背景图片
        scrollView.setScrollContentView(contentView);//设置内容

        MINEFRAGMENT_NAMETV = (TextView) scrollView.findViewById(R.id.tv_user_name);//用户名
        userIV = (CircleImageView) scrollView.findViewById(R.id.iv_user_head);//用户头像
        MINEFRAGMENT_NAMETV.setOnClickListener(this);//用户头像
        userIV.setOnClickListener(this);//用户名
        scrollView.findViewById(R.id.relative1).setOnClickListener(this);//充值记录
        scrollView.findViewById(R.id.relative2).setOnClickListener(this);//版本信息
        scrollView.findViewById(R.id.relative3).setOnClickListener(this);//余额信息
        scrollView.findViewById(R.id.relative4).setOnClickListener(this);//充电记录
        scrollView.findViewById(R.id.relative6).setOnClickListener(this);//通用设置

        //用户名设置
        initUserInfo();
    }

    /**
     * 用户信息设置
     */
    private void initUserInfo() {
        initUserName();
        initUserHeader();
    }

    /**
     * 用户头像设置
     */
    private void initUserHeader() {
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() +
                    "/userHeader");
            FileInputStream fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            userIV.setImageBitmap(bitmap);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户名设置
     */
    private void initUserName() {
        userDao = new UserDao(view.getContext());
        user = userDao.findUser();
        loginFlag = TokenUtil.checkToken(getActivity());
//        LogUtil.sop("test","loginFlag--->"+loginFlag);
        if (loginFlag) {
            userName = user.getName();
            MINEFRAGMENT_NAMETV.setText(userName);
        } else {
            MINEFRAGMENT_NAMETV.setText("登录/注册");
        }
        if (user != null) {
            userName = user.getName();
            phone = user.getPhone();
        }
//        LogUtil.sop("test","phone1--->"+phone);
    }

    @Override
    public void onClick(View view) {
        loginFlag = TokenUtil.checkToken(getActivity());
//        LogUtil.sop("test","loginFlag="+loginFlag);
        if (loginFlag) {
            switch (view.getId()) {
                case R.id.iv_user_head://头像监听
                    intent = new Intent(view.getContext(), UserInfoActivity.class);
                    startActivityForResult(intent, 0x102);
                    break;
                case R.id.relative1://充值记录
                    showDialog();
                    url = Url.getInstance().getPath(21);
                    findCostPresenter.findChargeCost(url, phone);
                    break;
                case R.id.relative3://余额查询
                    showDialog();
                    url = Url.getInstance().getPath(12);
                    findMoneyPresenter.findMoney(url, phone);
                    break;
                case R.id.relative4://充电记录
                    showDialog();
                    url = Url.getInstance().getPath(11);
                    findCostPresenter.findCost(url, phone, 0);
                    break;
                case R.id.relative6://通用设置
                    intent = new Intent(view.getContext(), SettingActivity.class);
                    intent.putExtra("name", userName);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, 0x103);
                    break;
                case R.id.relative2://版本信息
                    intent = new Intent(view.getContext(), VersionActivity.class);
                    startActivity(intent);
                    break;
            }
        } else {
            //用户没有登录，前往登录页面
            intent = new Intent(view.getContext(), LoginActivity.class);
            startActivityForResult(intent, 0x101);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0x101) {//登录界面传过来的
            initUserName();
        } else if (resultCode == 0x102) {//个人信息界面传过来的
            initUserHeader();
        } else if (resultCode == 0x103) {//通用设置界面传送过来的
            try {
                TokenUtil.TOKENUTIL_TOKEN = "";
                TokenUtil.setToken(getActivity(), "");
                //账户名设置为：登录/注册
                MINEFRAGMENT_NAMETV.setText("登录/注册");
                MINEFRAGMENT_NAMETV.setTextColor(view.getContext().getResources().getColor(R.color.white));
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDialog() {
        pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.barprogress));
        pDialog.setTitleText("请稍后！");
        pDialog.show();
    }

    @Override
    public void show(String str) {
        ToastUtil.showToast(view.getContext(), str);
    }

    @Override
    public void activityFinish() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    @Override
    public void noMoreRecode(String str) {

    }

    @Override
    public void getMoney(double m) {
        Intent intent = new Intent(getActivity(), AccountInfoActivity.class);
        intent.putExtra("money", m);
        startActivity(intent);
    }
}