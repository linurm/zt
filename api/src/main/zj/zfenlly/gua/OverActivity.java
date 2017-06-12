package zj.zfenlly.gua;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Calendar;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/2/15.
 */

public class OverActivity extends Activity {
    private final int DEC_SEC = 3;
    @ViewInject(R.id.textView)
    public TextView display_view;
    private int id = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.over_main);
        ViewUtils.inject(this);
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null)
            id = bundle.getInt("key");

        String v = bundle.getString("value");
        display_view.setText(v);
        int t = 0;
        if (v.equals("b10")) {
            t = -(10 * 60);
        } else if (v.equals("a10")) {
            t = 10 * 60;
        } else if (v.equals("a30")) {
            t = 30 * 60 - DEC_SEC;
        } else if (v.equals("a60")) {
            t = (60 * 60 - DEC_SEC * 2);
        } else if (v.equals("d60")) {
            t = -(60 * 60 - DEC_SEC * 2);
        } else if (v.equals("a0")) {
            t = 0;
            return;
        } else {
            return;
        }
        setTime(t);
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
        return mySharedPreferences.getString("pkg", null);
    }

    String getStartAppAct() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("act", null);
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
