package zj.zfenlly.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;

public class MainApplication extends Application implements Observable {

    private List<Observer> mObservers = new ArrayList<Observer>();
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    //private boolean server_pause = false;
    private boolean server_display_run = true;
    private boolean server_simulation = false;

    private WifiAdmin mWifiAdmin;

    public String SSIDNAME;

    public int port = 9111;

    public static enum APPUpdateParam {
        AAA
    }
    static {
        System.loadLibrary("JniCamera");   //defaultConfig.ndk.moduleName
    }

    public static final String DEVICE_CONFIG_FIND = "DEVICE_CONFIG_FIND";
    public static final String DEVICE_CONFIG_GET = "DEVICE_CONFIG_GET";
    public static final String DEVICE_WIFI_SCAN_RESULTS = "DEVICE_WIFI_SCAN_RESULTS";

    public static final String SERVER_PAUSE = "SERVER_PAUSE";

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    // private ComponentName mAPService;

    public synchronized void addObserver(Observer obs) {
        print("addObserver(" + obs + ")");
        if (mObservers.indexOf(obs) < 0) {
            mObservers.add(obs);
        }
    }

    public void startWifi() {
        mWifiAdmin = new WifiAdmin(this);
//        Intent intent = new Intent(this, ZfenllyService.class);
//        // mAPService =
//        startService(intent);
    }

    public void stopWifi() {
        mWifiAdmin = null;
        // mAPService = null;
//        Intent intent = new Intent(this, ZfenllyService.class);
//        stopService(intent);
    }

    @Override
    public void onCreate() {


        super.onCreate();


        //appRegister();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        print("onTerminate");
    }


    private void appRegister() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        AppReceiver myReceiver = new AppReceiver();
        print("register APP receiver");
        registerReceiver(myReceiver, intentFilter);
    }

    public String GetSSID() {
        if (mWifiAdmin != null)
            return mWifiAdmin.getSSID();
        else
            return null;
    }

    public void StartScan() {
        if (mWifiAdmin != null)
            mWifiAdmin.startScan();
    }

    public void ScanResults() {
        notifyObservers(DEVICE_WIFI_SCAN_RESULTS);
    }

    private static final int HANDLE_SEND_MULTICAST_FIND = 0;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLE_SEND_MULTICAST_FIND:
                    // sendMulticast();

                    break;
                default:
                    break;
            }
        }
    };

    public String getLocalHostIp() {
        if (mWifiAdmin == null) return null;
        int ipAddress = mWifiAdmin.getIpAddress();
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public void SendFind() {
        // ִ����Ϻ��handler����һ������Ϣ
        Runnable networkTask = new Runnable() {
            @Override
            public void run() {
                String localip = getLocalHostIp();
                Log.i(TAG, localip + "");
                // sendMulticast(localip);
                sendUDP();
                handler.sendMessage(handler.obtainMessage(
                        HANDLE_SEND_MULTICAST_FIND, null));
            }
        };
        new Thread(networkTask).start();
    }

    public class Client implements Runnable {
        private final String SERVERIP = "10.10.10.254";
        private final int SERVERPORT = 9112;

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                // Log.i(TAG, "Client: Start connecting\n");
                @SuppressWarnings("resource")
                DatagramSocket socket = new DatagramSocket();
                // byte[] buf;

                String localip = getLocalHostIp();
                String x = "GETUNIQINFO:" + localip + ";"
                        + String.valueOf(port) + ";" + SERVERIP;
                Log.i(TAG, "==>  " + x);
                byte[] buf = x.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                        serverAddr, SERVERPORT);
                // Log.i(TAG, "Client: Sending ��" + new String(buf) + "��\n");
                socket.send(packet);
                // Log.i(TAG, "Client: Message sent\n");
                // Log.i(TAG, "Client: Succeed!\n");
            } catch (Exception e) {
                ;// Log.i(TAG, "Client: Error!\n");
                e.printStackTrace();
            }
        }
    }

    public void sendUDP() {
        new Thread(new Client()).start();

    }

    public void sendMulticast(String ip) {

        // Log.i(TAG, "AllowMulticast");
        AllowMulticast();
        String finds = "FIND:" + ip + ";" + port;
        byte[] sendData = finds.getBytes();

        MulticastSocket multicastSocket = null;
        // Log.i(TAG, "MulticastSocket");
        try {
            multicastSocket = new MulticastSocket(port);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Log.i(TAG, "setTimeToLive");
        try {
            multicastSocket.setTimeToLive(20);// .setLoopbackMode(true);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InetAddress group = null;
        // Log.i(TAG, "getByName");
        try {
            group = InetAddress.getByName("226.0.0.100");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Log.i(TAG, "joinGroup");
        try {
            multicastSocket.joinGroup(group);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Log.i(TAG, "DatagramPacket");
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length,
                group, 1188);
        // Log.i(TAG, "send");
        try {
            multicastSocket.send(packet);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // Log.i(TAG, "ReleaseMulticast");
        ReleaseMulticast();
    }

    public List<ScanResult> GetWifiList() {
        if (mWifiAdmin != null) return mWifiAdmin.getWifiList();
        else return null;
    }

    public void OpenWifi() {
        if (mWifiAdmin != null)
            mWifiAdmin.openWifi();
    }

    public void CloseWifi() {
        if (mWifiAdmin != null)
            mWifiAdmin.closeWifi();
    }

    public int CheckState() {
        if (mWifiAdmin != null) return mWifiAdmin.getWifiState();
        else
            return 0;
    }

    public void AllowMulticast() {
        if (mWifiAdmin != null)
            mWifiAdmin.allowMulticast();
    }

    public void ReleaseMulticast() {
        if (mWifiAdmin != null)
            mWifiAdmin.releaseMulticast();
    }

    public List<WifiConfiguration> GetConfiguration() {
        if (mWifiAdmin != null)
            return mWifiAdmin.getConfiguration();
        else
            return null;
    }

    public int GetWifiLevel() {
        if (mWifiAdmin != null)
            return mWifiAdmin.getWifiLevel();
        else
            return 0;
    }

    public void RemoveConnectionWifi(int netId) {
        if (mWifiAdmin != null)
            mWifiAdmin.removeConnectionWifi(netId);
    }

    public void ReAssociate() {
        if (mWifiAdmin != null)
            mWifiAdmin.reassociate();
    }

    public void DisConnect() {
        if (mWifiAdmin != null)
            mWifiAdmin.disconnect();
    }

    public void ConnetionConfiguration(int index) {
        if (mWifiAdmin != null)
            mWifiAdmin.connetionConfiguration(index);
    }

    public void AddNetWork(WifiConfiguration configuration) {
        if (mWifiAdmin != null)
            mWifiAdmin.addNetWork(configuration);
    }

    public void ChangeWifiState() {
        if (mWifiAdmin != null)
            mWifiAdmin.changeWifiState();
    }

    public boolean IsWifiEnabled() {
        if (mWifiAdmin != null)
            return mWifiAdmin.isWifiEnabled();
        else return false;
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password,
                                            int Type) {
        if (mWifiAdmin != null)
            return mWifiAdmin.CreateWifiInfo(SSID, Password, Type);
        else
            return null;
    }

    public void notify_find() {
        notifyObservers(DEVICE_CONFIG_FIND);
    }

    private List<String> mfinds = new ArrayList<String>();

    public synchronized List<String> getFinds() {
        return mfinds;
    }

    public synchronized void addFind(String find) {
        mfinds.add(find);
    }

    public synchronized void clearFind() {
        mfinds.clear();
    }

    public synchronized List<String> getFoundsList() {
        // Log.i(TAG, "getFoundChannels()");
        if (mfinds.size() == 0) {
            return null;
        }
        List<String> clone = new ArrayList<String>(mfinds.size());
        for (String string : mfinds) {
            // Log.i(TAG, "getFoundChannels(): added " + string);
            clone.add(new String(string));
        }
        return clone;
    }


    /**
     * When an observer wants to unregister to stop receiving change
     * notifications, it calls here.
     */
    public synchronized void deleteObserver(Observer obs) {
        print("deleteObserver(" + obs + ")");
        mObservers.remove(obs);
    }

    private synchronized void notifyObservers(Object arg) {
        // Log.i(TAG, "notifyObservers(" + arg + ")");
        //if(mObservers)
        for (Observer obs : mObservers) {
            // Log.i(TAG, "notify observer = " + obs);
            obs.update(this, arg);
        }
    }

    public synchronized void setServerDisplayRun(boolean Running) {

        //this.server_pause = !Running;
        this.server_display_run = Running;
        print("setServerDisplayRun:" + (this.server_display_run ? "true" : "false"));
    }

    public synchronized void setServerSimulation(boolean Simulaiton) {

        this.server_simulation = Simulaiton;
        print("setServerSimulation:" + (this.server_simulation ? "true" : "false"));
    }

    public synchronized boolean isServerDisplayRun() {
//        print("is server_display_run:" + (this.server_display_run ? "true" : "false"));
        return this.server_display_run;
    }


    public synchronized boolean isServerSimulation() {
//        print("is server_simulation:" + (this.server_simulation ? "true" : "false"));
        return this.server_simulation;
    }


}
