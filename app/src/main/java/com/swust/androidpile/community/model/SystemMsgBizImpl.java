package com.swust.androidpile.community.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.swust.androidpile.R;
import com.swust.androidpile.dao.UserDao;
import com.swust.androidpile.entity.User;
import com.swust.androidpile.main.MyApplication;
import com.swust.androidpile.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public class SystemMsgBizImpl implements SystemMsgBiz {

    private static final String TAG = "SystemMsgBizImpl";

    //请求网络数据
    @Override
    public void findMsg(final String url, final SystemMsgListener listener) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {//参数设置
                    @Override
                    public void onResponse(String json) {//返回数据成功监听
                        LogUtil.sop(TAG,json);
                        listener.success(json);
                    }
                }, new Response.ErrorListener() {//错误监听
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.failed();
                error.printStackTrace();
            }
        });//request创建完毕
        request.setTag("CommunityFragment");
        //设置超时重传时间
        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getHttpQueue().add(request);
    }

    @Override
    public String getPhone(Context context) {
        UserDao userDao = new UserDao(context);
        User user = userDao.findUser();
        if (user != null) {
            return user.getPhone();
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> initData(List<Map<String, Object>> listItems) {
        int[] imageId = new int[]{R.mipmap.notice_item1, R.mipmap.notice_item2};
        String[] title = {"系统消息", "我的建议"};
        String[] message = new String[2];
        message[0] = "请下拉刷新";
        message[1] = "我们的进步离不开您的每一条建议";
        for (int i = 0; i < 2; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageId[i]);
            map.put("textView1", title[i]);
            map.put("textView2", message[i]);
            listItems.add(map);
        }
        return listItems;
    }
}
