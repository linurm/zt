package com.adjust.win;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.apportable.activity.VerdeActivity;
import com.zj.stock.R;
import com.zj.stock.STApplication;


/**
 * Created by Administrator on 2017/3/1.
 */

public class FloatWinService extends Service {
    private static final String TAG = "FloatWinService";
    int left = 0;
    int top = 0;
    int width = 0;
    int height = 0;
    Context mContext;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    //    LinearLayout mFloatLayout;
//    LinearLayout mUpFloatLayout;
    //    CameraPreviewSurfaceView mCameraPreviewSurfaceView = null;
//    View winView;
    private SeekBar floatView = null;

    private static Activity scanForActivity(Context cont) {
        if (cont == null) {
            Log.e("ZTAG", "null");
            return null;
        } else if (cont instanceof Activity) {
            Log.e("ZTAG", "Activity");
            return (Activity) cont;
        } else if (cont instanceof ContextWrapper) {
            Log.e("ZTAG", "scanForActivity");
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        }
        return null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mUpFloatLayout.removeView(floatView);
//        mFloatLayout.removeView(mUpFloatLayout);
//        mWindowManager.removeView(mFloatLayout);
        mWindowManager.removeView(floatView);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.e("ZTAG", "onCreate");

    }

//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//        if (intent != null) {
//            Bundle bundle = intent.getExtras();
//            int left = bundle.getInt("left", 0);
//            int top = bundle.getInt("top", 0);
//            int width = bundle.getInt("width", 0);
//            int height = bundle.getInt("height", 0);
//            Log.e("ZTAG", "onStart: " + left + ":" + top + ":" + width + ":" + height);
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            left = bundle.getInt("left", 0);
            top = bundle.getInt("top", 0);
            width = bundle.getInt("width", 0);
            height = bundle.getInt("height", 0);
            Log.e("ZTAG", "onstartcommand: " + left + ":" + top + ":" + width + ":" + height);
        }
        initView(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    public void initView(Context context) {
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
        wmParams.width = width;//WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = height;//WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = left;
        wmParams.y = top;
//        mFloatLayout = new LinearLayout(context);
//        LinearLayout.LayoutParams mFloatLayoutLP = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        mFloatLayoutLP.leftMargin = left;//mFloatLayoutLP.gravity=Gravity.LEFT | Gravity.TOP;
//        mFloatLayoutLP.topMargin = top;
//        mFloatLayout.setLayoutParams(mFloatLayoutLP);
//        mFloatLayout.setOrientation(LinearLayout.VERTICAL);
        mContext = context;
        Log.e("ZJTAG", "Z LOOK" + context);
//        mUpFloatLayout = new LinearLayout(context);
//        LinearLayout.LayoutParams mUpFloatLayoutLP = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
//        mUpFloatLayout.setLayoutParams(mUpFloatLayoutLP);
//        mUpFloatLayout.setOrientation(LinearLayout.HORIZONTAL);
        floatView = new SeekBar(getApplicationContext());
//        floatView.setProgressDrawable(getResources().getDrawable(R.drawable.seek));
        floatView.setProgressDrawable(getResources().getDrawable(R.drawable.thumb_image));
        floatView.setThumb(getResources().getDrawable(R.drawable.seek));
//        floatView.setBackgroundResource(R.drawable.ic_launcher);

//        floatView.setThumbOffset(20);
//        floatView.setMinimumHeight(20);
//        floatView.setMinimumWidth(20);
//        floatView.setLayoutParams(mFloatLayoutLP);
        try {
            float f = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f;
            int l = (int) (f * 100);
            floatView.setProgress(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        floatView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((VerdeActivity) ((STApplication) getApplication()).getActivity()).doSeek(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        floatView.setBackgroundResource(R.drawable.share_via_barcode);
//
//        mUpFloatLayout.addView(floatView);
//        mFloatLayout.addView(mUpFloatLayout);
        mWindowManager.addView(floatView, wmParams);
    }
}
