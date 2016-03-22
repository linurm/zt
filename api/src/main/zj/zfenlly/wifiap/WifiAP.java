package zj.zfenlly.wifiap;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.util.Log;

public class WifiAP {

	public static final int OPEN_INDEX = 0;
	public static final int WPA_INDEX = 1;
	public static final int WPA2_INDEX = 2;

	private static final int WPA2_PSK = 4;

	private static Context mContext = null;

	private static final int WIFI_AP_STATE_UNKNOWN = -1;
	private static final int WIFI_AP_STATE_ENABLED = 13;

	private final static String[] WIFI_STATE_TEXTSTATE = new String[] {
			"WIFI_STATE_DISABLING", "WIFI_STATE_DISABLED",
			"WIFI_STATE_ENABLING", "WIFI_STATE_ENABLED", "WIFI_STATE_UNKNOWN",
			"", "", "", "", "",
			"WIFI_AP_STATE_DISABLING",// 10
			"WIFI_AP_STATE_DISABLED", "WIFI_AP_STATE_ENABLING",
			"WIFI_AP_STATE_ENABLED", "WIFI_AP_STATE_FAILED" };

	private static WifiManager mWifiManager = null;
	private static WifiConfiguration mWifiConfig = null;

	private static final String TAG = WifiAP.class.getSimpleName();

	private static void print(String msg) {
		Log.i(TAG, msg);
	}

	public WifiAP(Context context) {
		mContext = context;
		initWifiTethering();
	}

	public static WifiConfiguration getWifiAPConfig() {

		Method method1 = null;
		try {
			method1 = mWifiManager.getClass().getMethod(
					"getWifiApConfiguration");

			mWifiConfig = (WifiConfiguration) method1.invoke(mWifiManager);
		} catch (Exception e) {
			print(e.getMessage());
			// toastText += "ERROR " + e.getMessage();
		}
		return mWifiConfig;
	}

	public static void setWifiAPConfig(WifiConfiguration mWc) {

		Method method1 = null;
		WifiConfiguration wc = mWc;

		if (mContext == null)
			return;

		try {
			if (getWifiAPState() == WIFI_AP_STATE_ENABLED) {
				method1 = mWifiManager.getClass().getMethod("setWifiApEnabled",
						WifiConfiguration.class, boolean.class);
				method1.invoke(mWifiManager, null, false); // true
				method1.invoke(mWifiManager, wc, true);
				print("ssid:" + wc.SSID);

			} else {
				method1 = mWifiManager.getClass().getMethod(
						"setWifiApConfiguration", WifiConfiguration.class);
				method1.invoke(mWifiManager, wc);
			}
		} catch (Exception e) {
			print(e.getMessage());
			// toastText += "ERROR " + e.getMessage();
		}

	}

	private static int getWifiAPState() {
		int state = WIFI_AP_STATE_UNKNOWN;

		if (mContext == null)
			return -1;

		try {
			Method method2 = mWifiManager.getClass()
					.getMethod("getWifiApState");
			state = (Integer) method2.invoke(mWifiManager);
		} catch (Exception e) {
		}
		print("getWifiAPState.state "
				+ (state == -1 ? "UNKNOWN" : WIFI_STATE_TEXTSTATE[state]));
		return state;
	}

	private static void initWifiTethering() {

		if (mContext == null)
			return;

		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);

	}

	public static int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
		if (mContext == null)
			return -1;

		if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
			return WPA_INDEX;
			// } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK))
			// {
		} else if (wifiConfig.allowedKeyManagement.get(WPA2_PSK)) {
			return WPA2_INDEX;
		}
		return OPEN_INDEX;
	}

	public static void setWifiAPConfig(int index, String ssid, String passwd) {

		if (mContext == null)
			return;

		// WifiConfiguration config = mWifiConfig;

		/**
		 * TODO: SSID in WifiConfiguration for soft ap is being stored as a raw
		 * string without quotes. This is not the case on the client side. We
		 * need to make things consistent and clean it up
		 */
		mWifiConfig.SSID = ssid;

		// print("ssid:" + mWifiConfig.SSID);

		switch (index) {
		case OPEN_INDEX:
			mWifiConfig.allowedKeyManagement.set(KeyMgmt.NONE);

		case WPA_INDEX:
			mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
			mWifiConfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (passwd.length() != 0) {
				String password = passwd;
				mWifiConfig.preSharedKey = password;
			}

		case WPA2_INDEX:
			// config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
			mWifiConfig.allowedKeyManagement.set(WPA2_PSK);
			mWifiConfig.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
			if (passwd.length() != 0) {
				String password = passwd;
				mWifiConfig.preSharedKey = password;
			}
		}

		return;
	}

	public static void setSoftapEnabled(boolean enable) {
		int state = WIFI_AP_STATE_UNKNOWN;

		if (mContext == null)
			return;

		int wifiState = mWifiManager.getWifiState();
		if (enable
				&& ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
			mWifiManager.setWifiEnabled(false);
			// Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 1);
		}

		Method method2 = null;

		try {
			method2 = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);

			state = (Integer) method2.invoke(mWifiManager, mWifiConfig, enable);

		} catch (Exception e) {
			print(e.getMessage());
			// toastText += "ERROR " + e.getMessage();
		}

		/**
		 * If needed, restore Wifi on tether disable
		 */
		if (!enable) {
			// int wifiSavedState = 0;
			// wifiSavedState = 0;
			// try {
			// wifiSavedState = Settings.Global.getInt(cr,
			// Settings.Global.WIFI_SAVED_STATE);
			// } catch (Settings.SettingNotFoundException e) {
			// ;
			// }
			// if (wifiSavedState == 1) {
			// mWifiManager.setWifiEnabled(true);
			// Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
			// }
		}
		print("state: " + state);

		// if (mWifiManager.getWifiApState() ==
		// WifiManager.WIFI_AP_STATE_ENABLED) {
		// mWifiManager.setWifiApEnabled(null, false);
		// mWifiManager.setWifiApEnabled(mWifiConfig, true);
		// } else {
		// mWifiManager.setWifiApConfiguration(mWifiConfig);
		// }
	}
}
