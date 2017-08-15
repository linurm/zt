package zj.zfenlly.gua;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Calendar;


/**
 * Created by Administrator on 2017/2/15.
 */

public class OverActivity extends Activity {
    private final int DEC_SEC = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(Rfile.over_main);

        Bundle bundle = getIntent().getExtras();

        int v = bundle.getInt("value");

        LoadInjectLib.setTime(v);
        SystemTime.setTime(v);
//        switch (v) {
//            case -30:
//                LoadInjectLib.decHalfHour("");
//                break;
//            case 30:
//                LoadInjectLib.addHalfHour("");
//                break;
//            case -60:
//                LoadInjectLib.decHour("");
//                break;
//            case 60:
//                LoadInjectLib.addHour("");
//                break;
//            default:
//                break;
//        }
//        if (v.equals("b10")) {
//            t = -(10 * 60);
//        } else if (v.equals("a10")) {
//            t = 10 * 60;
//        } else if (v.equals("a30")) {
//            t = 30 * 60 - DEC_SEC;
//        } else if (v.equals("a60")) {
//            t = (60 * 60 - DEC_SEC * 2);
//        } else if (v.equals("d60")) {
//            t = -(60 * 60 - DEC_SEC * 2);
//        } else if (v.equals("a0")) {
//            t = 0;
//            return;
//        } else {
//            return;
//        }
        //setTime(t);
        //setTimeAfter10Minites(t);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (id != 0)
        OtherAPP.startActivity3(this, getStartAppPkg(), getStartAppAct());
        finish();
    }

    String getStartAppPkg() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("pkg", getPackageName());
    }

    public String getLunchActivity() {

        PackageManager pm = getPackageManager();
        Intent it = pm.getLaunchIntentForPackage(getPackageName());
        String className = it.getComponent().getClassName();
        System.out.println(className);
        return className;
    }

    String getStartAppAct() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("act", getLunchActivity());
    }
//
//    void setTimeAfter10Minites(int t) {
//        Calendar c = Calendar.getInstance();
//        Log.e("zj", t + "");
//        c.setTimeInMillis(c.getTimeInMillis() + (30 * 60) * 1000);//30m-4second
//        long when = c.getTimeInMillis();
//        if (when / 1000 < Integer.MAX_VALUE) {
//            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
//        }
//    }

    public void setTime(int t) {
        Calendar c = Calendar.getInstance();
        //Log.e("zj", "-" + c.getTimeInMillis());
        c.setTimeInMillis(c.getTimeInMillis() + (t) * 1000);//30m-4second
        long when = c.getTimeInMillis();
        //Log.e("zj", "+" + c.getTimeInMillis());
        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }

}
