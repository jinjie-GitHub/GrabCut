package com.ltzk.mbsf.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * activity 管理类
 */
public class ActivityCollector {


    private static List<Activity> list = new ArrayList<>();


    public static void addActivity(Activity activity) {
        list.add(activity);
    }

    public static void removeActivity(Activity activity) {
          list.remove(activity);
    }

    public static Activity getTopActivity(){
        return list.get(list.size()-1);
    }

    public static void removeAllActivity() {
        for (int i = 0;i<list.size();i++) {
            Activity activity = list.get(i);
            if (!activity.isFinishing()) {
                activity.finish();
                list.remove(activity);
                i--;
            }
        }
    }

    public static int getActivitySize(){
        return list.size();
    }


}
