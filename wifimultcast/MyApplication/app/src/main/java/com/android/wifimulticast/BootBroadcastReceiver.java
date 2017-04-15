package com.android.wifimulticast;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/4/12.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "BootBroadcastReceiver.....");
        Intent service = new Intent(context, WifiService.class);
        context.startService(service);

    }
}
