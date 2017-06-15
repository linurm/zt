package zj.zfenlly.gua;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OtherAPP {

    static void setWillStartAPP(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStartAPP(act);
    }

    static void setWillStopAPP(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStopAPP();
    }


    public static void startActivity3(Context mContext, String pkg, String act) {
        mContext.startActivity(new Intent().setComponent(new ComponentName(pkg, act)));
    }


}
