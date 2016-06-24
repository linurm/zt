package zj.zfenlly.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;

import java.util.HashMap;
import java.util.List;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.net.SelectModeActivity;
import zj.zfenlly.other.Name;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class WifiFragment extends Fragment implements Name, Observer {
    public static final String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    private static final int MESSAGE_NETWORK_SCAN = 1;
    private static final int MESSAGE_NETWORK_CONNECT = 2;
    private static final int MESSAGE_NETWORK_CHANGE_STATE = 3;
    private static final int MESSAGE_NETWORK_CONFIG = 4;
    private static final int MESSAGE_DEVICE_CONFIG = 5;
    private final String TAG = this.getClass().getName();
    private final boolean BUG_T = false;

    @ViewInject(R.id.curNetWorkSSID)
    public TextView curNetWorkSSID;
    @ViewInject(R.id.curNetWorkEvent)
    public TextView curNetWorkEvent;
    public MainApplication mApplication;
    public String mName;
    @ViewInject(R.id.curWifiIV)
    ImageView mWifiStrength;
    @ViewInject(R.id.curNetWorkSSID)
    TextView mWifiSSID;
    @ViewInject(R.id.curNetWorkEvent)
    TextView mWifiEvent;
    // private WifiAdmin mWifiAdmin;
    WifiStateReceiver wifiReceiver;
    WifiConfiguration mWifiConfiguration;
    List<WifiConfiguration> listWCF;
    /**
     * Called when the activity is first created.
     */
    @ViewInject(R.id.allNetWork)
    private PullToRefreshListView allNetWork;
    @ViewInject(R.id.curWifiIV)
    private ImageView curWifiIV;
    @ViewInject(R.id.scan)
    private Button scan;
    @ViewInject(R.id.start)
    private Button start;
    @ViewInject(R.id.config)
    private Button config;
    private MyAdapter mMyAdapter;
    private int conState = 0;
    // 扫描结果列表
    private List<ScanResult> list;
    // .substring(this.getClass().getName().lastIndexOf(".") + 1);
    private int mColorRes = -1;
    private Handler mHandler = new Handler() {
        String ssid;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NETWORK_SCAN:
                    scanWifi();
                    break;
                case MESSAGE_NETWORK_CONFIG:
                    ssid = (String) msg.obj;
                    // NetWorkConnect(ssid);
                    onClickAction(ssid);
                    break;
                case MESSAGE_NETWORK_CONNECT:
                    ssid = (String) msg.obj;
                    NetWorkConnect(ssid, "");
                    break;
                case MESSAGE_NETWORK_CHANGE_STATE:
                    String ssid1 = (String) msg.obj;
                    NetWorkChangeStatus(ssid1);
                    break;
                case MESSAGE_DEVICE_CONFIG:
                    onDeviceConfig();
                    break;
                default:
                    break;
            }
        }
    };

    public WifiFragment() {
        this(R.color.white, "color");
    }

    public WifiFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.fragment_wifi);

        // ViewUtils.inject(this);
        print("onCreate");
        //if(BUG_T) {
        mApplication = (MainApplication) getActivity().getApplication();
        mApplication.addObserver(this);

        //addShortcut("open wifi");
        //}
    }

    private void addShortcut(String name) {
        Intent addShortcutIntent = new Intent(ACTION_ADD_SHORTCUT);

        // 不允许重复创建
        addShortcutIntent.putExtra("duplicate", false);// 经测试不是根据快捷方式的名字判断重复的
        // 应该是根据快链的Intent来判断是否重复的,即Intent.EXTRA_SHORTCUT_INTENT字段的value
        // 但是名称不同时，虽然有的手机系统会显示Toast提示重复，仍然会建立快链
        // 屏幕上没有空间时会提示
        // 注意：重复创建的行为MIUI和三星手机上不太一样，小米上似乎不能重复创建快捷方式

        // 名字
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);

        // 图标
        addShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getActivity().getApplicationContext(),
                        R.drawable.ic_launcher));

        // 设置关联程序
        Intent launcherIntent = new Intent();
        launcherIntent.setClass(getActivity().getApplicationContext(), SelectModeActivity.class);
        launcherIntent.addCategory(ACTION_CREATE_SHORTCUT);

        addShortcutIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, launcherIntent);

        // 发送广播
        getActivity().sendBroadcast(addShortcutIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
        //if(BUG_T) {

        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int color = getResources().getColor(mColorRes);

        View view = inflater.inflate(R.layout.fragment_wifi, null);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);
        //if(BUG_T) {
//
//		// test();
        wifiRegister(view);
        //}
        print("onCreateView");


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");
        //if(BUG_T) {
        wifiUnRegister();

        mApplication.deleteObserver(this);
        //}
    }

    private void wifiRegister(View view) {
        // WIFI状态接收器
        wifiReceiver = new WifiStateReceiver(this, view);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(wifiReceiver, filter);
    }

    private void wifiUnRegister() {
        getActivity().unregisterReceiver(wifiReceiver);
    }

    public void initNetworkUI() {

        mApplication.StartScan();
        mMyAdapter = new MyAdapter(getActivity(), mApplication.GetWifiList());
        allNetWork.setAdapter(mMyAdapter);
        allNetWork.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity()
                                .getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        allNetWork
                .setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        Toast.makeText(getActivity(), "End of List!",
                                Toast.LENGTH_SHORT).show();
                    }
                });

        ListView actualListView = allNetWork.getRefreshableView();

        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        /**
         * Add Sound Event Listener
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(
                getActivity());
        soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
        allNetWork.setOnPullEventListener(soundListener);

        actualListView.setAdapter(mMyAdapter);

        if (mApplication.IsWifiEnabled()) {
            start.setText("关闭网络");

        } else {
            start.setText("打开网络");
            curNetWorkSSID.setText(mApplication.GetSSID());
        }

    }


    @OnItemClick(R.id.allNetWork)
    public void onAllNetWorkClick(AdapterView<?> arg0, View arg1, int arg2,
                                  long arg3) {
        // 点击后在标题上显示点击了第几行 setTitle("你点击了第"+arg2+"行");

        ScanResult msr = (ScanResult) arg0.getItemAtPosition(arg2);

        WifiAccessPoint ap = new WifiAccessPoint(msr);


        if (ap.security == WifiAccessPoint.SECURITY_NONE) {
            Log.d(TAG, "MESSAGE_NETWORK_CONNECT " + ap.ssid);
            Message textMsg = mHandler.obtainMessage(MESSAGE_NETWORK_CONNECT);
            textMsg.obj = ap.ssid;
            mHandler.sendMessage(textMsg);
        } else if (ap.security == WifiAccessPoint.SECURITY_PSK) {
            Log.d(TAG, "MESSAGE_NETWORK_CONFIG " + ap.ssid);
            Message textMsg = mHandler.obtainMessage(MESSAGE_NETWORK_CONFIG);
            if (arg0.getItemAtPosition(arg2).toString().split(",")[0]
                    .split(":")[0].equals("SSID")) {
                textMsg.obj = arg0.getItemAtPosition(arg2).toString()
                        .split(",")[0].split(":")[1].substring(1);
                mHandler.sendMessage(textMsg);
            }
        }

    }

    @OnClick(R.id.curNetWorkSSID)
    public void oncurNetWorkSSIDClick(View v) {
        // TODO Auto-generated method stub
        Log.i(TAG, "" + curNetWorkSSID.getText());
        Message textMsg = mHandler.obtainMessage(MESSAGE_NETWORK_CHANGE_STATE);
        textMsg.obj = curNetWorkSSID.getText();
        mHandler.sendMessage(textMsg);
    }

    public void UpdateNet(String s1, String s2) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ItemTitle", s1);
        // Object o;
        // o = s2;
        if (s2.startsWith("[ESS]")) {
            // Log.i(TAG, "SSID: " + mScanResult.SSID + "  "
            // + mScanResult.capabilities);

        } else {
            ;// mWifiLock.setImageResource(R.drawable.lock);
            map.put("wifi_lock", R.drawable.lock);
            // Log.i(TAG, "SSID: " + mScanResult.SSID + "  "
            // + mScanResult.capabilities);
        }

    }

    @OnClick(R.id.scan)
    public void scan_onClick(View v) {
        mMyAdapter.clear();
        mMyAdapter.notifyDataSetChanged();
        // mSimpleAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessage(MESSAGE_NETWORK_SCAN);
    }

    @OnClick(R.id.start)
    public void start_onClick(View v) {
        if (mApplication.IsWifiEnabled()) {
            start.setText("打开网络");
        } else {
            start.setText("关闭网络");
        }
        mApplication.ChangeWifiState();
        Toast.makeText(getActivity(), "当前wifi状态为：" + mApplication.CheckState(),
                Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.config)
    public void config_onClick(View v) {
        mHandler.sendEmptyMessage(MESSAGE_DEVICE_CONFIG);
    }

    public void scanWifi() {

        // 开始扫描网络
        mApplication.StartScan();

    }

    public void updateWifiList() {
        list = mApplication.GetWifiList();

        if (list != null) {

            mMyAdapter.update(list);
            mMyAdapter.notifyDataSetChanged();

        } else {
            Log.i(TAG, "wifi list null");
        }
    }

//	private class MyListener implements OnClickListener {
//
//		@SuppressLint("ShowToast")
//		@Override
//		public void onClick(View v) {
//			// TODO Auto-generated method stub
//			switch (v.getId()) {
//			case R.id.scan:// 扫描网络
//				// listItem.clear();
//				mMyAdapter.clear();
//				mMyAdapter.notifyDataSetChanged();
//				// mSimpleAdapter.notifyDataSetChanged();
//				mHandler.sendEmptyMessage(MESSAGE_NETWORK_SCAN);
//				break;
//			case R.id.start:// 打开Wifi
//				if (mApplication.IsWifiEnabled()) {
//					start.setText("打开网络");
//				} else {
//					start.setText("关闭网络");
//				}
//				mApplication.ChangeWifiState();
//				Toast.makeText(getActivity(),
//						"当前wifi状态为：" + mApplication.CheckState(), 1).show();
//				break;
//			case R.id.config:
//				mHandler.sendEmptyMessage(MESSAGE_DEVICE_CONFIG);
//				break;
//			default:
//				break;
//			}
//		}
//	}

    private void onClickAction(String extraStr) {
//        Intent intent = new Intent();
//        intent.putExtra("ssid", extraStr);
//        intent.setClass(getActivity(), WifiConfigDialogFragment.class);
//        getActivity().startActivity(intent);
//        startActivityForResult(intent, 100);

        WifiConfigDialogFragment wifiConfigDialogFragment = new WifiConfigDialogFragment();
        Bundle args = new Bundle();
        print("ssid:" + extraStr);
        args.putString("ssid", extraStr);
        wifiConfigDialogFragment.setArguments(args);
        wifiConfigDialogFragment.show(getActivity().getFragmentManager(), "WifiConfigDialogFragment");
    }


    private void onDeviceConfig() {
        Intent intent = new Intent();
        // intent.putExtra("ssid", extraStr);
        intent.setClass(getActivity(), WifiDeviceList.class);
        // intent.setClass(SelectModeActivity.this, DeviceConfig.class);
        getActivity().startActivity(intent);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 可以根据多个请求代码来作相应的操作
        if (requestCode == 100) {
            if (20 == resultCode) {
                String ssid = data.getExtras().getString("ssid");
                String psk = data.getExtras().getString("psk");

                NetWorkConnect(ssid, psk);
                // TextView_result.setText("书籍名称:"+bookname+"书籍价钱"+booksale+"元");
            } else if (30 == resultCode) {
                String ssid = data.getExtras().getString("ssid");
                Log.i(TAG, "forget SSID = " + ssid);
                ForgetNetwork(ssid);

            } else if (40 == resultCode) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ForgetNetwork(String ssid) {
        listWCF = mApplication.GetConfiguration();
        if (listWCF != null) {
            for (int i = 0; i < listWCF.size(); i++) {
                mWifiConfiguration = listWCF.get(i);
                if (mWifiConfiguration.SSID
                        .equalsIgnoreCase("\"" + ssid + "\"")) {
                    mApplication
                            .RemoveConnectionWifi(mWifiConfiguration.networkId);
                    // mWifiAdmin.
                    Log.i(TAG, "removeConnectionWifi" + " i: " + i + " id:"
                            + mWifiConfiguration.networkId);
                }
            }
        } else {
            Log.i(TAG, " getConfiguration  null");
        }
    }

    public void NetWorkChangeStatus(String ssid) {
        // conState = 0;
        // listWCF = mWifiAdmin.getConfiguration();
        Log.i(TAG, "NetWorkChangeStatus: " + conState);
        if (conState == 0) {
            conState = 1;
            mApplication.ReAssociate();
        } else {
            conState = 0;
            mApplication.DisConnect();
        }

    }

    public void NetWorkConnect(String ssid, String psk) {
        // Log.i(TAG, ssid);

        listWCF = mApplication.GetConfiguration();
        // listItem.clear();
        if (listWCF != null) {
            // listItem.clear();
            if (psk.equals("********")) {//
                // Log.i(TAG,"DFASDGFSDA");

                for (int i = 0; i < listWCF.size(); i++) {
                    mWifiConfiguration = listWCF.get(i);

                    if (mWifiConfiguration.SSID.equalsIgnoreCase("\"" + ssid
                            + "\"")) {

                        mApplication.ConnetionConfiguration(i);
                    }

                }
            } else if (psk == null || psk == "") {
                mWifiConfiguration = mApplication.CreateWifiInfo(ssid, psk,
                        WifiAccessPoint.SECURITY_NONE);
                mApplication.AddNetWork(mWifiConfiguration);
                Log.i(TAG, "NO PASSWD CONNECT ");
            } else {
                mWifiConfiguration = mApplication.CreateWifiInfo(ssid, psk,
                        WifiAccessPoint.SECURITY_PSK);
                mApplication.AddNetWork(mWifiConfiguration);
                // mWifiAdmin.reassociate();
                Log.i(TAG, "PASSWD CONNECT ");
            }
            // Log.i(TAG, " listWCF.size() = " + listWCF.size());

        } else {
            Log.i(TAG, "wifi configuration null");
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        // Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String) arg;
        if (qualifier.equals(MainApplication.DEVICE_WIFI_SCAN_RESULTS)) {
            updateWifiList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (BUG_T) {
        mApplication.startWifi();
        initNetworkUI();
        //}

        print("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        //if (BUG_T) {
        mApplication.stopWifi();
        //}
        print("onPause");
    }


    public class MyAdapter extends BaseAdapter {

        LayoutInflater inflater;
        List<ScanResult> list;


        @ViewInject(R.id.wifi_strength)
        ImageView signalStrenth;

        @ViewInject(android.R.id.text1)
        TextView ssid;
        @ViewInject(android.R.id.text2)
        TextView others;

        public MyAdapter(Context context, List<ScanResult> list) {
            // TODO Auto-generated constructor stub
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        public void update(List<ScanResult> list) {
            clear();
            this.list = list;
        }

        public void clear() {
            list.clear();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = null;
            WifiAccessPoint ap = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);

            ViewUtils.inject(this, view);

            ScanResult scanResult = list.get(position);

//			TextView ssid = (TextView) view.findViewById(android.R.id.text1);
            ssid.setText(scanResult.SSID);
//			TextView other = (TextView) view.findViewById(android.R.id.text2);
            others.setText(scanResult.BSSID);

            ap = new WifiAccessPoint(scanResult);

            if (ap.security == WifiAccessPoint.SECURITY_NONE) {
                if (Math.abs(scanResult.level) > 100) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_1));
                } else if (Math.abs(scanResult.level) > 80) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_1));
                } else if (Math.abs(scanResult.level) > 70) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_2));
                } else if (Math.abs(scanResult.level) > 60) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_3));
                } else if (Math.abs(scanResult.level) > 50) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_3));
                } else {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_signal_4));
                }
            } else {
                if (Math.abs(scanResult.level) > 100) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_1));
                } else if (Math.abs(scanResult.level) > 80) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_1));
                } else if (Math.abs(scanResult.level) > 70) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_2));
                } else if (Math.abs(scanResult.level) > 60) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_3));
                } else if (Math.abs(scanResult.level) > 50) {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_3));
                } else {
                    signalStrenth.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_wifi_lock_signal_4));
                }
            }

            // 判断信号强度，显示对应的指示图标

            return view;
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {

                // mSimpleAdapter.notifyDataSetChanged();
                // mHandler.sendEmptyMessage(MESSAGE_NETWORK_SCAN);
                scanWifi();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            mMyAdapter.clear();
            mMyAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String[] result) {
            // mListItems.addFirst("Added after refresh...");
            // mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            allNetWork.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

}