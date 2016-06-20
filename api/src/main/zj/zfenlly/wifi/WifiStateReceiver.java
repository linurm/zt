package zj.zfenlly.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.view.View;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiFragment;


public class WifiStateReceiver extends BroadcastReceiver {

	//Context mcontext;
	private WifiFragment mWifiFragment;


	// private WifiApplication mWifiApplication = null;

	public WifiStateReceiver(WifiFragment mWA, View view) {
		// TODO Auto-generated constructor stub
		// context.getApplicationInfo().
		this.mWifiFragment = mWA;
		//ViewUtils.inject(this.mWifiFragment, view);

		mWifiFragment.mWifiStrength.setImageResource(R.drawable.levellist);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println(intent.getAction());
		if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
			int strength = this.mWifiFragment.mApplication
					.GetWifiLevel();
			// System.out.println("当前信号 " + strength);
			mWifiFragment.mWifiStrength.setImageLevel(strength);

		} else if (intent.getAction().equals(
				WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

			NetworkInfo info = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			System.out.println("网络状态改变  " + info.getState());

			if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {// 如果断开连接
				mWifiFragment.mWifiStrength.setImageLevel(0);
				mWifiFragment.mWifiSSID.setText("");
				mWifiFragment.mWifiEvent.setText("断开连接");
			} else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
				mWifiFragment.mWifiSSID.setText(this.mWifiFragment.mApplication
						.GetSSID());
				mWifiFragment.mWifiEvent.setText("已连接");
				this.mWifiFragment.mApplication.SSIDNAME = this.mWifiFragment.mApplication
						.GetSSID();
			} else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
				mWifiFragment.mWifiSSID.setText(this.mWifiFragment.mApplication
						.GetSSID());
				mWifiFragment.mWifiEvent.setText("正在连接...");
			}
		} else if (intent.getAction().equals(
				WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			// WIFI开关
			int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_DISABLED);
			System.out.println("wifi状态改变 " + wifistate);
			if (wifistate == WifiManager.WIFI_STATE_DISABLED) {// 如果关闭
				mWifiFragment.mWifiStrength.setImageLevel(0);
			}
		} else if (intent.getAction().equals(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			// System.out.println("wifi 烧苗结果 ");
			this.mWifiFragment.mApplication.ScanResults();
		}

	}

}