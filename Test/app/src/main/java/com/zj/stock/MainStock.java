package com.zj.stock;

import java.util.LinkedList;
import java.util.List;

import com.zj.stock.STApplication;
import com.zj.stock.Observer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainStock extends Activity implements Observer {

	private static final String TAG = "MainStock";
	private final boolean DEBUG = false;
	// private Context mContext = this;
	// private DownloadYahooData dlyd = null;
	// private PaintTheKLine ptk = null;
	public STApplication mSTApplication;

	TextView mTextView;
	TextView mHTextView;
	TextView mOTextView;
	TextView mCTextView;
	TextView mLTextView;

	TextView mSTValue;
	TextView mSTMarket;
	TextView mBalance;

	Coordinates mDKline;
	Coordinates mDVolume;
	Coordinates mDKDJ;
	Coordinates mDMACD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_stock);

		mSTApplication = (STApplication) getApplication();
		mSTApplication.addObserver(this);

		mTextView = (TextView) findViewById(R.id.textView1);

		mHTextView = (TextView) findViewById(R.id.high_value);
		mOTextView = (TextView) findViewById(R.id.open_value);
		mCTextView = (TextView) findViewById(R.id.close_value);
		mLTextView = (TextView) findViewById(R.id.low_value);

		mSTMarket = (TextView) findViewById(R.id.stock_market_v);
		mSTValue = (TextView) findViewById(R.id.stock_value_v);
		mBalance = (TextView) findViewById(R.id.balance_v);

		mDKline = (Coordinates) findViewById(R.id.kline);
		mDVolume = (Coordinates) findViewById(R.id.volume);

		mDKDJ = (Coordinates) findViewById(R.id.kdj);
		mDMACD = (Coordinates) findViewById(R.id.macd);

		final ImageButton startbtn = (ImageButton) findViewById(R.id.btn_start);
		startbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "startbtn");
				if (mSTApplication.isServerRun()) {
					mSTApplication.stop();
					startbtn.setImageDrawable(getResources().getDrawable(
							R.drawable.button_blue_start));
				} else {
					mSTApplication.start();
					startbtn.setImageDrawable(getResources().getDrawable(
							R.drawable.button_blue_stop));
				}
			}
		});

		final ImageButton pausebtn = (ImageButton) findViewById(R.id.btn_pause);
		pausebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "stopbtn");
				if (mSTApplication.isServerPause()) {
					mSTApplication.pause();
					pausebtn.setImageDrawable(getResources().getDrawable(
							R.drawable.button_blue_pause));
				} else {
					mSTApplication.pause();
					pausebtn.setImageDrawable(getResources().getDrawable(
							R.drawable.button_blue_start));

				}
			}
		});

		final ImageButton transactionbtn = (ImageButton) findViewById(R.id.btn_transaction);
		transactionbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "buybtn");
				if (mSTApplication.haveStock()) {
					mSTApplication.sell();
					transactionbtn.setImageDrawable(getResources().getDrawable(
							R.drawable.buy));
				} else {
					mSTApplication.buy();
					transactionbtn.setImageDrawable(getResources().getDrawable(
							R.drawable.sell));
				}
			}
		});

		int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
		Log.e(TAG, "CPU cores = " + NUMBER_OF_CORES);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main_stock, menu);
	// return true;
	// }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// this.unregisterReceiver(wifiReceiver);
		mSTApplication.deleteObserver(this);
	}

	private void UserDataDisplay() {

		UserData mUD = mSTApplication.userData;
		Message message = mHandler.obtainMessage(HANDLE_UPDATE_USERDATA, mUD);
		mHandler.sendMessage(message);
	}

	@SuppressWarnings("null")
	public void updateDisplay() {
		List<StockData> list = mSTApplication.getFinds();
		LinkedList mKDJs = mSTApplication.getKDJLinkedList();
		int kdj_len;
		LinkedList mMACDs = mSTApplication.getMACDLinkedList();
		int macd_len;
		mDKline.clearList();
		mDVolume.clearList();
		mDKDJ.clearList();
		mDMACD.clearList();
		if (list != null) {
			StockData sd1 = null;
			StockData pre_sd = null;
			KDJData kdj = null;
			KDJData pre_kdj = null;
			MACDData macd = null, pre_macd = null;
			// int k_len;
			int len = list.size();
			sd1 = list.get(len - 1);// first new
			// KLine kl1 = new KLine(sd1.open, sd1.high, sd1.low, sd1.close);
			float mv = mSTApplication.GetMaxVolume();
			// Log.e(TAG, "h" + mSTApplication.GetHighValue() + "l"
			// + mSTApplication.GetLowValue());
			mDKline.setPValue(mSTApplication.GetHighValue(),
					mSTApplication.GetLowValue());
			mDKline.addKline(sd1, len - 1);

			pre_sd = sd1;
			kdj_len = mKDJs.size();// 3
			macd_len = mMACDs.size();
			// k_len = kdj_len;
			// Log.e(TAG, "" + kdj_len + " " + len);
			if (macd_len > 0) {
				mDMACD.setPValue(100, 1);
				macd = (MACDData) mMACDs.get(macd_len - 1);
				if (macd_len == 1) {
					pre_macd = new MACDData(0, 0, 0, 0, 0);
				} else {
					pre_macd = (MACDData) mMACDs.get(macd_len - 2);
				}
				mDMACD.addMACD(pre_macd, macd, len - 1);
			}
			if (kdj_len > 0) {
				// mKDJs.getLast();
				mDKDJ.setPValue(110, -10);
				kdj = (KDJData) mKDJs.get(kdj_len - 1);// 2
				// mKDJs.removeLast();
				if (kdj_len == 1) {// left
					// Log.e(TAG, "qqq: kkk");
					pre_kdj = new KDJData(50, 50, 50, 50);
				} else {
					pre_kdj = (KDJData) mKDJs.get(kdj_len - 2);// 2
				}
				mDKDJ.addKDJ(pre_kdj, kdj, len - 1);

			}
			mDVolume.addVolume(sd1, len - 1, mv);

			Message message = mHandler.obtainMessage(HANDLE_UPDATE_TEXTVIEW,
					sd1);
			mHandler.sendMessage(message);

			if (len > 1) {

				StockData sd2;
				sd2 = list.get(len - 2);// second
				mDKline.addKline(sd2, len - 2);
				if (macd_len > 0) {
					macd = pre_macd;
					if (macd_len == 2) {// left
						// Log.e(TAG, "qqq: kkk");
						pre_macd = new MACDData(0, 0, 0, 0, 0);
					} else {
						pre_macd = (MACDData) mMACDs.get(macd_len - 3);// 1
					}
					// mKDJs.removeLast();

					mDMACD.addMACD(pre_macd, macd, len - 2);
				}
				if (kdj_len > 0) {
					kdj = pre_kdj;
					if (kdj_len == 2) {// left
						// Log.e(TAG, "qqq: kkk");
						pre_kdj = new KDJData(50, 50, 50, 50);
					} else {
						pre_kdj = (KDJData) mKDJs.get(kdj_len - 3);// 1
					}
					// mKDJs.removeLast();

					mDKDJ.addKDJ(pre_kdj, kdj, len - 2);
				}
				pre_sd = sd2;
				mDVolume.addVolume(sd2, len - 2, mv);
				float fn, fl;
				fn = sd1.close;
				fl = sd2.close;
				String as = 100 * (fn - fl) / fl + "%";
				message = mHandler.obtainMessage(HANDLE_UPDATE_GAINS, as);
				mHandler.sendMessage(message);

				for (int i = len - 3, j = kdj_len - 3; i >= 0; i--, j--) {
					StockData sd3;
					sd3 = list.get(i);// third ...
					// sd2 = list.get(i);
					// kl1 = new KLine(sd3.open, sd3.high, sd3.low, sd3.close);
					mDKline.addKline(sd3, i);
					if (macd_len > 0) {
						macd = pre_macd;
						// mKDJs.removeLast();
						if ((i == 0) && (macd_len == len)) {// left
							pre_macd = new MACDData(0, 0, 0, 0, 0);
						} else {
							pre_macd = (MACDData) mMACDs.get(j - 1);// 0
						}
						if (DEBUG)
							Log.e(TAG, "macd_len: " + macd_len + " " + j + " "
									+ pre_macd.dif + " " + macd.dif);
						mDMACD.addMACD(pre_macd, macd, i);
					}
					if (kdj_len > 0) {
						kdj = pre_kdj;
						// mKDJs.removeLast();
						if ((i == 0) && (kdj_len == len)) {// left
							pre_kdj = new KDJData(50, 50, 50, 50);
						} else {
							pre_kdj = (KDJData) mKDJs.get(j - 1);// 0
						}
						if (DEBUG)
							Log.e(TAG, "kdj_len: " + kdj_len + " " + j + " "
									+ pre_kdj.k + " " + kdj.k);
						mDKDJ.addKDJ(pre_kdj, kdj, i);
					}
					pre_sd = sd3;
					mDVolume.addVolume(sd3, i, mv);

				}
				// sd1 = list.get(1);
				// sd2 = list.get(0);
				// kl1 = new KLine(sd1.open, sd1.high, sd1.low, sd1.close);
				// mDKline.addKline(kl1, 1);
				// kl1 = new KLine(sd2.open, sd2.high, sd2.low, sd2.close);
				// mDKline.addKline(kl1, 0);
			}

			if (len > 20) {
				;// display kdj
			}
			// Log.e(TAG, "open " + sd1.open);
			mDKline.postInvalidate();
			mDVolume.postInvalidate();
			mDKDJ.postInvalidate();
			mDMACD.postInvalidate();
		} else {
			;// Log.i(TAG, "wifi list null");
		}
		mSTApplication.DisplayDone();
	}

	private static final int HANDLE_UPDATE_TEXTVIEW = 0;
	private static final int HANDLE_UPDATE_GAINS = 1;
	private static final int HANDLE_UPDATE_USERDATA = 2;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_UPDATE_TEXTVIEW: {
				StockData sd = null;
				sd = (StockData) msg.obj;
				mHTextView.setText(sd.high + "");
				mOTextView.setText(sd.open + "");
				mLTextView.setText(sd.low + "");
				mCTextView.setText(sd.close + "");

			}
				break;
			case HANDLE_UPDATE_GAINS:
				String s = (String) msg.obj;
				mTextView.setText(s);
				break;
			case HANDLE_UPDATE_USERDATA:
				UserData mUD = (UserData) msg.obj;
				mSTValue.setText("" + mUD.stock_value);
				mSTMarket.setText("" + mUD.stock_market);
				mBalance.setText("" + mUD.balance);
				break;
			}
		}
	};

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		String qualifier = (String) arg;
		if (qualifier.equals(STApplication.ST_GET_ONE)) {
			updateDisplay();// updateWifiList();
		} else if (qualifier.equals(STApplication.ST_USER_DATA)) {
			UserDataDisplay();// updateWifiList();
		} else if (qualifier.equals(STApplication.ST_GET_MIDDLE)) {
			;// updateWifiList();
		} else if (qualifier.equals(STApplication.ST_GET_LAST)) {
			;// updateWifiList();
		}
	}

}
