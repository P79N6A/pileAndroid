package com.swust.androidpile.mine.model;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface AdviceBiz {
    /**
     * 我的建议
     *
     * @param url
     * @param phone
     * @param advice
     */
    void mineAdvice(String url, String phone, String advice,MineListener listener);
}
