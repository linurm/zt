package zj.zfenlly.gua;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TimeSetting {

    public static int getTimes(Context mContext) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_times", 0);
    }

    public static void setTimes(Context mContext, int t) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_times", t);
        editor.commit();
    }

    public static int getInterval(Context mContext) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_interval", 0);
    }

    public static void setInterval(Context mContext, int t) {
        SharedPreferences mySharedPreferences = mContext.getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_interval", t);
        editor.commit();
    }
}
