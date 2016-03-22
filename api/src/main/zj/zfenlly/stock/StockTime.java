package zj.zfenlly.stock;

import java.text.SimpleDateFormat;
import android.annotation.SuppressLint;
import android.util.Log;

public class StockTime {

	private static String from1 = "9:30";
	private static String to1 = "11:30";
	private static String from2 = "13:00";
	private static String to2 = "15:03";

	private static boolean isAM(String hm) {
		String[] t = hm.split(":");

		// int h = Integer.parseInt(t[0]);
		//
		// if (h < 12) {
		// return true;
		// } else {
		// return false;
		// }
		// S h = Integer.parseInt(t[2]);

		if (t[2].equals("上午")) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean isGreator(String hm, String benchmark) {
		String[] t = hm.split(":");
		String[] b = benchmark.split(":");

		int h = Integer.parseInt(t[0]);
		int m = Integer.parseInt(t[1]);
		int h_b = Integer.parseInt(b[0]);
		int m_b = Integer.parseInt(b[1]);

		if (h > h_b) {
			return true;
		} else if (h == h_b) {
			if (m >= m_b) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	private static boolean isnotGreator(String hm, String benchmark) {
		return isGreator(hm, benchmark) ? false : true;
	}

	private static boolean checkHours(String hm) {

		if (isAM(hm)) {

			if (isGreator(hm, from1) && isnotGreator(hm, to1)) {
				return true;
			}
		} else {
			if (isGreator(hm, from2) && isnotGreator(hm, to2)) {
				return true;
			}
		}

		// print("out of time");
		return false;
	}

	private final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	@SuppressWarnings("unused")
	private void print(String msg) {
		Log.i(TAG, msg);
	}

	@SuppressLint("SimpleDateFormat")
	public static boolean checkTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"HH:mm:a@yyyy-MM-dd@E");
		String date = sDateFormat.format(new java.util.Date());

		// :::13:54@2015-09-07@周一
		// date.charAt("-");
		String[] a = date.split("@");
		// print(":::" + a[3]);
		if (a[2].equals("周六")) {
			return false;
		} else if (a[2].equals("周日")) {
			return false;
		}

		return checkHours(a[0]);
	}
}
