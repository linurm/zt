package com.adjust.win;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/7/31.
 */

public class FloatWindowView {
    Context mContext;
    public boolean isSeekBarOnView = false;

    public void init(Context context) {
        mContext = context;
    }

    public void doseek(int v){

    }

    public void startSeekBar(int left, int top, int width, int height) {
        if (isSeekBarOnView == false) {
            isSeekBarOnView = true;
            Intent intent = new Intent(mContext, FloatWinService.class);
            Bundle bundle = new Bundle();
            bundle.putInt("left", left);
            bundle.putInt("top", top);
            bundle.putInt("width", width);
            bundle.putInt("height", height);
            intent.putExtras(bundle);
            mContext.startService(intent);
        }
    }

    public void stopSeekBar() {
        if (isSeekBarOnView == true) {
            isSeekBarOnView = false;
            Intent intent = new Intent(mContext, FloatWinService.class);
            mContext.stopService(intent);
        }
    }


}
