package zj.zfenlly.wifi;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import zj.zfenlly.tools.R;

public class WifiAccessPoint {
	enum PskType {
		UNKNOWN, WPA, WPA2, WPA_WPA2
	}

	String ssid;
	String bssid;
	int security;
	int networkId;
	boolean wpsAvailable = false;

	PskType pskType = PskType.UNKNOWN;

	final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);
	private int mRssi;
	static final int SECURITY_NONE = 0;
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_EAP = 3;

	public static final int[] STATE_SECURED = { R.attr.state_encrypted };
	public static final int[] STATE_NONE = {};

	ScanResult mScanResult;

	WifiAccessPoint(ScanResult result) {
		loadResult(result);
		// refresh();
	}

	private static int getSecurity(ScanResult result) {
		if (result.capabilities.contains("WEP")) {
			return SECURITY_WEP;
		} else if (result.capabilities.contains("PSK")) {
			return SECURITY_PSK;
		} else if (result.capabilities.contains("EAP")) {
			return SECURITY_EAP;
		}
		return SECURITY_NONE;
	}

	private void loadResult(ScanResult result) {
		ssid = result.SSID;
		bssid = result.BSSID;
		security = getSecurity(result);
		wpsAvailable = security != SECURITY_EAP
				&& result.capabilities.contains("WPS");
		if (security == SECURITY_PSK)
			pskType = getPskType(result);
		networkId = -1;
		mRssi = result.level;
		mScanResult = result;
	}

	private PskType getPskType(ScanResult result) {
		boolean wpa = result.capabilities.contains("WPA-PSK");
		boolean wpa2 = result.capabilities.contains("WPA2-PSK");
		if (wpa2 && wpa) {
			return PskType.WPA_WPA2;
		} else if (wpa2) {
			return PskType.WPA2;
		} else if (wpa) {
			return PskType.WPA;
		} else {
			Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
			return PskType.UNKNOWN;
		}
	}

	int getLevel() {
		if (mRssi == Integer.MAX_VALUE) {
			return -1;
		}
		return WifiManager.calculateSignalLevel(mRssi, 4);
	}

	static String convertToQuotedString(String string) {
		return "\"" + string + "\"";
	}

}
