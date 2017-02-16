package zj.zfenlly.net;

import android.app.Activity;
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


import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;

import static zj.zfenlly.net.SystemInfo.CPU_TYPE;

/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatWinService extends Service {

    private static final String TAG = "FloatWinService";
    private final int DEC_SEC = 3;

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
    private TextView afterhalfhour;
    private TextView afterha10minites;
    private Button after1hour;
    private Button before1hour;

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
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
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mUpFloatLayout = new LinearLayout(context);
            LinearLayout.LayoutParams mUpFloatLayoutLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mUpFloatLayout.setLayoutParams(mUpFloatLayoutLP);
            mUpFloatLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        mDownFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mDownFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mDownFloatLayout.setLayoutParams(mDownFloatLayoutLP);
        mDownFloatLayout.setOrientation(LinearLayout.HORIZONTAL);
    }

    void setTimeAfter10Minites() {
        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber(), "a10");
    }

    void setTimeAfter30Minites() {
        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber(), "a30");
    }

    int getStartAppNumber() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("app_num", 1);
    }

    void setTimeAfter1Hour() {
        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber(), "a60");
    }

    void setTimeBefore1Hour() {
        WifiStatusLoader.getInstance(mContext).startAPP(getStartAppNumber(), "d60");
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
        wmParams.x = 800;
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
            }
        });

        if (mWifiAdmin.isWifiEnabled()) {
            floatView.setImageResource(R.drawable.wifi_on); // 这里简单的用自带的icon来做演示
        } else {
            floatView.setImageResource(R.drawable.wifi_off);
        }
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            after1hour = new Button(this);
            after1hour.setText("+ hour");
            after1hour.setHeight(dip2px(mContext, 80));
            after1hour.setBackgroundResource(R.drawable.button_shape);
            after1hour.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
            after1hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter1Hour();
                    Toast.makeText(FloatWinService.this, "+1 hour", Toast.LENGTH_SHORT).show();
                }
            });
            before1hour = new Button(this);
            before1hour.setText("- hour");
            before1hour.setHeight(dip2px(mContext, 80));
            before1hour.setBackgroundResource(R.drawable.button_shape);
            before1hour.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
            before1hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeBefore1Hour();
                    Toast.makeText(FloatWinService.this, "-1 hour", Toast.LENGTH_SHORT).show();
                }
            });
            afterhalfhour = new TextView(this);
            afterhalfhour.setText("+ 30m");
            afterhalfhour.setHeight(dip2px(mContext, 80));
            afterhalfhour.setWidth(175);
            afterhalfhour.setBackgroundResource(R.drawable.button_shape);
            afterhalfhour.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
            afterhalfhour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter30Minites();
                    Toast.makeText(FloatWinService.this, "+30m", Toast.LENGTH_SHORT).show();
                }
            });

            afterha10minites = new TextView(this);
            afterha10minites.setText("+ 10m");
            afterha10minites.setHeight(dip2px(mContext, 80));
            afterha10minites.setWidth(175);
            afterha10minites.setBackgroundResource(R.drawable.button_shape);
            afterha10minites.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
            afterha10minites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter10Minites();
                    Toast.makeText(FloatWinService.this, "+10m", Toast.LENGTH_SHORT).show();
                }
            });

            mUpFloatLayout.addView(afterha10minites);
            mUpFloatLayout.addView(afterhalfhour);
            mFloatLayout.addView(mUpFloatLayout);
            mDownFloatLayout.addView(before1hour);
        }
        mDownFloatLayout.addView(floatView);
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mDownFloatLayout.addView(after1hour);
        }

        mFloatLayout.addView(mDownFloatLayout);
        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(this);
        mWifiStatusLoader.setRecentsPanel(floatView);
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mFloatLayout.removeAllViews();
        mWindowManager.removeView(mFloatLayout);
    }

}
