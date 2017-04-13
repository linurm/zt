package zj.zfenlly.wifidevice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.other.Name;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class WifiDeviceFragment extends Fragment implements Name, Observer {
    private static int BROADCAST_PORT = 8251;
    private static String BROADCAST_IP = "239.0.5.100";
    private final String TAG = this.getClass().getName();
    private final int MS_TIMES_POLL = 6000;
    public String mName;
    public MainApplication mStockApplication;
    @ViewInject(R.id.textView1)
    public TextView iptext;
    Handler handler = new Handler();
    Runnable poll_runnable = new Runnable() {
        @Override
        public void run() {
            if (isWiFiConnected(getActivity())) {
                new Thread(networkTask).start();
            }
        }
    };
    private int mColorRes = -1;
    private WifiManager.MulticastLock multicastLock;
    private MulticastSocket multicastSocket = null;
    private InetAddress serverAddress = null;
    private Handler RecMsgHabndler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG, "HandleMessage-->handleMessage");
            Log.e(TAG, msg.getData().getString("RecMsg"));
            iptext.append(msg.getData().getString("RecMsg"));
        }
    };
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            mStockApplication.AllowMulticast();
            startMultiCastServer();
            mStockApplication.ReleaseMulticast();
            handler.postDelayed(poll_runnable, MS_TIMES_POLL);
        }
    };

    public WifiDeviceFragment() {
        this(R.color.white, "wd");
    }

    public WifiDeviceFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
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
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        // Log.i(TAG, "update(" + arg + ")");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStockApplication = (MainApplication) getActivity().getApplication();
        mStockApplication.addObserver(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_wifi_client, container, false);
        view.setBackgroundColor(color);


        ViewUtils.inject(this, view);
        handler.postDelayed(poll_runnable, MS_TIMES_POLL);
//        new Thread(networkTask).start();
        return view;
    }

    public void startMultiCastServer() {
        try {
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            serverAddress = InetAddress.getByName(BROADCAST_IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            multicastSocket.setLoopbackMode(true);
            multicastSocket.joinGroup(serverAddress);
            multicastSocket.setTimeToLive(5);
//            multicastSocket.setBroadcast(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Log.e("tag", "start=====================");
            byte[] data = new byte[1024];
//
//            String finds = "send:";
//            byte[] sendData = finds.getBytes();
//            DatagramPacket packet = new DatagramPacket(sendData, sendData.length, serverAddress, BROADCAST_PORT);
//            multicastSocket.send(packet);
//            multicastSocket.setInterface(serverAddress);

            Log.e("TAG", "" + multicastSocket.getNetworkInterface().toString());
//
            DatagramPacket datagramPackage = new DatagramPacket(data, data.length);
            multicastSocket.receive(datagramPackage);
            String serverIP = new String(datagramPackage.getData(), datagramPackage.getOffset(), datagramPackage.getLength());
            //Thread.sleep(3);
            DoOnReceiveMsg("" + serverIP);
            if (serverIP.startsWith("FIND:")) {
                getIPPort(serverIP.substring(5));
            }
            Log.e("tag", "receive ip：" + serverIP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        multicastSocket.close();
    }

    private void getIPPort(String ipport) {
        String[] sb = ipport.split(";");
        String remote_ip = sb[0];
        String remote_port = sb[1];
        Log.e("TAG", "" + remote_ip + ":" + remote_port);
    }

    public void DoOnReceiveMsg(String ARecMsg) {
        Message msg = new Message();
        Bundle b = new Bundle();// 存放数据
        b.putString("RecMsg", ARecMsg);
        msg.setData(b);
        this.RecMsgHabndler.sendMessage(msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");
        mStockApplication.ReleaseMulticast();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }
}
