package zj.zfenlly.wifiap;

import android.annotation.SuppressLint;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class WifiApFragment extends Fragment implements
		AdapterView.OnItemSelectedListener, TextWatcher {

	public MainApplication mZfenllyApplication;
	private int mColorRes = -1;

	@ViewInject(R.id.ap)
	private Button mApButton;
	@ViewInject(R.id.cancel)
	private Button mCancelButton;
	@ViewInject(R.id.ssid)
	private EditText mSsid;
	@ViewInject(R.id.password)
	private EditText mPassword;
	// private TextView mSecurity;
	private WifiConfiguration mWifiConf;
	@ViewInject(R.id.security)
	private Spinner mSecurity;// mSecurity = ((Spinner)
								// mView.findViewById(R.id.security));

	@ViewInject(R.id.fields)
	private LinearLayout mLinearLayout;

	// boolean mIsWifiEnabled = false;
	// View mView = null;
	private int mSecurityTypeIndex = WifiAP.OPEN_INDEX;

	private final String TAG = this.getClass().getName();
			//.substring(this.getClass().getName().lastIndexOf(".") + 1);

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	public String mName;

	public WifiApFragment(int colorRes, String name) {
		mColorRes = colorRes;
		mName = name;
		setRetainInstance(true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mColorRes", mColorRes);
	}

	@OnClick(R.id.ap)
	public void onAPClick(View v) {
		// TODO Auto-generated method stub
		mWifiConf = WifiAP.getWifiAPConfig();
		mWifiConf.SSID = mSsid.getText().toString();
		mWifiConf.preSharedKey = mPassword.getText().toString();
		WifiAP.setWifiAPConfig(mWifiConf);
		// setWifiApEnabled(true);
		WifiAP.setSoftapEnabled(true);
	}

	@OnClick(R.id.cancel)
	public void onCancelClick(View v) {
		// TODO Auto-generated method stub
		// setWifiApEnabled(false);
		mWifiConf = WifiAP.getWifiAPConfig();
		mWifiConf.SSID = mSsid.getText().toString();
		mWifiConf.preSharedKey = mPassword.getText().toString();
		WifiAP.setWifiAPConfig(mWifiConf);
		WifiAP.setSoftapEnabled(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mColorRes = savedInstanceState.getInt("mColorRes");

		int color = getResources().getColor(mColorRes);
		View view = inflater.inflate(R.layout.fragment_wifi_ap, null);
		view.setBackgroundColor(color);
		ViewUtils.inject(this, view);

		// updateStatusDisplay();
		initScreen();
		showSecurityFields();
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setContentView(R.layout.fragment_wifi_ap);

		WifiAP mWifiAP = new WifiAP(getActivity());

		mSecurityTypeIndex = WifiAP.getSecurityTypeIndex(WifiAP
				.getWifiAPConfig());

		// print("ddddddddddddd");
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		mSecurityTypeIndex = position;
		showSecurityFields();
		// validate();
	}

	private void initScreen() {

		if (WifiAP.getWifiAPConfig() != null) {
			mSsid.setText(WifiAP.getWifiAPConfig().SSID);
			mSecurity.setSelection(mSecurityTypeIndex);
			if (mSecurityTypeIndex == WifiAP.WPA_INDEX
					|| mSecurityTypeIndex == WifiAP.WPA2_INDEX) {
				mPassword.setText(WifiAP.getWifiAPConfig().preSharedKey);
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		print("onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		print("onPause");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		print("onDestroyView");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private void showSecurityFields() {
		if (mSecurityTypeIndex == WifiAP.OPEN_INDEX)
			mLinearLayout.setVisibility(View.GONE);
		else
			mLinearLayout.setVisibility(View.VISIBLE);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		;// print("afterTextChanged");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		;// print("beforeTextChanged");
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		;// print("onTextChanged");
	}

}
