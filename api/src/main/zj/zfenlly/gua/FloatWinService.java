package zj.zfenlly.gua;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import zj.zfenlly.wifi.WifiAdmin;

import static zj.zfenlly.gua.SystemInfo.CPU_TYPE;


/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatWinService extends Service {

    private static final String TAG = "FloatWinService";
    private static final int ButtonHeight = 66;
    private static final int SET_VIEW_START = 1;
    private static final int SET_VIEW_STOP = 2;

    Context mContext;
    LinearLayout mFloatLayout;
    LinearLayout mFloatLayout2;
    LinearLayout mUpFloatLayout;
    LinearLayout mMidFloatLayout;
    LinearLayout mDownFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager.LayoutParams wmParams2;
    WindowManager mWindowManager;
    MZFloatView v;
    NotifySound ns = new NotifySound();
    private FloatView floatView = null;
    private WifiAdmin mWifiAdmin = null;
    private TextView afterhalfhour;
    private TextView after10minites;
    private TextView before10minites;
    private Button after1hour;
    private Button before1hour;
    private Button refreshView;
    private Button settingsView;
    private Button wifiControlView;
    private Button addTimesView;
    private Button delTimesView;
    private Button addIntervalView;
    private Button delIntervalView;
    private Button addCoordinateView;
    private Button startClickView;
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SET_VIEW_START:
                    startClickView.setText("s");
                    break;
                case SET_VIEW_STOP:
                    startClickView.setText("t");
                    break;
                default:
                    break;
            }
        }
    };
    private Button start18ClickView;
    private Button start6ClickView;
    private Button start3ClickView;
    private boolean add_flag = false;
    private boolean settings_flag = false;
    private MZFloatView CoordinateView;
    private int click_times;
    private int click_interval;
    private ClickThread ct = null;
    private boolean isOnExpandView = false;

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
        ns.init(this);
        createView(this);
        ct = new ClickThread();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void initViewBox(Context context) {

    }

    void setTimeBefore10Minites() {
        WifiStatusLoader.getInstance(mContext).startAPP(0, "b10");
    }

    void setTimeAfter10Minites() {
        WifiStatusLoader.getInstance(mContext).startAPP(0, "a10");
    }

    void setTimeAfter30Minites() {
        WifiStatusLoader.getInstance(mContext).startAPP(0, "a30");
    }

    void setTimeAfter1Hour() {
        WifiStatusLoader.getInstance(mContext).startAPP(0, "a60");
    }

    void setRefresh() {

       /* new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(1000); //1秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
//                Message message = Message.obtain();
//                message.what = 1;
//                messageHandler.sendMessage(message);
                Instrumentation mInst = new Instrumentation();
                long downTime;
                long eventTime;
                float x = 0, y = 0;
                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();
                    x = dip2px(getApplicationContext(), 300);
                    y = dip2px(getApplicationContext(), 200);
                    MotionEvent me1 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);

                    mInst.sendPointerSync(me1);
                } catch (Exception e) {
                }
                synchronized (this) {
                    try {
                        wait(5000); //1秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();
                    MotionEvent me2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
                    mInst.sendPointerSync(me2);
                    Log.e("instrument", "send pointersync " + x + ":" + y);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();*/

        WifiStatusLoader.getInstance(mContext).startAPP(0, "a0");
    }

    void setTimeBefore1Hour() {
        WifiStatusLoader.getInstance(mContext).startAPP(0, "d60");
    }

    private void createView(Context context) {
        // 获取WindowManager
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗口至左上角，便于调整坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 200;
        wmParams.y = 100;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 显示myFloatView图像
        mFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mFloatLayout.setLayoutParams(mFloatLayoutLP);
        mFloatLayout.setOrientation(LinearLayout.VERTICAL);
        mContext = context;
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mUpFloatLayout = new LinearLayout(context);
            mUpFloatLayout.setLayoutParams(mFloatLayoutLP);
            mUpFloatLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        mMidFloatLayout = new LinearLayout(context);
        mMidFloatLayout.setLayoutParams(mFloatLayoutLP);
        mMidFloatLayout.setOrientation(LinearLayout.HORIZONTAL);

        mDownFloatLayout = new LinearLayout(context);
        mDownFloatLayout.setLayoutParams(mFloatLayoutLP);
        mDownFloatLayout.setOrientation(LinearLayout.HORIZONTAL);
        floatView = new FloatView(getApplicationContext(), mFloatLayout, mWindowManager, wmParams);
        floatView.setbClickable(true);
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View var1) {
                if (floatView.isbClickable()) {
                    Log.e("click", "click");
                    //floatView.setbClickable(false);
                    if (isOnExpandView == false) {
                        addExpandView();
                    } else {
                        delExpandView();
                        if (settings_flag)
                            delSettingsView();
                        if (add_flag) {
                            Toast.makeText(FloatWinService.this, "[ - ]", Toast.LENGTH_SHORT).show();
                            delCView();
                        }
                    }
                }
            }
        });

        if (mWifiAdmin.isWifiEnabled()) {
            floatView.setImageResource(Rfile.wifi_on); // 这里简单的用自带的icon来做演示
        } else {
            floatView.setImageResource(Rfile.wifi_off);
        }

        mMidFloatLayout.addView(floatView);
        mFloatLayout.addView(mMidFloatLayout);

        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(this);
        mWifiStatusLoader.setRecentsPanel(floatView);
        mWindowManager.addView(mFloatLayout, wmParams);
        Log.e("addCView", "add cview");
    }

    private void delExpandView() {
        isOnExpandView = false;
        Log.e(TAG, "delExpandView");
        mMidFloatLayout.removeView(startClickView);
        mMidFloatLayout.removeView(start18ClickView);
        mMidFloatLayout.removeView(start6ClickView);
        mMidFloatLayout.removeView(start3ClickView);

        mMidFloatLayout.removeView(wifiControlView);
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mMidFloatLayout.removeView(before1hour);
            mMidFloatLayout.removeView(after1hour);
        }
        mDownFloatLayout.removeAllViews();
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mUpFloatLayout.removeAllViews();
            mFloatLayout.removeView(mUpFloatLayout);
        }
        mFloatLayout.removeView(mDownFloatLayout);
    }

    private void addExpandView() {
        isOnExpandView = true;
        Log.e(TAG, "addExpandView");
        wifiControlView = new Button(this);
        wifiControlView.setText("WIFI");
        settingsView = new Button(this);
        settingsView.setText("Set");
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                dip2px(mContext, ButtonHeight),
                dip2px(mContext, ButtonHeight)
        );
        wifiControlView.setLayoutParams(p);
        wifiControlView.setBackgroundResource(Rfile.button_shape);
        wifiControlView.setTextColor(getResources().getColor(Rfile.green));
        wifiControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mWifiAdmin.isWifiEnabled()) {
                    mWifiAdmin.closeWifi();
                    Toast.makeText(FloatWinService.this, "close wifi", Toast.LENGTH_SHORT).show();
                } else {
                    mWifiAdmin.openWifi();
                    Toast.makeText(FloatWinService.this, "open wifi", Toast.LENGTH_SHORT).show();
                }
            }
        });
        settingsView.setLayoutParams(p);
//        settingsView.setBackgroundResource(Rfile.button_shape);
//        settingsView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settings_flag) {
                    delSettingsView();
                } else {
                    addSettingsView();
                }
            }
        });

        refreshView = new Button(this);
        refreshView.setText("F5");
        refreshView.setLayoutParams(p);
        refreshView.setBackgroundResource(Rfile.button_shape);
        refreshView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRefresh();
                Toast.makeText(FloatWinService.this, "refresh", Toast.LENGTH_SHORT).show();
            }
        });
        addCoordinateView = new Button(this);
        addCoordinateView.setText("[ + ]");
        addCoordinateView.setLayoutParams(p);
        addCoordinateView.setBackgroundResource(Rfile.button_shape);
        addCoordinateView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        addCoordinateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setRefresh();
                if (add_flag) {
                    Toast.makeText(FloatWinService.this, "[ - ]", Toast.LENGTH_SHORT).show();
                    delCView();
                } else {
                    Toast.makeText(FloatWinService.this, "[ + ]", Toast.LENGTH_SHORT).show();
                    addCView();
                }
            }
        });
        startClickView = new Button(this);
        startClickView.setText("N");
        startClickView.setLayoutParams(p);
        startClickView.setBackgroundResource(Rfile.button_shape);
        startClickView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        startClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_flag) {
                    add_flag = false;
                    Toast.makeText(FloatWinService.this, "start", Toast.LENGTH_SHORT).show();
                    delCView();
                }
                StartClick(0);
            }
        });

        start18ClickView = new Button(this);
        start18ClickView.setText("18");
        start18ClickView.setLayoutParams(p);
        start18ClickView.setBackgroundResource(Rfile.button_shape);
        start18ClickView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        start18ClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_flag) {
                    add_flag = false;
                    Toast.makeText(FloatWinService.this, "start", Toast.LENGTH_SHORT).show();
                    delCView();
                }
                StartClick(18);
            }
        });

        start6ClickView = new Button(this);
        start6ClickView.setText("6");
        start6ClickView.setLayoutParams(p);
        start6ClickView.setBackgroundResource(Rfile.button_shape);
        start6ClickView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        start6ClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_flag) {
                    add_flag = false;
                    Toast.makeText(FloatWinService.this, "start", Toast.LENGTH_SHORT).show();
                    delCView();
                }
                StartClick(6);
            }
        });

        start3ClickView = new Button(this);
        start3ClickView.setText("3");
        start3ClickView.setLayoutParams(p);
        start3ClickView.setBackgroundResource(Rfile.button_shape);
        start3ClickView.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
        start3ClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (add_flag) {
                    add_flag = false;
                    Toast.makeText(FloatWinService.this, "start", Toast.LENGTH_SHORT).show();
                    delCView();
                }
                StartClick(3);
            }
        });
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            after1hour = new Button(this);
            after1hour.setText("+ hour");
            after1hour.setLayoutParams(p);
            after1hour.setBackgroundResource(Rfile.button_shape);
            after1hour.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
            after1hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter1Hour();
                    Toast.makeText(FloatWinService.this, "+1 hour", Toast.LENGTH_SHORT).show();
                }
            });
            before1hour = new Button(this);
            before1hour.setText("- hour");
            before1hour.setLayoutParams(p);
            before1hour.setBackgroundResource(Rfile.button_shape);
            before1hour.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
            before1hour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeBefore1Hour();
                    Toast.makeText(FloatWinService.this, "-1 hour", Toast.LENGTH_SHORT).show();
                }
            });
            afterhalfhour = new TextView(this);
            afterhalfhour.setText("+ 30m");
            afterhalfhour.setLayoutParams(p);
            afterhalfhour.setBackgroundResource(Rfile.button_shape);
            afterhalfhour.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
            afterhalfhour.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter30Minites();
                    Toast.makeText(FloatWinService.this, "+30m", Toast.LENGTH_SHORT).show();
                }
            });

            before10minites = new TextView(this);
            before10minites.setText("- 10m");
            before10minites.setLayoutParams(p);
            before10minites.setBackgroundResource(Rfile.button_shape);
            before10minites.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
            before10minites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeBefore10Minites();
                    Toast.makeText(FloatWinService.this, "-10m", Toast.LENGTH_SHORT).show();
                }
            });

            after10minites = new TextView(this);
            after10minites.setText("+ 10m");
            after10minites.setLayoutParams(p);
            after10minites.setBackgroundResource(Rfile.button_shape);
            after10minites.setTextColor(getResources().getColor(Rfile.abs__bright_foreground_disabled_holo_light));
            after10minites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setTimeAfter10Minites();
                    Toast.makeText(FloatWinService.this, "+10m", Toast.LENGTH_SHORT).show();
                }
            });
            mUpFloatLayout.addView(before10minites);
            mUpFloatLayout.addView(after10minites);
            mUpFloatLayout.addView(afterhalfhour);

            mMidFloatLayout.addView(before1hour);

            mMidFloatLayout.addView(after1hour);
        }
        mDownFloatLayout.addView(refreshView);
        mDownFloatLayout.addView(addCoordinateView);


        mMidFloatLayout.addView(wifiControlView);
        mDownFloatLayout.addView(settingsView);//
        mMidFloatLayout.addView(startClickView);//
        mMidFloatLayout.addView(start18ClickView);//
        mMidFloatLayout.addView(start6ClickView);//
        mMidFloatLayout.addView(start3ClickView);//

        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            mFloatLayout.addView(mUpFloatLayout);
        }
        mFloatLayout.addView(mDownFloatLayout);
    }

    private void StartClick(int t) {

        if (ct != null && ct.isStart) {
            ct.stopThread();
            ct = null;
        } else {
            ct = new ClickThread();
            if (t != 0) {
                ct.setTempTimes(t);
            }
            ct.start();
        }
    }

    private void addCView() {
        add_flag = true;
        wmParams2 = new WindowManager.LayoutParams();
        wmParams2.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams2.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        wmParams2.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
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
        wmParams2.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams2.x = 600;
        wmParams2.y = 200;
        // 设置悬浮窗口长宽数据
        wmParams2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatLayout2 = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams mFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mFloatLayout2.setLayoutParams(mFloatLayoutLP);
        mFloatLayout2.setOrientation(LinearLayout.VERTICAL);
        CoordinateView = new MZFloatView(getApplicationContext(), mFloatLayout2, mWindowManager, wmParams);
        //CoordinateView.setBackgroundResource(R.drawable.button_shape);
        CoordinateView.setImageResource(Rfile.mz);
        mFloatLayout2.addView(CoordinateView);
        mWindowManager.addView(mFloatLayout2, wmParams2);
    }

    private void delCView() {
        add_flag = false;
        mFloatLayout2.removeAllViews();
        mWindowManager.removeView(mFloatLayout2);
    }

    private void addSettingsView() {
        settings_flag = true;
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                200,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addTimesView = new Button(this);
        click_times = getTimes();
        addTimesView.setText("" + click_times);
        addTimesView.setHeight(dip2px(mContext, ButtonHeight));
        addTimesView.setWidth(dip2px(mContext, ButtonHeight));
        addTimesView.setLayoutParams(p);
//        addTimesView.setBackgroundResource(R.drawable.button_shape);
//        addTimesView.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        addTimesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click_times += 1;
                setTimes(click_times);
                delTimesView.setText("" + click_times);
                addTimesView.setText("" + click_times);
            }
        });

        mDownFloatLayout.addView(addTimesView);

        delTimesView = new Button(this);
        delTimesView.setText("" + click_times);
        delTimesView.setHeight(dip2px(mContext, ButtonHeight));
        delTimesView.setWidth(dip2px(mContext, ButtonHeight));
        delTimesView.setLayoutParams(p);
//        delTimesView.setBackgroundResource(R.drawable.button_shape);
//        delTimesView.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        delTimesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click_times == 0) {
                    return;
                }
                click_times -= 1;
                setTimes(click_times);
                delTimesView.setText("" + click_times);
                addTimesView.setText("" + click_times);
            }
        });
        mDownFloatLayout.addView(delTimesView);

        //

        addIntervalView = new Button(this);
        click_interval = getInterval();
        addIntervalView.setText("" + click_interval);
        addIntervalView.setHeight(dip2px(mContext, ButtonHeight));
        addIntervalView.setWidth(dip2px(mContext, ButtonHeight));
        addIntervalView.setLayoutParams(p);
//        addIntervalView.setBackgroundResource(R.drawable.button_shape);
//        addIntervalView.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        addIntervalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click_interval += 1;
                setInterval(click_interval);
                delIntervalView.setText("" + click_interval);
                addIntervalView.setText("" + click_interval);
            }
        });

        mDownFloatLayout.addView(addIntervalView);

        delIntervalView = new Button(this);
        delIntervalView.setText("" + click_interval);
        delIntervalView.setHeight(dip2px(mContext, ButtonHeight));
        delIntervalView.setWidth(dip2px(mContext, ButtonHeight));
        delIntervalView.setLayoutParams(p);
//        delIntervalView.setBackgroundResource(R.drawable.button_shape);
//        delIntervalView.setTextColor(getResources().getColor(R.color.abs__bright_foreground_disabled_holo_light));
        delIntervalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (click_interval == 0) {
                    return;
                }
                click_interval -= 1;
                setInterval(click_interval);
                delIntervalView.setText("" + click_interval);
                addIntervalView.setText("" + click_interval);
            }
        });
        mDownFloatLayout.addView(delIntervalView);//
        Log.e("TAG", "add settings view");
    }

    private void delSettingsView() {
        settings_flag = false;
        Log.e("TAG", "del settings view");
        mDownFloatLayout.removeView(delTimesView);//
        mDownFloatLayout.removeView(addTimesView);//
        mDownFloatLayout.removeView(addIntervalView);//
        mDownFloatLayout.removeView(delIntervalView);//
    }

    private int getTimes() {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_times", 0);
    }

    private void setTimes(int t) {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_times", t);
        editor.commit();
    }

    private int getInterval() {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_interval", 0);
    }

    private void setInterval(int t) {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_interval", t);
        editor.commit();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mFloatLayout.removeAllViews();
        mWindowManager.removeView(mFloatLayout);
        if (add_flag) {
            add_flag = false;
            delCView();
        }
        if (settings_flag) {
            settings_flag = false;
            delSettingsView();
        }
    }


    class ClickThread extends Thread {

        //        int x, y;
        int times = getTimes();
        int interval = getInterval() * 100;
        Instrumentation mInst = new Instrumentation();
        long downTime;
        long eventTime;
        boolean isStart = false;

        public void setTempTimes(int t) {
            times = t;
        }

        public void stopThread() {
            isStart = false;
            if (isAlive()) {
                interrupt();
            }
        }

        @Override
        public void run() {
            isStart = true;
            mHandler.sendMessage(mHandler.obtainMessage(
                    SET_VIEW_STOP, null));
            while (times-- > 0 && isStart) {

                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();
                    MotionEvent me1 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, CoordinateView.x1, CoordinateView.y1, 0);
                    mInst.sendPointerSync(me1);
                } catch (Exception e) {
                }
                synchronized (this) {
                    try {
                        wait(200);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (isStart == false) break;
                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();
                    MotionEvent me2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, CoordinateView.x1, CoordinateView.y1, 0);
                    mInst.sendPointerSync(me2);
                    Log.e("instrument", "send pointersync " + CoordinateView.x1 + ":" + CoordinateView.y1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                synchronized (this) {
                    try {
                        wait(interval); //1秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (isStart == false) break;
            }
            isStart = false;
            ns.play(2);
            mHandler.sendMessage(mHandler.obtainMessage(
                    SET_VIEW_START, null));
        }
    }
}
