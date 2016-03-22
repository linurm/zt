package zj.zfenlly.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

public class WifiDeviceList extends Activity implements Observer {
	// private Button findBtn;
	private ListView mFindDevices;
	private Button mBuzzer;
	MainApplication mWifiApplication;

	private String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
	private Map<String, Object> map;

	private WifiListViewAdapter listViewAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_find_list);
		// findBtn = (Button) findViewById(R.id.find);
		mFindDevices = (ListView) findViewById(R.id.find_device);
		mBuzzer = (Button) findViewById(R.id.buzzer_on);

		mBuzzer.setOnClickListener(new MyListener());
		// mFindDevices.setAdapter(listViewAdapter);
		// Log.i(TAG, "setAdapter 1");
		// mFindDevices.setAdapter(listViewAdapter);
		// Log.i(TAG, "setAdapter 2");
		// findBtn.setOnClickListener(new MyListener());
		mWifiApplication = (MainApplication) getApplication();
		mWifiApplication.addObserver(this);

		clearUI();
		mWifiApplication.SendFind();

	}

	private class MyListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.buzzer_on:
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWifiApplication.deleteObserver(this);
	}

	// private class MyListener implements OnClickListener {
	//
	// @SuppressLint("ShowToast")
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// switch (v.getId()) {
	//
	// case R.id.find:
	// // mHandler.sendEmptyMessage(MESSAGE_DEVICE_CONFIG);
	// clearUI();
	// mWifiApplication.SendFind();
	// //
	//
	// break;
	// default:
	// break;
	// }
	// }
	// }

	private void clearUI() {
		if (listItems == null) {

		} else {
			if (listItems.size() != 0) {
				listItems.clear();
				listViewAdapter.notifyDataSetChanged();
			}
		}
		Log.i(TAG, "clearUI");

		// listViewAdapter.notifyDataSetChanged();
	}

	private void updateUI() {

		List<String> mls = mWifiApplication.getFoundsList();
		for (String ml : mls) {
			// Log.i(TAG, "22222222 " + ml.split("=")[0]);
			map = new HashMap<String, Object>();
			map.put("title", ml.split("=")[0]);
			map.put("info", ml.split("=")[1]);
			listItems.add(map);
		}
		listViewAdapter = new WifiListViewAdapter(this, listItems);
		mFindDevices.setAdapter(listViewAdapter);
		// listViewAdapter.notifyDataSetChanged();
	}

	private static final int HANDLE_DEVICE_CONFIG_FIND = 0;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_DEVICE_CONFIG_FIND: {
				// Log.i(TAG, "==========HANDLE_DEVICE_CONFIG_FIND");
				updateUI();
			}
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		Log.i(TAG, "update(" + arg + ")");
		String qualifier = (String) arg;
		if (qualifier.equals(MainApplication.DEVICE_CONFIG_FIND)) {
			Message message = mHandler.obtainMessage(HANDLE_DEVICE_CONFIG_FIND);
			mHandler.sendMessage(message);
		}
	}
}
