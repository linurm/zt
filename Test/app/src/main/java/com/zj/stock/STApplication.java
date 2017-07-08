package com.zj.stock;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class STApplication extends Application implements Observable {

    public static final String ST_GET_ONE = "ST_GET_ONE";
    public static final String ST_GET_FIRST = "ST_GET_FIRST";
    public static final String ST_GET_MIDDLE = "ST_GET_MIDDLE";
    public static final String ST_GET_LAST = "ST_GET_LAST";
    public static final String ST_SERVICE_START = "ST_SERVICE_START";
    public static final String ST_SERVICE_STOP = "ST_SERVICE_STOP";
    public static final String ST_SERVICE_PAUSE = "ST_SERVICE_PAUSE";
    public static final String ST_SERVICE_BUY = "ST_SERVICE_BUY";

    ;
    public static final String ST_SERVICE_SELL = "ST_SERVICE_SELL";
    public static final String ST_USER_DATA = "ST_USER_DATA";
    private static final String TAG = "STApplication";
    // private WifiAdmin mWifiAdmin;
    public UserData userData;
    public String SSIDNAME;
    public ISTServiceAIDLInterface mService = null;
    public int port = 9111;
    private List<Observer> mObservers = new ArrayList<Observer>();
    private boolean isConn = false;
    private List<StockData> mSTData = new ArrayList<StockData>();

    // private ComponentName mAPService;
    private LinkedList mKDJlist = new LinkedList();
    // private List<KDJData> mKDJData = new ArrayList<KDJData>();
    private LinkedList mMACDlist = new LinkedList();
    private boolean display_done = true;
    private float maxVolume = 0;
    private float highValue = 0, lowValue = 0;
    private float KDJhigh = 0, KDJlow = 0;
    private int DisNum = 0;
    private ServiceConnection mcoon = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mService = null;
            isConn = false;
            Log.e(TAG, "service disconnected!!!");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (ISTServiceAIDLInterface) service;
            isConn = true;
            try {
                mService.setDisNum(DisNum);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e(TAG, "service connected!!!");

        }
    };

    public synchronized boolean IsDisplayDone() {
        return display_done;
    }

    public synchronized void DisplayDone() {
        display_done = true;
    }

    public synchronized void ReadyForDisplay() {
        display_done = false;

    }

    public synchronized void SetValue(float maxVulome, float highValue,
                                      float lowValue) {
        this.maxVolume = maxVulome;
        this.highValue = highValue;
        this.lowValue = lowValue;
    }

    public synchronized void SetKDJValue(float highValue, float lowValue) {
        this.KDJhigh = highValue;
        this.KDJlow = lowValue;
    }

    public synchronized float GetKDJH() {
        return this.KDJhigh;
    }

    public synchronized float GetKDJL() {
        return this.KDJlow;
    }

    public synchronized float GetMaxVolume() {
        return this.maxVolume;
    }

    public synchronized float GetHighValue() {
        return this.highValue;
    }

    public synchronized float GetLowValue() {
        return this.lowValue;
    }

    public void setUserData(UserData mUUD) {
        userData = mUUD;
        UserDataPereference.setUserData(this, userData);
        notifyObservers(ST_USER_DATA);
    }

    public synchronized List<StockData> getFinds() {
        return mSTData;
    }

    public synchronized void addFind(StockData find) {
        mSTData.add(find);
    }

    public synchronized void clearFinds() {
        mSTData.clear();
    }

    public synchronized void clearMACD() {
        mMACDlist.clear();
    }

    @SuppressWarnings("rawtypes")
    public synchronized LinkedList getMACDLinkedList() {
        return mMACDlist;
    }

    @SuppressWarnings("unchecked")
    public synchronized void addMACD(MACDData find) {
        mMACDlist.add(find);
    }

    public synchronized void removeFirstMACD() {
        mMACDlist.removeFirst();
        // m
    }

    public synchronized KDJData getLastMACD() {
        if (mMACDlist.size() == 1) {
            return (KDJData) mMACDlist.getFirst();
        } else if (mMACDlist.size() != 0) {
            return (KDJData) mMACDlist.getLast();
        } else {
            return null;
        }
        // m
    }

    public synchronized void clearKDJ() {
        mKDJlist.clear();
    }

    @SuppressWarnings("rawtypes")
    public synchronized LinkedList getKDJLinkedList() {
        return mKDJlist;
    }

    @SuppressWarnings("unchecked")
    public synchronized void addKDJ(KDJData find) {
        mKDJlist.add(find);
    }

    public synchronized void removeFirstKDJ() {
        mKDJlist.removeFirst();
        // m
    }

    public synchronized KDJData getLastKDJ() {
        if (mKDJlist.size() == 1) {
            return (KDJData) mKDJlist.getFirst();
        } else if (mKDJlist.size() != 0) {
            return (KDJData) mKDJlist.getLast();
        } else {
            return null;
        }
        // m
    }

    public synchronized void addObserver(Observer obs) {
        Log.i(TAG, "addObserver(" + obs + ")");
        if (mObservers.indexOf(obs) < 0) {
            mObservers.add(obs);
        }
    }

    public boolean isServerRun() {
        if (isConn) {
            try {
                return mService.IsServerRun();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean haveStock() {
        if (isConn) {
            try {
                return mService.HaveStock();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    public void setDisplayNum(int n) {
        DisNum = n;
        if (isConn) {
            try {
                mService.setDisNum(n);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isServerPause() {
        if (isConn) {
            try {
                return mService.IsServerPause();
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;

    }

    @Override
    public void onCreate() {
        // mWifiAdmin = new WifiAdmin(this);
        super.onCreate();
        userData = UserDataPereference.getUserData(this);
        Intent intent = new Intent(this, STService.class);
        bindService(intent, mcoon, Context.BIND_AUTO_CREATE);// zz
        // mAPService =
        //startService(intent);
        notifyObservers(ST_SERVICE_START);
    }

    @Override

    public void onTerminate() {

        super.onTerminate();


        // mWifiAdmin = null;
        // mAPService = null;
        // Intent intent = new Intent(this, STService.class);
        if (isConn == true) {
            unbindService(mcoon);
            isConn = false;
        }
        // stopService(intent);
    }

    public void start() {
        // mWifiAdmin = new WifiAdmin(this);
        // Intent intent = new Intent(this, APService.class);
        // mAPService =
        // startService(intent);
        notifyObservers(ST_SERVICE_START);
    }

    public void stop() {
        // mWifiAdmin = new WifiAdmin(this);
        // Intent intent = new Intent(this, APService.class);
        // mAPService =
        // startService(intent);
        notifyObservers(ST_SERVICE_STOP);
    }

    public void pause() {
        // mWifiAdmin = new WifiAdmin(this);
        // Intent intent = new Intent(this, APService.class);
        // mAPService =
        // startService(intent);
        notifyObservers(ST_SERVICE_PAUSE);
    }

    public void buy() {
        notifyObservers(ST_SERVICE_BUY);
    }

    public void sell() {
        notifyObservers(ST_SERVICE_SELL);
    }

    /**
     * When an observer wants to unregister to stop receiving change
     * notifications, it calls here.
     */
    public synchronized void deleteObserver(Observer obs) {
        Log.i(TAG, "deleteObserver(" + obs + ")");
        mObservers.remove(obs);
    }

    public void display() {
        ReadyForDisplay();
        notifyObservers(ST_GET_ONE);
    }

    private void notifyObservers(Object arg) {
        // Log.i(TAG, "notifyObservers(" + arg + ")");
        for (Observer obs : mObservers) {
            // Log.i(TAG, "notify observer = " + obs);
            obs.update(this, arg);
        }
    }

    public static enum APPUpdateParam {
        AAA
    }
}
