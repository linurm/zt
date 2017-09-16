package zj.zfenlly.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.MNClick.MnClickFragment;
import zj.zfenlly.arc.ArcFragment;
import zj.zfenlly.bluetooth.BluetoothFragment;
import zj.zfenlly.caculator.CalculationFragment;
import zj.zfenlly.camera.CameraFragment;
import zj.zfenlly.camera.CameraJni;
import zj.zfenlly.coloradjust.ColorAdjustFragment;
import zj.zfenlly.http.HttpFragment;
import zj.zfenlly.mobeta.DragsortFragment;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.record.RecordFragment;
import zj.zfenlly.stock.StockFragment;
import zj.zfenlly.stock2.Stock2Fragment;
import zj.zfenlly.usb.UsbFragment;
import zj.zfenlly.wifi.WifiAdmin;
import zj.zfenlly.wifi.WifiFragment;
import zj.zfenlly.wifiap.WifiApFragment;
import zj.zfenlly.wifidevice.WifiDeviceFragment;

public class MainApplication extends Application implements Observable {

    public static final String DEVICE_CONFIG_FIND = "DEVICE_CONFIG_FIND";
    public static final String DEVICE_CONFIG_GET = "DEVICE_CONFIG_GET";
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    public static final String DEVICE_WIFI_SCAN_RESULTS = "DEVICE_WIFI_SCAN_RESULTS";
    public static final String SERVER_PAUSE = "SERVER_PAUSE";
    private static final int HANDLE_SEND_MULTICAST_FIND = 0;
    static private final String STAG = "zj Application";
    private static String soName = "libjiagu";

    static {
        System.loadLibrary("JniCamera");   //defaultConfig.ndk.moduleName
    }

    private final String TAG = this.getClass().getName();
    public String SSIDNAME;
    public int port = 9111;

    public List<BaseFragment> getFragments() {
        return fragments;
    }

    List<BaseFragment> fragments = new ArrayList<BaseFragment>();
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
    private List<Observer> mObservers = new ArrayList<Observer>();
    //private boolean server_pause = false;
    private boolean server_display_run = true;
    private boolean server_simulation = false;
    private WifiAdmin mWifiAdmin;
    // private ComponentName mAPService;
    private List<String> mfinds = new ArrayList<String>();

    public static boolean isSameFile(BufferedInputStream paramBufferedInputStream1, BufferedInputStream paramBufferedInputStream2) {
        try {
            int j = paramBufferedInputStream1.available();
            int i = paramBufferedInputStream2.available();
            byte[] arrayOfByte1 = null;
            byte[] arrayOfByte2 = null;
            if (j == i) {
                arrayOfByte1 = new byte[j];
                arrayOfByte2 = new byte[i];
                paramBufferedInputStream1.read(arrayOfByte1);
                paramBufferedInputStream2.read(arrayOfByte2);
                i = 0;
            }
            while (i < j) {
                int k = arrayOfByte1[i];
                int m = arrayOfByte2[i];
                if (k != m) {
                    return false;
                }
                i += 1;
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copy(Context paramContext, String paramString1, String paramString2, String paramString3) {
        String paramString4 = paramString2 + "/" + paramString3;
        InputStream strs;
        InputStream isstr;
        File paramS = new File(paramString2);
        if (!paramS.exists()) {
            paramS.mkdir();
        }
        try {
            Object localObject = new File(paramString4);
            if (((File) localObject).exists()) {
                strs = paramContext.getResources().getAssets().open(paramString1);
                localObject = new FileInputStream((File) localObject);
                BufferedInputStream localBufferedInputStream1 = new BufferedInputStream(strs);
                BufferedInputStream localBufferedInputStream2 = new BufferedInputStream((InputStream) localObject);
                if (isSameFile(localBufferedInputStream1, localBufferedInputStream2)) {
                    strs.close();
                    ((InputStream) localObject).close();
                    localBufferedInputStream1.close();
                    localBufferedInputStream2.close();
                    Log.e(STAG, "is the Same file");
                    return true;
                }
                strs.close();
            }
            isstr = paramContext.getResources().getAssets().open(paramString1);
            OutputStream opstr = new FileOutputStream(paramString4);
            byte[] abs = new byte['ᰀ'];
            for (; ; ) {
                int i = isstr.read(abs);
                if (i <= 0) {
                    break;
                }
                opstr.write(abs, 0, i);
            }
            Log.e(STAG, "copy so ");
            opstr.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //isstr.close();
        return true;
    }

    public void addFragment() {
        fragments.add(new ColorFragment());
        fragments.add(new CameraFragment());
        fragments.add(new BluetoothFragment());
        fragments.add(new CalculationFragment());
        fragments.add(new UsbFragment());
        fragments.add(new WifiApFragment());
        fragments.add(new HttpFragment());
        fragments.add(new ColorAdjustFragment());
        fragments.add(new WifiFragment());
        fragments.add(new RecordFragment());
        fragments.add(new StockFragment());
        fragments.add(new DragsortFragment());
        fragments.add(new ArcFragment());
        fragments.add(new WifiDeviceFragment());
        fragments.add(new Stock2Fragment());
        fragments.add(new MnClickFragment());
        print("++++++++++++++++++++++add fragment+++++++++++++++++++");
    }

    private void print(String msg) {
        Log.e(TAG, msg);
    }

    public synchronized void addObserver(Observer obs) {
        print("addObserver(" + obs + ")");
        if (mObservers.indexOf(obs) < 0) {
            mObservers.add(obs);
        }
    }

    protected void attachBaseContext(Context paramContext) {
        super.attachBaseContext(paramContext);
        print("attachBaseContext");
        //String str = paramContext.getFilesDir().getAbsolutePath();
        //Context context = paramContext;

        //copy(context, soName + ".so", str, soName + "_copy.so");
        //test2(str + "/" + soName + "_copy.so");
    }

    public void test2(String a) {
        CameraJni camerajni = new CameraJni();
        print("sssssssss:" + camerajni.getCLanguageString(a));
    }

    public void startWifi() {
//        mWifiAdmin = new WifiAdmin(this);
//        Intent intent = new Intent(this, ZfenllyService.class);
//        // mAPService =
//        startService(intent);
    }

    public void stopWifi() {
//        mWifiAdmin = null;
        // mAPService = null;
//        Intent intent = new Intent(this, ZfenllyService.class);
//        stopService(intent);
    }

    @Override
    public void onCreate() {


        super.onCreate();
        mWifiAdmin = new WifiAdmin(this);
        print("onCreate");
        addFragment();
        //appRegister();

    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        print("onTerminate");
        fragments.clear();
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

    public String getLocalHostIp() {
        if (mWifiAdmin == null) return null;
        int ipAddress = mWifiAdmin.getIpAddress();
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    public void SendFind() {
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

    public void sendUDP() {
        new Thread(new Client()).start();
    }

    public void sendMulticast(String ip) {

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

    public synchronized boolean isServerDisplayRun() {
//        print("is server_display_run:" + (this.server_display_run ? "true" : "false"));
        return this.server_display_run;
    }

    public synchronized void setServerDisplayRun(boolean Running) {

        //this.server_pause = !Running;
        this.server_display_run = Running;
        print("setServerDisplayRun:" + (this.server_display_run ? "true" : "false"));
    }

    public synchronized boolean isServerSimulation() {
//        print("is server_simulation:" + (this.server_simulation ? "true" : "false"));
        return this.server_simulation;
    }

    public synchronized void setServerSimulation(boolean Simulaiton) {

        this.server_simulation = Simulaiton;
        print("setServerSimulation:" + (this.server_simulation ? "true" : "false"));
    }

    public static enum APPUpdateParam {
        AAA
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


}
