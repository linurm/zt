package com.zj.stock;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.zj.stock.Observable;
import com.zj.stock.STApplication;
import com.zj.stock.Observer;
import com.zj.test.EnumState;

public class STService extends Service implements Observer {

	public STApplication mSTApplication;
	private final String TAG = "STService";
	private final boolean DEBUG = false;
	private volatile boolean isRunning = false;
	private volatile boolean isPause = false;
	private volatile boolean haveStock = false;
	private final int DIS_NUM = 35;
	private Thread mGetDataThread = null;
	private EnumState UserState = EnumState.IDLE;
	private UserData mUD;
	private final int kdj_n = 9;
	private float kdj_h, kdj_l;

	private void StartGetData() {
		if (!isRunning) {
			isRunning = true;
			// if(mGetDataThread==null)
			mGetDataThread = new Thread(getdataable);
			mGetDataThread.start();
		}
	}

	private MyServiceBinder mBinder = new MyServiceBinder();

	public class MyServiceBinder extends Binder implements
			STServiceAIDLInterface {

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
			// try {
			Log.e(TAG, "join");
			// mGetDataThread.interrupt();
			// mGetDataThread.join();
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
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
		Log.i(TAG, "onCreate");
	}

	// private Thread mGetDataThread = new Thread() {
	private Runnable getdataable = new Runnable() {
		// Socket client;
		private StockData recent_sd = null;

		private void sell_stock(StockData mSD, int open_close) {
			if (mUD.stock_num != 0) {
				haveStock = false;
				mUD.stock_value = mUD.balance + mUD.stock_num
						* (open_close == 1 ? mSD.open : mSD.close) * 100;
				mUD.stock_market = 0;
				mUD.stock_num = 0;
				mUD.balance = mUD.stock_value;
			}
		}

		private void buy_stock(StockData mSD) {
			mUD.stock_num = (int) (mUD.stock_value / (mSD.open) / 100);
			mUD.stock_market = (mSD.open) * mUD.stock_num * 100;
			mUD.balance = mUD.stock_value - mUD.stock_market;
			haveStock = true;
		}

		private void update_stock(StockData mSD) {
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
			GetDataFromYahooUtil stockUtil = new GetDataFromYahooUtil();

			List<StockData> sd = null;
			float k, kp, rsv, pre_k = 50, d, j, pre_d = 50;
			float ema12, ema26, dif, dea, bar, pre_ema12 = 0, pre_ema26 = 0, pre_dea = 0;
			try {
				sd = stockUtil.getListStockData("000839.sz", "2002-10-22",
						"2014-12-31");
			} catch (Exception e) {
				e.printStackTrace();
				// return 0;
			}
			int total_num = sd.size();
			Log.i(TAG, "thread run  : num " + total_num);
			try {
				while (isRunning) {
					// while (!this) {
					// kp = 50;// (2 / 3) * 50 + (1 / 3) * rsv;
					mSTApplication.clearKDJ();
					mSTApplication.clearMACD();
					Log.e(TAG, "clear: ");
					// from last to first
					for (int i = total_num - 1; i >= 0; i--) {
						mSTApplication.clearFinds();
						float maxvolume = 0;
						float lowValue = 1000, highValue = 0;

						int l = 0;
						// get from i + DIS_NUM - 1 to i
						for (l = i + DIS_NUM - 1; l >= i; l--) {
							if (l >= total_num)
								continue;

							StockData s = sd.get(l);
							if (kdj_n >= l - i) {
								kdj_h = (s.high > kdj_h) ? s.high : kdj_h;
								kdj_l = (s.low < kdj_l) ? s.low
										: kdj_l <= 0 ? s.low : kdj_l;
							}

							if (l == i) {
								// macd
								if (l == total_num - 1) {
									pre_ema12 = s.close;
									pre_ema26 = s.close;
									// Log.e(TAG,"*************************");
								}
								ema12 = (11 * pre_ema12 + 2 * s.close) / 13;
								ema26 = (25 * pre_ema26 + 2 * s.close) / 27;
								dif = ema12 - ema26;
								if (l == total_num - 1) {
									pre_dea = dif;
								}
								dea = (4 * pre_dea + 1 * dif) / 5;
								bar = 2 * (dif - dea);
								pre_ema12 = ema12;
								pre_ema26 = ema26;
								pre_dea = dea;
								//
								// kp = pre_k;
								// kdj
								rsv = (float) ((s.close - kdj_l) / (float) (kdj_h - kdj_l)) * 100;
								if (l == total_num - 1) {
									pre_k = 50;
									pre_d = 50;
								}
								k = (float) (2 * pre_k + rsv) / 3;
								d = (float) (2 * pre_d + k) / 3;
								j = (float) (3 * k - 2 * d);
								pre_k = k;
								pre_d = d;
								//
								if (DEBUG)
									Log.d(TAG, " dif: " + dif + " dea: " + dea
											+ " bar: " + bar);
								// if (DEBUG)
								Log.d(TAG, " k: " + k + " d: " + d + " j: " + j);

								KDJData mk = new KDJData(k, d, j, rsv);
								MACDData mM = new MACDData(ema12, ema26, dif,
										dea, bar);

								if (l + DIS_NUM < total_num - 1) {// -1 for
																	// first
									mSTApplication.removeFirstKDJ();
									mSTApplication.removeFirstMACD();
									// Log.e(TAG, "remove first: " + j);
								}
								mSTApplication.addKDJ(mk);
								mSTApplication.addMACD(mM);
								// Log.d(TAG, "add kdj: " + mk.k + " : " +
								// mk.rsv
								// + " " + j);
							}

							maxvolume = (s.volume > maxvolume) ? s.volume
									: maxvolume;
							highValue = (s.high > highValue) ? s.high
									: highValue;
							lowValue = (s.low < lowValue) ? s.low : lowValue;
							mSTApplication.addFind(s);
						}

						recent_sd = sd.get(l + 1);

						mSTApplication.SetValue(maxvolume, highValue, lowValue);
						// mSTApplication.SetKDJValue(kdj_h, kdj_l);
						mSTApplication.display();

						if (UserState == EnumState.BUY) {
							buy_stock(recent_sd);
							UserState = EnumState.IDLE;
						} else if (UserState == EnumState.SELL) {
							sell_stock(recent_sd, 1);// open value
							UserState = EnumState.IDLE;
						} else if (UserState == EnumState.IDLE) {
							update_stock(recent_sd);
						}

						if (i == 0) {
							sell_stock(recent_sd, 0);// close value
						}

						mSTApplication.setUserData(mUD);
						if (!isRunning) {
							sell_stock(recent_sd, 0);// close value
							break;
						}
						Thread.sleep(1000);

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

	public void onDestroy() {
		mSTApplication.deleteObserver(this);
		isRunning = false;
		if (mGetDataThread.isAlive()) {
			try {
				mGetDataThread.interrupt();
				mGetDataThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Log.i(TAG, "onDestroy");
	}
}
