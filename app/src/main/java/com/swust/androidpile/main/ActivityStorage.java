package com.swust.androidpile.main;

import android.app.Activity;
import java.util.*;

/**
 * 作者：Chen Yixing on 2017/7/17.
 * 邮箱：1663268062@qq.com
 * 版本：V1.0
 */

public class ActivityStorage {
    private static ActivityStorage storage=new ActivityStorage();

    private ActivityStorage(){}

    public static ActivityStorage getInstance(){
        return storage;
    }

    private List<Activity> list= new ArrayList<Activity>();

    public void addActivity(Activity activity){
        list.add(activity);
    }

    public void deleteAll(){
        for(Activity a:list){
            a.finish();
        }
    }
}
