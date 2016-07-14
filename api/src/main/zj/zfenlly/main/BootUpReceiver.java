package zj.zfenlly.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * Created by Administrator on 2016/1/7.
 */
public class BootUpReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.w(TAG, msg);
    }

    public void onReceive(Context context, Intent intent) {

        print(intent.toString());
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            Intent i = new Intent(context, MainActivity.class);
            print("boot_completed");
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        } else if (intent.getAction().equals("zj.intent.RESTART")) {
            startStockService(context);
        } else if (intent.getAction().equals("zj.intent.monitor")) {

            startMonitorService(context);

//            Intent i = new Intent(context, MainActivity.class);
//
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
            print("others:" + intent);
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            print("others:" + intent);//} else if (intent.getAction().equals(UsbManager.ACTION_USB_STATE)) {
        } else {
            print("others:" + intent);

        }

    }

    private void startStockService(Context context) {
        Intent serviceIntent = new Intent("zj.zfenlly.StockService");
//            serviceIntent.putExtra("command", RKUpdateService.COMMAND_CHECK_REMOTE_UPDATING);
//            serviceIntent.putExtra("delay", 25000);
        context.startService(serviceIntent);
        Intent mIntent = new Intent();
        mIntent.setAction("zj.zfenlly.StockService");//你定义的service的action
        mIntent.setPackage("zj.zfenlly.tools");//这里你需要设置你应用的包名
        context.startService(mIntent);

        print("serviceIntent:" + serviceIntent);
    }
    private void startMonitorService(Context context) {
        Intent serviceIntent = new Intent("zj.zfenlly.MonitorService");
//            serviceIntent.putExtra("command", RKUpdateService.COMMAND_CHECK_REMOTE_UPDATING);
//            serviceIntent.putExtra("delay", 25000);
        context.startService(serviceIntent);
        Intent mIntent = new Intent();
        mIntent.setAction("zj.zfenlly.MonitorService");//你定义的service的action
        mIntent.setPackage("zj.zfenlly.tools");//这里你需要设置你应用的包名
        context.startService(mIntent);

        print("serviceIntent:" + serviceIntent);
    }
}
