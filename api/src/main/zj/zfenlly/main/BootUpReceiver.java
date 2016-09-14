package zj.zfenlly.main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2016/1/7.
 */
public class BootUpReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

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

        final Intent intent = new Intent();
        intent.setAction("zj.zfenlly.StockService");
        final Intent eintent = new Intent(createExplicitFromImplicitIntent(context, intent));

//        Intent serviceIntent = new Intent("zj.zfenlly.StockService");
//            serviceIntent.putExtra("command", RKUpdateService.COMMAND_CHECK_REMOTE_UPDATING);
//            serviceIntent.putExtra("delay", 25000);
        context.startService(eintent);
//        Intent mIntent = new Intent();
//        mIntent.setAction("zj.zfenlly.StockService");//你定义的service的action
//        mIntent.setPackage("zj.zfenlly.tools");//这里你需要设置你应用的包名
//        context.startService(mIntent);

//        print("serviceIntent:" + serviceIntent);
    }

    private void startMonitorService(Context context) {
//        Intent serviceIntent = new Intent("zj.zfenlly.MonitorService");
////            serviceIntent.putExtra("command", RKUpdateService.COMMAND_CHECK_REMOTE_UPDATING);
////            serviceIntent.putExtra("delay", 25000);
//        context.startService(serviceIntent);
        Intent mIntent = new Intent();
        mIntent.setAction("zj.zfenlly.MonitorService");//你定义的service的action
        //mIntent.setPackage("zj.zfenlly.tools");//这里你需要设置你应用的包名
        final Intent eintent = new Intent(createExplicitFromImplicitIntent(context, mIntent));
        context.startService(eintent);

//        print("serviceIntent:" + serviceIntent);
    }
}
