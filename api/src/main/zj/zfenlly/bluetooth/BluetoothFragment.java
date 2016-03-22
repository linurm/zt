package zj.zfenlly.bluetooth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;


@SuppressLint("ValidFragment")
public class BluetoothFragment extends Fragment implements Name {
	public BluetoothAdapter mBluetoothAdapter = null;

	ConnectThread mConnectThread = null;
	ConnectedThread mConnectedThread = null;
	@ViewInject(R.id.button1)
	Button mSearchButton;

	@ViewInject(R.id.listView1)
	ListView mListSearch;
	SimpleAdapter mSearchAdapter = null;
	List<Map<String, String>> mSearchListData = null;
	String[] s = new String[20];
	@ViewInject(R.id.listView2)
	ListView mListMatch;
	SimpleAdapter mMatchAdapter = null;
	List<Map<String, String>> mMatchListData = null;
	private WifiAdmin mWifiAdmin;
	WifiDevice mWifiDevice = null;
	IntentFilter filter = null;

	private int mColorRes = -1;
	public String mName;
	private final String TAG = this.getClass().getName();

	private void print(String msg) {
		Log.i(TAG, msg);
	}

	public BluetoothFragment() {
		this(R.color.white, "color");
	}

	public BluetoothFragment(int colorRes, String name) {
		mColorRes = colorRes;
		setName(name);
		setRetainInstance(true);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return mName;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		mName = name;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mColorRes = savedInstanceState.getInt("mColorRes");

		print("onCreateView");

		int color = getResources().getColor(mColorRes);
		View view = inflater.inflate(R.layout.fragment_bluetooth, null);
		view.setBackgroundColor(color);
		ViewUtils.inject(this, view);

		mSearchListData = new ArrayList<Map<String, String>>();
		mSearchAdapter = new SimpleAdapter(getActivity(), mSearchListData,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"mac" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		mListSearch.setAdapter(mSearchAdapter);

		// 匹配相关

		mMatchListData = new ArrayList<Map<String, String>>();
		mMatchAdapter = new SimpleAdapter(getActivity(), mMatchListData,
				android.R.layout.simple_list_item_2, new String[] { "name",
						"mac" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		mListMatch.setAdapter(mMatchAdapter);

		clearPairedDevice();

		return view;
	}

	@OnClick(R.id.button1)
	public void onClick(View v) {
		mSearchListData.clear();
		mSearchAdapter.notifyDataSetChanged();
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();

		}
		mBluetoothAdapter.startDiscovery();
		mWifiAdmin.startScan();
		// mConnectThread = new ConnectThread(null);
		// mConnectThread.start();
	}

	@OnItemClick(R.id.listView1)
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) ((AdapterView<ListAdapter>) mListSearch)
				.getItemAtPosition(arg2);
		String name = map.get("name");
		String mac = map.get("mac");

		BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(mac);
		try {
			Boolean returnValue = false;
			if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
				// 利用反射方法调用BluetoothDevice.createBond(BluetoothDevice
				// remoteDevice);
				Method createBondMethod = BluetoothDevice.class
						.getMethod("createBond");
				Log.d("BlueToothTestActivity", "开始配对");
				returnValue = (Boolean) createBondMethod.invoke(btDev);

			} else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
				;// connect(btDev);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, " name:" + name + " mac:" + mac);
	}

	@OnItemClick(R.id.listView2)
	public void onItemClick2(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unchecked")
		HashMap<String, String> map = (HashMap<String, String>) ((AdapterView<ListAdapter>) mListMatch)
				.getItemAtPosition(arg2);
		String name = map.get("name");
		String mac = map.get("mac");

		BluetoothDevice btDev = mBluetoothAdapter.getRemoteDevice(mac);
		try {
			Boolean returnValue = false;
			if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {

			} else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
				;// connect(btDev);
				showCustomDia(btDev);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.d(TAG, " name:" + name + " mac:" + mac);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Register the BroadcastReceiver
		filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		getActivity().registerReceiver(mReceiver, filter); // Don't forget to
															// unregister

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mWifiAdmin = new WifiAdmin(getActivity());
		if (mBluetoothAdapter == null) {
			Toast.makeText(getActivity(), "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_SHORT)
					.show();
			getActivity().finish();
		}

		// 如果本地蓝牙没有开启，则开启
		if (!mBluetoothAdapter.isEnabled()) {
			// 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
			// 那么将会收到RESULT_OK的结果，
			// 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
			Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(mIntent, 1);

			// 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
			// mBluetoothAdapter.enable();
			// mBluetoothAdapter.disable();//关闭蓝牙
		}
		// if (!mBluetoothAdapter.isDiscovering()) {
		// Intent mIntent = new Intent(
		// BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		// mIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
		// startActivityForResult(mIntent, 2);
		// }

		// 搜索相关

	}

	void clearPairedDevice() {
		mMatchListData.clear();
		mMatchAdapter.notifyDataSetChanged();
	}

	void updatePairedDevice() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		// If there are paired devices
		if (pairedDevices.size() > 0) {
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {

				Map<String, String> m = new HashMap<String, String>();
				m.put("name", device.getName());
				m.put("mac", device.getAddress());

				int isexist = 0;
				for (int i = 0; i < mMatchListData.size(); i++) {
					Map<String, String> ma = mMatchListData.get(i);
					if (ma.get("name").equals(device.getName())
							&& ma.get("mac").equals(device.getAddress())) {// have
						isexist = 1;
						break;
					}
				}
				if (isexist == 0) {
					mMatchListData.add(m);
					mMatchAdapter.notifyDataSetChanged();
				}
				// Log.d(TAG, device.getName() + ":" + device.getAddress());
			}
		}
		;
	}

	// Create a BroadcastReceiver for ACTION_FOUND
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			BluetoothDevice device = null;
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				Map<String, String> m = new HashMap<String, String>();
				m.put("name", device.getName());
				m.put("mac", device.getAddress());
				int isexist = 0;
				for (int i = 0; i < mSearchListData.size(); i++) {
					Map<String, String> ma = mSearchListData.get(i);
					if (ma.get("name").equals(device.getName())
							&& ma.get("mac").equals(device.getAddress())) {// have
						isexist = 1;
						break;
					}
				}
				if (isexist == 0) {
					mSearchListData.add(m);
					mSearchAdapter.notifyDataSetChanged();
				}

			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				int cur_bond_state = intent.getIntExtra(
						BluetoothDevice.EXTRA_BOND_STATE,
						BluetoothDevice.BOND_NONE);
				int previous_bond_state = intent.getIntExtra(
						BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE,
						BluetoothDevice.BOND_NONE);

				Log.v(TAG, "### cur_bond_state ##" + cur_bond_state
						+ " ~~ previous_bond_state" + previous_bond_state);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				Log.d(TAG, "finished");
				updatePairedDevice();
			} else if (BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE
					.equals(action)) {
				Log.d(TAG, "discoverable");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				Log.d(TAG, "started");
				clearPairedDevice();
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					Log.d("BlueToothTestActivity", "正在配对......");
					break;
				case BluetoothDevice.BOND_BONDED:
					Log.d("BlueToothTestActivity", "完成配对");
					// connect(device);// 连接设备
					break;
				case BluetoothDevice.BOND_NONE:
					Log.d("BlueToothTestActivity", "取消配对");
				default:
					break;
				}
			}

		}
	};

	private void showCustomDia(final BluetoothDevice mBluetoothDevice) {
		final Context context = getActivity();
		List<ScanResult> mListSR;
		// int index = 0;
		// 初始化自定义布局参数
		LayoutInflater layoutInflater = getActivity().getLayoutInflater();

		// 为了能在下面的OnClickListener中获取布局上组件的数据，必须定义为final类型.
		final View customLayout = layoutInflater.inflate(
				R.layout.main_dialog_custom, (ViewGroup) getActivity()
						.findViewById(R.id.customDialog));

		mListSR = mWifiAdmin.getWifiList();

		for (int i = 0; i < mListSR.size(); i++) {
			try {
				s[i] = new String(mListSR.get(i).SSID);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 创建对话框
		new AlertDialog.Builder(context).setTitle("选择wifi设备")
		// 设置对话框标题
				.setView(customLayout)
				// 为对话框添加组件
				/*
				 * .setSingleChoiceItems(s, 0, new
				 * DialogInterface.OnClickListener() {
				 * 
				 * @Override public void onClick(DialogInterface dialog, int
				 * which) { EditText wifi = (EditText) customLayout
				 * .findViewById(R.id.wifi); wifi.setText(s[which]); } })
				 */.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 获取自定义布局上的输入框的值
						EditText wifi = (EditText) customLayout
								.findViewById(R.id.wifi);
						EditText mima = (EditText) customLayout
								.findViewById(R.id.mima);

						mWifiDevice = new WifiDevice(wifi.getText().toString(),
								mima.getText().toString());

						ConnectThread mConnectThread = new ConnectThread(
								mBluetoothDevice);
						mConnectThread.start();
					}
				})// 设置对话框[肯定]按钮
				.setNegativeButton("取消", null)// 设置对话框[否定]按钮
				.show();
	}

	private class WifiDevice {
		String m_wifi, m_mima;

		WifiDevice(String wifi, String mima) {
			m_wifi = wifi;
			m_mima = mima;
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
		print("onDestroy");
		getActivity().unregisterReceiver(mReceiver);
		// System.exit(0);
	}

	private class ConnectThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final BluetoothDevice mmDevice;

		public ConnectThread(BluetoothDevice device) {
			// Use a temporary object that is later assigned to mmSocket,
			// because mmSocket is final
			BluetoothSocket tmp = null;
			mmDevice = device;
			// Get a BluetoothSocket to connect with the given BluetoothDevice
			try {
				// MY_UUID is the app's UUID string, also used by the server
				// code
				UUID uuid = UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB");
				tmp = device.createRfcommSocketToServiceRecord(uuid);
			} catch (IOException e) {
			}
			mmSocket = tmp;
		}

		public void run() {
			// Cancel discovery because it will slow down the connection
			if (mBluetoothAdapter.isDiscovering())
				mBluetoothAdapter.cancelDiscovery();
			try {
				// Connect the device through the socket. This will block
				// until it succeeds or throws an exception
				mmSocket.connect();
			} catch (IOException connectException) {
				// Unable to connect; close the socket and get out
				try {
					mmSocket.close();
				} catch (IOException closeException) {
				}
				return;

			}
			// Do work to manage the connection (in a separate thread)
			// manageConnectedSocket(mmSocket);
			ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
			mConnectedThread.start();
			Log.d(TAG, "CONNECT RUN");
			// cancel();
		}

		// Will cancel an in-progress connection, and close the socket
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	private class ConnectedThread extends Thread {
		private final BluetoothSocket mmSocket;
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {

			// mWifiDevice;
			byte[] buffer = (mWifiDevice.m_wifi + ":" + mWifiDevice.m_mima)
					.getBytes();

			// Keep listening to the InputStream until an exception occurs
			// while (true) {
			// Read from the InputStream
			write(buffer);
			Log.d(TAG, "write");
			// Send the obtained bytes to the UI activity
			// mHandler.obtainMessage(MESSAGE_READ, bytes, -1,
			// buffer).sendToTarget();
			// }
			// cancel();
		}

		// Call this from the main activity to send data to the remote device
		public void write(byte[] bytes) {

			try {
				mmOutStream.write(bytes);
			} catch (IOException e) {
			}
		}

		// Call this from the main activity to shutdown the connection
		public void cancel() {
			try {
				mmSocket.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == getActivity().RESULT_OK) {
				Toast.makeText(getActivity(), "蓝牙已经开启", Toast.LENGTH_SHORT)
						.show();
			} else if (resultCode == getActivity().RESULT_CANCELED) {
				Toast.makeText(getActivity(), "不允许蓝牙开启", Toast.LENGTH_SHORT)
						.show();
				getActivity().finish();
			}
		}

		if (requestCode == 2) {
			if (resultCode == getActivity().RESULT_OK) {
				Toast.makeText(getActivity(), "对周围可见", Toast.LENGTH_SHORT)
						.show();
			} else if (resultCode == getActivity().RESULT_CANCELED) {
				Toast.makeText(getActivity(), "不允许对周围可见", Toast.LENGTH_SHORT)
						.show();
				getActivity().finish();
			}
		}

	}

}
