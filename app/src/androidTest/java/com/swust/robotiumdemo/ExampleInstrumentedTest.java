package com.swust.robotiumdemo;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.swust.androidpile.main.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),
                activityTestRule.getActivity());
    }

    @Test
    public void order() throws Exception {
        ReportFile.createCaseReport("预约用例", "预约用例测试报告");
        //点击“我的”页面
//        solo.sleep(1000);
        ImageView mine = (ImageView) solo.getView("mine");
        solo.clickOnView(mine);
        boolean flag = solo.waitForFragmentByTag("HomeFragment");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'我的'页面", flag);
//        Log.i("test", "------------------------------------>" + flag);
//        solo.sleep(2000);
        //点击“通用设置”
        RelativeLayout relative6 = (RelativeLayout) solo.getView("relative6");
        solo.clickOnView(relative6);
        flag = solo.waitForActivity("SettingActivity");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'通用设置'", flag);
//        Log.i("test", "------------------------------------>" + flag);
//        solo.sleep(2000);
        //点击“偏好设置”
        RelativeLayout relative4 = (RelativeLayout) solo.getView("relative4");
        solo.clickOnView(relative4);
        flag = solo.waitForActivity("PreferActivity");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'偏好设置'", flag);
//        Log.i("test", "------------------------------------>" + flag);
        solo.hideSoftKeyboard();
//        solo.sleep(2000);
        //调整参数值
        TextView prefer_bt7 = (TextView) solo.getView("prefer_maohao1");
        solo.clickOnView(prefer_bt7);
//        solo.sleep(2000);
        solo.scrollToBottom();
//        solo.sleep(1000);
        Button prefer_submitTV = (Button) solo.getView("prefer_submitTV");
        solo.clickOnView(prefer_submitTV);
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "调整参数值", flag);
//        solo.sleep(1000);
//        solo.sleep(3000);
        //点击“预约”
        RelativeLayout relative5 = (RelativeLayout) solo.getView("relative5");
        solo.clickOnView(relative5);
        flag = solo.waitForActivity("OrderInitActivity");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'预约'", flag);
//        Log.i("test", "------------------------------------>" + flag);
        //点击“预约商户”
        RelativeLayout relative1 = (RelativeLayout) solo.getView("relative1");
        solo.clickOnView(relative1);
        flag = solo.waitForActivity("OrderActivity");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'预约商户'", flag);
        //点击“提交预约”
        Button order_submit = (Button) solo.getView("order_submit");
        solo.clickOnView(order_submit);
        flag = solo.waitForText("预约成功");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'提交预约'", flag);
//        Log.i("test", "------------------------------------>" + flag);
        //点击“预约记录”
        RelativeLayout relative2 = (RelativeLayout) solo.getView("relative2");
        solo.clickOnView(relative2);
        flag = solo.waitForLogMessage("showOrderRecode");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'预约记录'", flag);
        solo.goBack();
        //点击“更新卡状态”
        RelativeLayout relative3 = (RelativeLayout) solo.getView("relative3");
        solo.clickOnView(relative3);
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'更新卡状态'", flag);
        //点击“解锁”
        Button jieshiBT = (Button) solo.getView("jieshiBT");
        solo.clickOnView(jieshiBT);
        flag = solo.waitForText("更新成功");
        ReportFile.writeCaseReport(solo, "预约用例测试报告", "点击'解锁'", flag);
        solo.clickOnView(relative3);
        flag = solo.waitForText("尚未被预约");
        solo.sleep(3000);
    }
}
