package com.swust.androidpile.community.model;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public interface SystemMsgListener {
    void success(String json);

    void failed();
}
