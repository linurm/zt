package zj.zfenlly.main;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import zj.zfenlly.stock.StockService;

public class AppReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        boolean isServiceRunning = false;

        if (arg1.getAction().equals(Intent.ACTION_TIME_TICK)) {

            // ���Service״̬
//            print("receive time tick:" + StockService.class.getName());
            ActivityManager manager = (ActivityManager) arg0
                    .getSystemService(Context.ACTIVITY_SERVICE);
            isServiceRunning = false;
            for (RunningServiceInfo service : manager
                    .getRunningServices(Integer.MAX_VALUE)) {
//                print(service.service
//                        .getClassName());
                if (StockService.class.getName().equals(service.service
                        .getClassName())) {
                    isServiceRunning = true;
                    print(StockService.class.getName() + " is running");
//                    while(true){
//
//                    }

                }
            }
            if (!isServiceRunning) {
                Intent i = new Intent(arg0, StockService.class);
                arg0.startService(i);
                print("start service");
            }
        } else {
            print("is not action_time_tick");
        }
    }
}
