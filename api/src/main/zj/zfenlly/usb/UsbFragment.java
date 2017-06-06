package zj.zfenlly.usb;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;

import java.util.HashMap;
import java.util.Iterator;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.main.MainApplication;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class UsbFragment extends BaseFragment {

	public MainApplication mZfenllyApplication;
	private final String TAG = this.getClass().getName()
			.substring(this.getClass().getName().lastIndexOf(".") + 1);
	private static final String ACTION_USB_PERMISSION = "zj.zfenlly.usb.USB_PERMISSION";
	private int mColorRes = -1;
	private WakeLock wakeLock = null;
	private UsbManager mUsbManager;

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	public UsbFragment() {
		this(R.color.white, "usb");
	}
	public UsbFragment(int colorRes,String name) {
		super(name, false);
		mColorRes = colorRes;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}

	private void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) getActivity().getSystemService(
					Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "xyz");
			if (null != wakeLock) {
				Log.i(TAG, "call acquireWakeLock");
				wakeLock.acquire();
			}
		}
	}

	private void releaseWakeLock() {
		if (null != wakeLock && wakeLock.isHeld()) {
			Log.i(TAG, "call releaseWakeLock");
			wakeLock.release();
			wakeLock = null;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		int color = getResources().getColor(mColorRes);
		// construct the RelativeLayout
		View view = inflater.inflate(R.layout.fragment_usb, null);
		view.setBackgroundColor(color);
		ViewUtils.inject(this, view);

		return view;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// final UsbManager mUsbManager = (UsbManager)
		// getSystemService(Context.USB_SERVICE);
		mUsbManager = (UsbManager) getActivity().getSystemService(
				Context.USB_SERVICE);

		if (mUsbManager == null) {
			return;
		} else {
			;// Log.i(TAG, "usb�豸��" + String.valueOf(mUsbManager.toString()));
		}
		HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
		Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
		while (deviceIterator.hasNext()) {
			UsbDevice device = deviceIterator.next();
			Log.d(TAG,
					"device: " + device.getVendorId() + ": "
							+ device.getDeviceId());
			// Toast.makeText(this, device.getVendorId(), 10).show();
		}
		new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Log.d(TAG, "BroadcastReceiver: ");
				if (ACTION_USB_PERMISSION.equals(action)) {
					synchronized (this) {
						UsbDevice tmpDevice = (UsbDevice) intent
								.getParcelableExtra(UsbManager.EXTRA_DEVICE);

						if (intent.getBooleanExtra(
								UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
							if (tmpDevice != null) {
								Log.d(TAG, "permission granted for device "
										+ tmpDevice);

								// call method to set up device communication
								// showDevice(tmpDevice);
							}
						} else {
							Log.d(TAG, "permission denied for device "
									+ tmpDevice);
						}
					}
				} else {
					if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
						UsbDevice device = (UsbDevice) intent
								.getParcelableExtra(UsbManager.EXTRA_DEVICE);
						if (device != null) {
							Log.d(TAG, "device " + device
									+ "have been detached ");

							// call your method that cleans up and closes
							// communication with the device

						}

					}
				}
			}
		};
		acquireWakeLock();
		//Log.d(TAG, "ddddddddddddd");
	}

	@Override
	public void onPause() {
		super.onPause();
//		getActivity().unregisterReceiver(mUsbReceiver);

	}

	@Override
	public void onResume() {
		super.onResume();
		// mPermissionIntent = PendingIntent.getBroadcast(getActivity(), 0, new
		// Intent(
		// ACTION_USB_PERMISSION), 0);
		// IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		// getActivity().registerReceiver(mUsbReceiver, filter);
		//
		// mUsbManager.requestPermission(device, mPermissionIntent);

	}
	

	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		print("onDestroyView");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		releaseWakeLock();
	}

}
