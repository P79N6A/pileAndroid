package com.swust.androidpile.home.model;

import java.util.List;

/**
 * Created by Administrator on 2017/4/7 0007.
 */

public interface ChargingListener {
    void success(String json);

    void failed(String json);

    void isLegalOrNot(List<Object> list);
}
