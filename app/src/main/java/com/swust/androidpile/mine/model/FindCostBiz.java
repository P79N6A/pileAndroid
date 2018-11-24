package com.swust.androidpile.mine.model;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public interface FindCostBiz {
    /**
     * 寻找充电记录
     * @param url
     * @param phone
     * @param listener
     */
    void findCost(String url,String phone,int count,MineListener listener);

    /**
     * 查询用户充值记录
     * @param url
     * @param phone
     * @param listener
     */
    void findChargeCost(String url, String phone,MineListener listener);
}
