package zj.zfenlly.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OtherAPP {

    static void setWillStartAPP(Activity act, int id) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStartAPP(act, id);
    }
    static void setActivity(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setActivity(act);
    }


    static void setWillStopAPP(Activity act) {
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(act.getApplicationContext());
        mWifiStatusLoader.setIsStopAPP();
    }

    static void startOtherActivity(Activity act, int select_id) {
        try {
            act.startActivity(prepareIntent(select_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void startOtherActivity(Context mContext, int select_id) {
        try {
            mContext.startActivity(prepareIntent(select_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Intent prepareIntent(int id) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = null;
        if (id == 1) {
            //cn = new ComponentName("com.teamlava.castlestory41", "com.apportable.activity.VerdeActivity");
            cn = new ComponentName("zybornit.loadlib", "zybornit.loadlib.MainActivity");
            //""
            //""
        } else if (id == 2) {
            //ComponentName cn = new ComponentName("zj.zfenlly.tools", "zj.zfenlly.main.MainActivity");
            cn = new ComponentName("com.teamlava.castlestory", "com.teamlava.castlestory.Main");
        }
        intent.setComponent(cn);


        return intent;
    }

    public static void startActivity2(Context mContext, int id) {
        mContext.startActivity(prepareIntent(id));
    }
}
