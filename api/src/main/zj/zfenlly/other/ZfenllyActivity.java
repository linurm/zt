package zj.zfenlly.other;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import zj.zfenlly.tools.R;
import zj.zfenlly.usb.UsbFragment;
import zj.zfenlly.wifi.WifiFragment;

public class ZfenllyActivity extends Activity {

	private TextView wifi_tv;
	private TextView usb_tv;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all);
		
		mContext = this;

		usb_tv = (TextView) findViewById(R.id.tv_usb);
		usb_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, UsbFragment.class);

				startActivity(intent);
			}

		});

		wifi_tv = (TextView) findViewById(R.id.tv_wifi);
		wifi_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, WifiFragment.class);

				startActivity(intent);
			}

		});
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}
}
