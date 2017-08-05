package com.adjust.win;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.apportable.activity.VerdeActivity;
import com.zj.stock.STApplication;

/**
 * Created by Administrator on 2017/7/31.
 */

public class FloatWindowView {
    public boolean isSeekBarOnView = false;
    Context mContext;

    public FloatWindowView(Context context) {
        init(context);
    }

    public void init(Context context) {
        mContext = context;
    }

    public void startSeekBar(int left, int top, int width, int height, int progress, int number) {
        if (isSeekBarOnView == false) {
            isSeekBarOnView = true;
            Intent intent = new Intent(mContext, FloatWinService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("left", left);
            bundle.putInt("top", top);
            bundle.putInt("width", width);
            bundle.putInt("height", height);
            bundle.putInt("progress", progress);
            bundle.putInt("number", number);
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }

    public void stopSeekBar() {
        if (isSeekBarOnView == true) {


            ((FloatWinService)((STApplication)((VerdeActivity)mContext).getApplication()).getWinService()).removeView();
            new Handler().postDelayed(new Runnable(){

                public void run() {

                    //execute the task
                    Intent intent = new Intent(mContext, FloatWinService.class);
                    mContext.stopService(intent);
                    isSeekBarOnView = false;
                }

            }, 1000);


        }
    }


}
