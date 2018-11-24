package com.swust.androidpile.community.view;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public interface CommunityView {


    /**
     * 请求失败的时候
     */
    void fail();

    void success(String json);

    /**
     * 显示结果
     *
     * @param str
     */
    void show(String str);

//    /**
//     * 结束当前活动
//     */
//    void activityFinish();
//
//    /**
//     * 处理返回的系统消息数据
//     * @param jsonstring
//     */
//    void refreshListView(String jsonstring);

    /**
     * 获取用户电话号码
     */
    void getPhone(String phone);

//    /**
//     * 获取ListView的数据源
//     * @param listItems
//     */
//    void initData(List<Map<String, Object>> listItems);

}
