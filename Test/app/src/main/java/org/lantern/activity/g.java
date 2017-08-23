package org.lantern.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/23.
 */

public class g extends Activity {
    public int startMode = 0;
    public void startGuaApp() {
        new Handler().postDelayed(new Runnable(){
            public void run() {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String packageName = "zj.zfenlly.tools";
                String className = "zj.zfenlly.gua.activity.SelectModeActivity";
                intent.setClassName(packageName, className);
                Log.e("ZTAG", "" + startMode);
                if (startMode == 1)
                    startActivity(intent);
            }

        }, 3000);
    }

    public void getStartMode() {
        Intent intent = getIntent();
        String a = intent.getAction();
        if (a.equals(Intent.ACTION_VIEW)) {
            //start vpn
//            Log.e("ZTAG", "onResume:" + intent.toString());
            startMode = 1;
        } else if (a.equals(Intent.ACTION_MAIN)) {
//            Log.e("ZTAG", "onResume1:" + intent.toString());
            startMode = 2;
        }
    }
}
