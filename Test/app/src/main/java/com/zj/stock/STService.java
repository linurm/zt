package com.zj.stock;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zj.test.EnumState;

import java.util.List;

public class STService extends Service implements Observer {

    private final String TAG = "STService";
    private final boolean DEBUG = true;
    private final int kdj_n = 9;
    public STApplication mSTApplication;
    private int DIS_NUM = 0;
    private volatile boolean isRunning = false;
    private volatile boolean isPause = false;
    private volatile boolean haveStock = false;
    private Thread mGetDataThread = null;
    private EnumState UserState = EnumState.IDLE;
    private UserData mUD;
    private float kdj_h = 0, kdj_l = 100;
    private float kdj_9h = 0, kdj_9l = 100;
    private MyServiceBinder mBinder = new MyServiceBinder();
    private Stock2Client mClient = null;
    // private Thread mGetDataThread = new Thread() {
    private Runnable getdataable = new Runnable() {
        // Socket client;
        private StockData recent_sd = null;

        private void sellStock(StockData mSD, int open_close) {
            if (mUD.stock_num != 0) {
                haveStock = false;
                mUD.stock_value = mUD.balance + mUD.stock_num
                        * (open_close == 1 ? mSD.open : mSD.close) * 100;
                mUD.stock_market = 0;
                mUD.stock_num = 0;
                mUD.balance = mUD.stock_value;
            }
        }

        private void buyStock(StockData mSD) {
            mUD.stock_num = (int) (mUD.stock_value / (mSD.open) / 100);
            mUD.stock_market = (mSD.open) * mUD.stock_num * 100;
            mUD.balance = mUD.stock_value - mUD.stock_market;
            haveStock = true;
        }

        private void updateStock(StockData mSD) {
            if (mUD.stock_num != 0) {
                mUD.stock_value = mUD.balance + mUD.stock_num * (mSD.open)
                        * 100;
                mUD.stock_market = (mSD.open) * mUD.stock_num * 100;
            }
        }

        @Override
        public void run() {
            // rs.Poll(1);
            // Log.i(TAG, "mTcpPollThread thread run");
            GetDataFromNet stockUtil = new GetDataFromNet();

            List<StockData> sd = null;
            float k, kp, rsv, pre_k = 50, d, j, pre_d = 50;
            float ema12, ema26, dif = 0, dea = 0, bar = 0, pre_ema12 = 0, pre_ema26 = 0, pre_dea = 0;
            MACDData pre_macd = null;
            MACDData now_macd = null;
            KDJData now_kdj = null;
            KDJData pre_kdj = null;
            float macd_h = 0;
            float macd_l = 100;
            int total_num = 0;
            do {
                try {
                    sd = stockUtil.getListStockData(mClient);
                } catch (Exception e) {
                    e.printStackTrace();
                    // return 0;
                }
                total_num = sd.size();
            } while (isRunning && total_num == 0);
            Log.e(TAG, "thread run  : num " + total_num);
            try {
                while (isRunning) {
                    // while (!this) {
                    // kp = 50;// (2 / 3) * 50 + (1 / 3) * rsv;
                    mSTApplication.clearKDJ();
                    mSTApplication.clearMACD();
                    Log.e(TAG, "clear total_num: " + total_num);


                    // from last to first
                    for (int i = total_num - 1; i >= 0; i--) {
                        mSTApplication.clearFinds();
                        float maxvolume = 0;

                        float lowValue = 1000, highValue = 0;
                        macd_h = 0;
                        macd_l = 100;
                        kdj_9h = 0;
                        kdj_9l = 100;
                        kdj_h = 100;
                        kdj_l = 0;
                        int l = 0;
                        // get from i + DIS_NUM - 1 to i
                        Log.e(TAG, "clear: " + i + "/" + total_num);
                        for (l = i + DIS_NUM - 1; l >= i; l--) {
                            Log.e(TAG, "l:" + l + " /" + i);
                            if (l >= total_num) {//skip{
                                l = total_num;
                                continue;
                            }
                            Log.e(TAG, "l:" + l);
                            StockData s = sd.get(l);
                            if (l == total_num - 1) {
                                pre_macd = new MACDData(s.close, s.close, 0, 0, 0);
                                pre_kdj = new KDJData();
                            }

                            if (kdj_n > l - i) {
                                kdj_9h = (s.high > kdj_9h) ? s.high : kdj_9h;
                                kdj_9l = (s.low < kdj_9l) ? s.low : kdj_9l;
                            }
                            Log.e(TAG, "addKDJ new + : " + kdj_9h + " " + kdj_9l + " l:" + l + " i:" + i);

                            now_macd = new MACDData(s, pre_macd);
                            pre_macd = now_macd;
                            macd_h = (macd_h > now_macd.maxV ? macd_h : now_macd.maxV);
                            macd_l = (macd_l < now_macd.minV ? macd_l : now_macd.minV);

                            Log.e(TAG, "aMACD  " + l + ": " + now_macd.toString());
                            // kdj
//                            now_kdj =

                            if (l > total_num - 1 - kdj_n) {
                                pre_kdj = new KDJData();
                            } else {
                                pre_kdj = new KDJData(s, pre_kdj, kdj_9h, kdj_9l);
                            }
                            Log.e(TAG, "aKDJ  " + l + ": " + pre_kdj.toString());
                            if (l == i) {
//                                if (i > total_num - 1 - kdj_n) {
                                mSTApplication.addKDJ(pre_kdj);
//                                } else {
//                                    mSTApplication.addKDJ(now_kdj);
//                                }
                                // macd
                                mSTApplication.addMACD(now_macd);

                                if (l + DIS_NUM < total_num - 1) {// -1 for
                                    // first
                                    mSTApplication.removeFirstKDJ();
                                    mSTApplication.removeFirstMACD();
                                }

                            }
                            maxvolume = (s.volume > maxvolume) ? s.volume
                                    : maxvolume;
                            highValue = (s.high > highValue) ? s.high
                                    : highValue;
                            lowValue = (s.low < lowValue) ? s.low : lowValue;

                            //
                            kdj_h = (s.high > kdj_h) ? s.high : kdj_h;
                            kdj_l = (s.low < kdj_l) ? s.low : kdj_l;
                            kdj_h = (kdj_h > pre_kdj.j) ? kdj_h : pre_kdj.j;
                            kdj_l = (kdj_l < pre_kdj.j) ? kdj_l : pre_kdj.j;
                            mSTApplication.addFind(s);
                        }
                        kdj_h += 10;
                        kdj_l -= 10;
                        if (DEBUG) {
                            Log.e(TAG, "aMACD H L: " + macd_h + " :" + macd_l);
                            Log.e(TAG, "aKDJ H L: " + kdj_h + " :" + kdj_l);
                        }
                        recent_sd = sd.get(l + 1);//get
                        mSTApplication.setCodeText(recent_sd.date + recent_sd.name + recent_sd.code);
                        mSTApplication.SetValue(maxvolume, highValue, lowValue);
                        mSTApplication.setMACDMaxMin(macd_h, macd_l);
                        mSTApplication.SetKDJValue(kdj_h, kdj_l);
                        mSTApplication.display();

                        if (UserState == EnumState.BUY) {
                            buyStock(recent_sd);
                            UserState = EnumState.IDLE;
                        } else if (UserState == EnumState.SELL) {
                            sellStock(recent_sd, 1);// open value
                            UserState = EnumState.IDLE;
                        } else if (UserState == EnumState.IDLE) {
                            updateStock(recent_sd);
                        }

                        if (i == 0) {
                            sellStock(recent_sd, 0);// close value
                        }

                        mSTApplication.setUserData(mUD);
                        if (!isRunning) {
                            sellStock(recent_sd, 0);// close value
                            break;
                        }
                        int e = 0;
                        while ((e++ < 20) && (isRunning)) {
                            Thread.sleep(10);//50
                        }
//                        Log.e("TAG", "SS" + android.os.Process.myTid());
                        while (!mSTApplication.IsDisplayDone() || isPause) {
                            Thread.sleep(100);
                        }
                    }
                    isRunning = false;
                    // }
                }//
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGetDataThread = null;
            Log.i(TAG, "thread exit");
        }
    };

    private void StartGetData() {
        if (!isRunning) {
            isRunning = true;
            // if(mGetDataThread==null)
            mGetDataThread = new Thread(getdataable);
            mGetDataThread.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return false;
    }

    private void StopGetData() {
        isRunning = false;
        if (mGetDataThread.isAlive()) {
            try {
                Log.e(TAG, "join");
                //mGetDataThread.interrupt();
                mGetDataThread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void PauseGetData() {
        isPause = isPause ? false : true;
    }


    private void UserBuy() {
        if (UserState == EnumState.IDLE)
            UserState = EnumState.BUY;
    }

    private void UserSell() {
        if (UserState == EnumState.IDLE)
            UserState = EnumState.SELL;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        String qualifier = (String) arg;
        if (qualifier.equals(STApplication.ST_SERVICE_START)) {
            StartGetData();// updateWifiList();
        } else if (qualifier.equals(STApplication.ST_SERVICE_STOP)) {
            StopGetData();// updateWifiList();
        } else if (qualifier.equals(STApplication.ST_SERVICE_PAUSE)) {
            PauseGetData();
        } else if (qualifier.equals(STApplication.ST_SERVICE_BUY)) {
            UserBuy();// updateWifiList();
        } else if (qualifier.equals(STApplication.ST_SERVICE_SELL)) {
            UserSell();// updateWifiList();
        }
    }

    public void onCreate() {
        mSTApplication = (STApplication) getApplication();
        mSTApplication.addObserver(this);
        mUD = mSTApplication.userData;
        mClient = Stock2Client.getInstance();
        Log.i(TAG, "onCreate");
    }

    public void onDestroy() {
        mSTApplication.deleteObserver(this);
        isRunning = false;
//        if (mGetDataThread.isAlive()) {
//            try {
////                mGetDataThread.interrupt();
//                mGetDataThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        Log.i(TAG, "onDestroy");
    }

    public class MyServiceBinder extends Binder implements
            ISTServiceAIDLInterface {

        @Override
        public IBinder asBinder() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean IsServerRun() throws RemoteException {
            // TODO Auto-generated method stub
            return isRunning;
        }

        @Override
        public boolean HaveStock() throws RemoteException {
            // TODO Auto-generated method stub
            return haveStock;
        }

        @Override
        public boolean IsServerPause() throws RemoteException {
            // TODO Auto-generated method stub
            return isPause;
        }

        @Override
        public void setDisNum(int n) throws RemoteException {
            // TODO Auto-generated method stub
            DIS_NUM = n;
        }


    }
}
