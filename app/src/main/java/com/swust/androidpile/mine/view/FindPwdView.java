package com.swust.androidpile.mine.view;

/**
 * Created by Administrator on 2017/5/2 0002.
 */

public interface FindPwdView {

    /**
     * 页面展示数据用
     * @param str
     */
    void show(String str);

    /**
     * 验证码验证成功后，跳转到修改密码页面
     */
    void jump();

}
