package com.swust.androidpile.community.model;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public interface SystemMsgBiz {
    /**
     * 请求网络数据，获取系统消息
     */
    void findMsg(String url, SystemMsgListener listener);

    /**
     * 获取电话号码
     *
     * @return
     */
    String getPhone(Context context);

    /**
     * 获取ListView的数据源
     *
     * @param listItems
     * @return
     */
    List<Map<String, Object>> initData(List<Map<String, Object>> listItems);
}
