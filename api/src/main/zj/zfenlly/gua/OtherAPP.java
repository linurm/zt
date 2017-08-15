package zj.zfenlly.gua;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OtherAPP {

    public static void setWillStartAPP(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStartAPP();
    }

    public static void setWillStartVpn(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setStartVpn(true);
    }

    public static void setWillStopAPP(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStopAPP();
    }


    public static void startActivity3(Context mContext, String pkg, String act) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName(pkg, act);
        mContext.startActivity(intent);
    }


}
