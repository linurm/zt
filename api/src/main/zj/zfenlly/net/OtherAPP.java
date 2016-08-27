package zj.zfenlly.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OtherAPP {

    static void setWillStartAPP(Activity act){
//        Intent mIntent = new Intent("zj.zfenlly.net.openwifi.startAct");
//        act.sendBroadcast(mIntent);
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStartAPP(act);

    }
    static void startOtherActivity(Activity act) {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.teamlava.castlestory41", "com.apportable.activity.VerdeActivity");
        //ComponentName cn = new ComponentName("zj.zfenlly.tools", "zj.zfenlly.main.MainActivity");
        //ComponentName cn = new ComponentName("com.android.tools.sdkcontroller", "com.android.tools.sdkcontroller.activities.MainActivity");
        intent.setComponent(cn);

        act.startActivity(intent);
    }

    public static void startActivity(Context mContext) {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.teamlava.castlestory41", "com.apportable.activity.VerdeActivity");
        //ComponentName cn = new ComponentName("zj.zfenlly.tools", "zj.zfenlly.main.MainActivity");
        //ComponentName cn = new ComponentName("com.android.tools.sdkcontroller", "com.android.tools.sdkcontroller.activities.MainActivity");
        intent.setComponent(cn);

        mContext.startActivity(intent);
    }
}
