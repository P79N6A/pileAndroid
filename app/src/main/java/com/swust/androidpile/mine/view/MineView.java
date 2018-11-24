package com.swust.androidpile.mine.view;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface MineView {
    /**
     * 显示结果
     * @param str
     */
    void show(String str);

    /**
     * 结束当前活动
     */
    void activityFinish();

    /**
     * 订单列表刷新，提示没有更多消息了
     * @param str
     */
    void noMoreRecode(String str);

}
