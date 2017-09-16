package zj.zfenlly.main;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import zj.zfenlly.stock.StockService;

/**
 * Created by Administrator on 2016/3/7.
 */
public class MonitorService extends Service {


    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isstart = true;

        mThread.start();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        print("onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        print("onTrimMemory");
        Intent intentScan = new Intent("zj.intent.RESTART");
        //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        getApplicationContext().sendBroadcast(intentScan);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Intent localIntent = new Intent();
//        localIntent.setClass(this, StockService.class);  //销毁时重新启动Service
//        this.startService(localIntent);
        Intent intentScan = new Intent("zj.intent.RESTART");
        //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
        //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        getApplicationContext().sendBroadcast(intentScan);
        try {
            Thread.sleep(50);// 10ms
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        print("onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isstart = false;

        if (mThread.isAlive()) {
            try {
                mThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        print("onDestroy");
    }

    private boolean isstart = false;

    private MThread mThread = new MThread();

    public class MThread extends Thread {
        boolean isServiceRunning = false;

        @Override
        public void run() {
            while (isstart) {
                ActivityManager manager = (ActivityManager) getApplicationContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);
                isServiceRunning = false;
                for (ActivityManager.RunningServiceInfo service : manager
                        .getRunningServices(Integer.MAX_VALUE)) {
//                print(service.service
//                        .getClassName());
                    if (StockService.class.getName().equals(service.service
                            .getClassName())) {
                        isServiceRunning = true;
//                        print(StockService.class.getName() + " is running");
//                    while(true){
//
//                    }

                    }
                }
                if (!isServiceRunning) {
                    Intent intentScan = new Intent("zj.intent.RESTART");
                    //intentScan.addCategory(Intent.CATEGORY_DEFAULT);
                    //intentScan.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intentScan.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    getApplicationContext().sendBroadcast(intentScan);
                    //print("start service");
                }
                try {
                    Thread.sleep(5);// 10ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
