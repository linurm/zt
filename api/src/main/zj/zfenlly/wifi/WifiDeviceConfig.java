package zj.zfenlly.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

public class WifiDeviceConfig extends Activity implements Observer {

	MainApplication mWifiApplication;
	private final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	TextView mTV1, mTV2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_config);
		mTV1 = (TextView) findViewById(R.id.textView1);
		mTV2 = (TextView) findViewById(R.id.textView2);

		mTV1.setOnClickListener(new MyListener());
		mTV2.setOnClickListener(new MyListener());

		mWifiApplication = (MainApplication) getApplication();
		mWifiApplication.addObserver(this);
	}

	private class MyListener implements OnClickListener {

		@SuppressLint("ShowToast")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.textView1:// ɨ������
				// listItem.clear();
				mWifiApplication.SendFind();
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

	private static final int HANDLE_DEVICE_CONFIG_GET = 0;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_DEVICE_CONFIG_GET: {
				// Log.i(TAG, "==========HANDLE_DEVICE_CONFIG_FIND");
				mTV1.setText("");
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
		if (qualifier.equals(MainApplication.DEVICE_CONFIG_GET)) {
			Message message = mHandler.obtainMessage(HANDLE_DEVICE_CONFIG_GET);
			mHandler.sendMessage(message);
		}
	}

}
