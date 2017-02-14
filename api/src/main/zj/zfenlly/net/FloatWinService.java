package zj.zfenlly.net;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;

/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatWinService extends Service {

    private static final String TAG = "FloatWinService";
    Context mContext;
    LinearLayout mFloatLayout;
    LinearLayout mUpFloatLayout;
    LinearLayout mDownFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    Button mFloatView;
    private FloatView floatView = null;
    private BroadcastReceiver wifiReceiver;
    //Button mRecentView;
    private WifiAdmin mWifiAdmin = null;
    private TextView dtime;
    private Button add;
    private Button dec;
    private final int DEC_SEC = 3;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //createFloatView();
        // Toast.makeText(FxService.this, "create FxService",
        // Toast.LENGTH_LONG);
        mWifiAdmin = new WifiAdmin(this);
        createView2(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void initViewBox(Context context) {
        mFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mFloatLayout.setLayoutParams(mFloatLayoutLP);
        mFloatLayout.setOrientation(LinearLayout.VERTICAL);
        mContext = context;
        mUpFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mUpFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mUpFloatLayout.setLayoutParams(mUpFloatLayoutLP);
        mUpFloatLayout.setOrientation(LinearLayout.HORIZONTAL);

        mDownFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mDownFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDownFloatLayout.setLayoutParams(mDownFloatLayoutLP);
        mDownFloatLayout.setOrientation(LinearLayout.HORIZONTAL);

//        floatView = new FloatButton(context);
//        LinearLayout.LayoutParams floatViewLp = new LinearLayout.LayoutParams(
//                MetricUtil.getDip(context, 50), MetricUtil.getDip(context, 50));
//        floatView.setLayoutParams(floatViewLp);
//        mFloatLayout.addView(floatView);
    }

    void setTimeAfter30Minites() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() + (30 * 60 - DEC_SEC) * 1000);//30m-4second
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }

        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber());
    }

    int getStartAppNumber() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("app_num", 1);
    }

/*    void setTimeBefore30Minites() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() - 1795000);//10second
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }*/

    void setTimeAfter1Hour() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() + (60 * 60 - DEC_SEC * 2) * 1000);//1h-8second
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }

        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber());
    }

    void setTimeBefore1Hour() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(c.getTimeInMillis() - (60 * 60 - DEC_SEC * 2) * 1000);//1h-8second
        long when = c.getTimeInMillis();

        if (when / 1000 < Integer.MAX_VALUE) {
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }

        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber());
    }

    private void createView2(Context mContext) {
        // 获取WindowManager
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为：
		 * 下面的flags属性的效果形同“锁定”。
		 * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
		 * LayoutParams.FLAG_NOT_FOCUSABLE  不可聚焦
		 * LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
		 */
        // 调整悬浮窗口至左上角，便于调整坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 显示myFloatView图像
        initViewBox(mContext);
        floatView = new FloatView(getApplicationContext(), mFloatLayout, mWindowManager, wmParams);
        floatView.setbClickable(true);
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View var1) {
                //floatView.setEnabled(false);
                if (floatView.isbClickable()) {
                    Log.e("click", "click");
                    floatView.setbClickable(false);
                    if (mWifiAdmin.isWifiEnabled()) {
                        mWifiAdmin.closeWifi();
                        Toast.makeText(FloatWinService.this, "close wifi", Toast.LENGTH_SHORT).show();
                    } else {
                        mWifiAdmin.openWifi();
                        Toast.makeText(FloatWinService.this, "open wifi", Toast.LENGTH_SHORT).show();
                    }
                }
                //return true;
            }

        });

        if (mWifiAdmin.isWifiEnabled()) {
            floatView.setImageResource(R.drawable.wifi_on); // 这里简单的用自带的icon来做演示
        } else {
            floatView.setImageResource(R.drawable.wifi_off);
        }
        add = new Button(this);
        add.setText("+");

        add.setHeight(160);
        add.setBackgroundResource(R.drawable.button_shape);
        add.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeAfter1Hour();
                Toast.makeText(FloatWinService.this, "+1 hour", Toast.LENGTH_SHORT).show();
            }
        });
        dec = new Button(this);
        dec.setText("-");
        dec.setHeight(160);
        dec.setBackgroundResource(R.drawable.button_shape);
        dec.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeBefore1Hour();
                Toast.makeText(FloatWinService.this, "-1 hour", Toast.LENGTH_SHORT).show();
            }
        });
        //mWindowManager.addView(add, wmParams);
        dtime = new TextView(this);

        dtime.setText("   it is the time click + 30 minites   ");
        dtime.setHeight(160);

        dtime.setBackgroundResource(R.drawable.button_shape);
        dtime.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        dtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeAfter30Minites();
                Toast.makeText(FloatWinService.this, "+30m", Toast.LENGTH_SHORT).show();
            }
        });


        mUpFloatLayout.addView(dtime);
        mDownFloatLayout.addView(dec);
        mDownFloatLayout.addView(floatView);
        mDownFloatLayout.addView(add);

        mFloatLayout.addView(mUpFloatLayout);
        mFloatLayout.addView(mDownFloatLayout);

        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(this);
        mWifiStatusLoader.setRecentsPanel(floatView);

        mWindowManager.addView(mFloatLayout, wmParams);

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //if (mFloatLayout != null) {
        mFloatLayout.removeAllViews();
        mWindowManager.removeView(mFloatLayout);
        //}
    }

}
