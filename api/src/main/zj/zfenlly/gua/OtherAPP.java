package zj.zfenlly.gua;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

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


    public static void startActivity3(final Context mContext, final String pkg, final String act) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName(pkg, act);
                mContext.startActivity(intent);
            }
        }, 2000);
    }

    public static void startActivity4(final Activity mContext, final String pkg, final String act, final int v) {

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
                if (v != 0) {
                    LoadInjectLib.setTime(v);
                    SystemTime.setTime(v);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClassName(pkg, act);
                mContext.startActivity(intent);
                mContext.finish();
//            }
//        }, 3000);
    }

}
