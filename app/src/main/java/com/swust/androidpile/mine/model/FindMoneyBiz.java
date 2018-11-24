package com.swust.androidpile.mine.model;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface FindMoneyBiz {
    /**
     * 请求余额查询
     * @param url
     * @param phone
     * @param listener
     */
    void findMoney(String url,String phone,MineListener listener);

}
