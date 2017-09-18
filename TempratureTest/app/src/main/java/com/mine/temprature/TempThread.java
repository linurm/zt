package com.mine.temprature;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/9/6.
 */

public class TempThread extends Thread {
    public App mContext;
    boolean isStart = false;

    public TempThread(App context) {
        mContext = context;
    }

    @Override
    public void run() {
        isStart = true;
        int i = 0;
        boolean select_flag = true;
        String temp;
        while (isStart) {
            try {
                sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (i++ >= 3) {//3s
                i = 0;
                if (select_flag) {
//                    select_flag = false;
//                    Log.e("ZTAG", "temp" );
                    temp = mContext.getCPUtemprature();
                } else {
                    select_flag = true;
//                    Log.e("ZTAG", "cpu");
                    temp = mContext.getProcessCpuRate();
                }
//                Log.e("ZTAG", "" + temp);
                if (EventBus.getDefault().hasSubscriberForEvent(MessageEvent.class)) {
                    EventBus.getDefault().post(new MessageEvent(temp));
                }
            }
        }
        isStart = false;
    }

    public void stopThread() {
        isStart = false;
        if (isAlive()) {
            interrupt();
        }
    }
}
