package zj.zfenlly.net;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;

/**
 * Created by Administrator on 2016/6/20.
 */
public class OtherAPP {

    static void startOtherActivity(Activity act, boolean autostart) {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.teamlava.castlestory41", "com.apportable.activity.VerdeActivity");
        //ComponentName cn = new ComponentName("zj.zfenlly.tools", "zj.zfenlly.main.MainActivity");
        //ComponentName cn = new ComponentName("com.android.tools.sdkcontroller", "com.android.tools.sdkcontroller.activities.MainActivity");
        intent.setComponent(cn);
        if (autostart) {
            act.startActivityForResult(intent, 0x618);
        } else {
            act.startActivity(intent);
        }
    }
}
