package zj.zfenlly.net;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.lang.reflect.Method;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;


/**
 * Created by Administrator on 2016/5/30.
 */
public class WifiOFFActivity extends Activity {

    @ViewInject(R.id.wifion)
    public Button wifion;
    @ViewInject(R.id.wifioff)
    public Button wifioff;
    @ViewInject(R.id.wifiok)
    public Button wifiok;
    @ViewInject(R.id.net3gon)
    public Button net3gon;
    @ViewInject(R.id.net3goff)
    public Button net3goff;
    private WifiAdmin mWifiAdmin = null;

    private boolean screen_landscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nettest);
        mWifiAdmin = new WifiAdmin(this);
        ViewUtils.inject(this);
        mWifiAdmin.closeWifi();
        //finish();
        //wifiok.setVisibility(View.VISIBLE);
        startOtherActivity();
        Log.e("wifi act", "onCreate wifi activity");
        finish();
    }

//    public final void setMobileNetEnable(boolean on) {

    private void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try {
            setMobileDataEnabl = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    }

//    public boolean getMobileNet() {
//        ConnectivityManager connMgr = (ConnectivityManager)
//                getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//        boolean isMobileConn = networkInfo.isConnected();
//        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        boolean isWifiConn = networkInfo.isConnected();
//        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//         isMobileConn = networkInfo.isConnected();
//        Log.e("", "Wifi connected: " + isWifiConn);
//        Log.e("", "Mobile connected: " + isMobileConn);
//        return isMobileConn;
//    }

    public boolean getMobileNet() {
        boolean isMobileDataEnable = false;


        Object[] arg = null;
        try {
            isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isMobileDataEnable;
    }


    public boolean invokeMethod(String methodName,
                                Object[] arg) throws Exception {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Class ownerClass = mConnectivityManager.getClass();

        Class[] argsClass = null;
        if (arg != null) {
            argsClass = new Class[1];
            argsClass[0] = arg.getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);

        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);

        return isOpen;
    }

    public Object invokeBooleanArgMethod(String methodName,
                                         boolean value) throws Exception {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        Class ownerClass = mConnectivityManager.getClass();

        Class[] argsClass = new Class[1];
        argsClass[0] = boolean.class;

        Method method = ownerClass.getMethod(methodName, argsClass);

        return method.invoke(mConnectivityManager, value);
    }

    @OnClick(R.id.net3gon)
    public void net3gon(View v) {
        if (!getMobileNet()) {
            toggleMobileData(this, true);
        }
    }

    //
//    @OnClick(R.id.synctime)
//    public void synctimes(View v) {
//
//    }
//
    @OnClick(R.id.net3goff)
    public void net3goff(View v) {
        if (getMobileNet()) {
            toggleMobileData(this, false);
        }
    }

    @OnClick(R.id.wifion)
    public void wifion(View v) {
        mWifiAdmin.openWifi();
        //finish();
        startOtherActivity();
        //finish();
    }

    @OnClick(R.id.wifioff)
    public void wifioff(View v) {
        mWifiAdmin.closeWifi();
        //finish();
        wifiok.setVisibility(View.VISIBLE);
        startOtherActivity();
        //finish();
    }

    @OnClick(R.id.wifiok)
    public void wifiok(View v) {
        mWifiAdmin.openWifi();
        //finish();
        //startOtherActivity();
        finish();
    }

    private void startOtherActivity() {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.teamlava.castlestory41", "com.apportable.activity.VerdeActivity");
        intent.setComponent(cn);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
//        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            screen_landscape = true;
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onResume();
        Log.e("wifi act", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(screen_landscape){
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //}
        Log.e("wifi act", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mWifiAdmin.openWifi();
        if (mWifiAdmin.isWifiEnabled()) {
            wifion.setVisibility(View.INVISIBLE);
            wifioff.setVisibility(View.VISIBLE);
        } else {
            wifion.setVisibility(View.VISIBLE);
            wifioff.setVisibility(View.INVISIBLE);
        }
        if (getMobileNet()) {
            net3gon.setVisibility(View.INVISIBLE);
            net3goff.setVisibility(View.VISIBLE);
        } else {
            net3gon.setVisibility(View.VISIBLE);
            net3goff.setVisibility(View.INVISIBLE);

        }
        Log.e("wifi act", "onStart wifi activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("wifi act", "onStop wifi activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("wifi act", "onDestroy wifi activity");
    }


//    public class NetworkReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager conn = (ConnectivityManager)
//                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = conn.getActiveNetworkInfo();
//
//            // Checks the user prefs and the network connection. Based on the result, decides whether
//            // to refresh the display or keep the current display.
//            // If the userpref is Wi-Fi only, checks to see if the device has a Wi-Fi connection.
//            if (WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//                // If device has its Wi-Fi connection, sets refreshDisplay
//                // to true. This causes the display to be refreshed when the user
//                // returns to the app.
//                refreshDisplay = true;
//                Toast.makeText(context, "wifi connected", Toast.LENGTH_SHORT).show();
//
//                // If the setting is ANY network and there is a network connection
//                // (which by process of elimination would be mobile), sets refreshDisplay to true.
//            } else if (ANY.equals(sPref) && networkInfo != null) {
//                refreshDisplay = true;
//
//                // Otherwise, the app can't download content--either because there is no network
//                // connection (mobile or Wi-Fi), or because the pref setting is WIFI, and there
//                // is no Wi-Fi connection.
//                // Sets refreshDisplay to false.
//            } else {
//                refreshDisplay = false;
//                Toast.makeText(context, "lost connection", Toast.LENGTH_SHORT).show();
//            }
//        }
}
