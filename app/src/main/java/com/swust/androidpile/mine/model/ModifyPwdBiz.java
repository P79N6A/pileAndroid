package com.swust.androidpile.mine.model;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface ModifyPwdBiz {

    /**
     * 修改密码
     * @param url
     * @param phone
     * @param oldPwd
     * @param newPwd
     * @param listener
     */
    void modifyPwd(String url, String phone, String oldPwd, String newPwd, MineListener listener);

    /**
     * 忘记密码时重新修改密码
     *
     * @param url
     * @param phone
     * @param pwd
     * @param listener
     */
    void reModifyPwd(String url, String phone, String pwd, MineListener listener);
}
