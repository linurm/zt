package com.mine.temprature;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/9/11.
 */

public class TempService extends Service {
    WindowManager mWindowManager;
    WindowManager.LayoutParams wmParams;
    LinearLayout mFloatLayout;
    String lasttemp = null;
    private FloatView floatView = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗口至左上角，便于调整坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 200;
        wmParams.y = 100;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatLayout = new LinearLayout(this);
        LinearLayout.LayoutParams mFloatLayoutLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mFloatLayout.setLayoutParams(mFloatLayoutLP);
        mFloatLayout.setOrientation(LinearLayout.VERTICAL);
        floatView = new FloatView(getApplicationContext(), mFloatLayout, mWindowManager, wmParams);
        floatView.setText("50");
        floatView.setTextSize(50);
//        floatView.setbClickable(true);
        mFloatLayout.addView(floatView);
        mWindowManager.addView(mFloatLayout, wmParams);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {


        if (lasttemp != event.temp) {
            floatView.setText(event.temp);
            lasttemp = event.temp;
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
