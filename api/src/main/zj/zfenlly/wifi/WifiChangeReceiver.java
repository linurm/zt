package zj.zfenlly.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import zj.zfenlly.net.WifiStatusLoader;

/**
 * Created by Administrator on 2016/8/17.
 */
public class WifiChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {//这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.e("000", "wifiState" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    //Toast.makeText(context,"wifi off",Toast.LENGTH_LONG).show();
                    WifiStatusLoader.getInstance(context).WifiDisableDisplay();
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //Toast.makeText(context,"wifi on",Toast.LENGTH_LONG).show();
                    WifiStatusLoader.getInstance(context).WifiEnableDisplay();
                    break;
                //
            }
        } else if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                Log.e("000", "netWork has lost");
                return;
            }
            NetworkInfo tmpInfo = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            Log.e("000", tmpInfo.toString() + " {isConnected = " + tmpInfo.isConnected() + "}");

            if (tmpInfo.isConnected() == true) {
                WifiStatusLoader.getInstance(context).StartAPP();
            }

        }
//        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的连接状态
//            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (null != parcelableExtra) {
//                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                NetworkInfo.State state = networkInfo.getState();
//                if (state == NetworkInfo.State.CONNECTED) {
//                    ;//showWifiCconnected(context);
//                    Log.e("000", "00000000000000000000000000000000000000000000000000");
//                }
//                /**else if(state==State.DISCONNECTED){
//                 showWifiDisconnected(context);
//                 }*///昨天写的这个方法，在坐地铁的时候发现，如果地铁上有无效的wifi站点，手机会自动连接，但是连接失败后还是会接到广播，所以不能用了
//            }
//        }

    }
}
