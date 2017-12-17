package com.zj.stock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class MainStock extends Activity implements Observer {

    private static final String TAG = "MainStock";
    private static final int HANDLE_UPDATE_TEXTVIEW = 0;
    private static final int HANDLE_UPDATE_GAINS = 1;
    private static final int HANDLE_UPDATE_USERDATA = 2;
    private static final int HANDLE_UPDATE_MACD_MAXMIN = 3;
    private static final int HANDLE_UPDATE_VOLUME_MAXMIN = 4;
    private static final int HANDLE_UPDATE_KLINE_MAXMIN = 5;
    private final boolean DEBUG = false;
    // private Context mContext = this;
    // private DownloadYahooData dlyd = null;
    // private PaintTheKLine ptk = null;
    public STApplication mSTApplication;
    TextView mTextView;
    TextView mGainView;
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

    TextView mHv;
    TextView mLv;
    TextView mVh;
    TextView mVl;
    TextView mKh;
    TextView mKl;
    TextView mMh;
    TextView mMl;

    TextView mCodeText;
    private boolean isLookCode = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_UPDATE_TEXTVIEW:
                    StockData sd = null;
                    sd = (StockData) msg.obj;
                    if (isLookCode)
                        mCodeText.setText(mSTApplication.getCodeText());
                    mHTextView.setText(sd.high + "");
                    mOTextView.setText(sd.open + "");
                    mLTextView.setText(sd.low + "");
                    mCTextView.setText(sd.close + "");
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
                    mGainView.setText("" + mUD.gains);
                    break;
                case HANDLE_UPDATE_MACD_MAXMIN:
                    VauleMaxMin mmm = (VauleMaxMin) msg.obj;

                    mMh.setText(Utils.f2String(mmm.max));
                    mMl.setText(Utils.f2String(mmm.min));
                    break;
                case HANDLE_UPDATE_VOLUME_MAXMIN:
                    VauleMaxMin vmm = (VauleMaxMin) msg.obj;
                    String a = Utils.f2String(vmm.max / 1000000);
                    mVh.setText(a + "W");
                    mVl.setText("" + vmm.min);
                    break;
                case HANDLE_UPDATE_KLINE_MAXMIN:
                    VauleMaxMin kmm = (VauleMaxMin) msg.obj;
                    mHv.setText("" + kmm.max);
                    mLv.setText("" + kmm.min);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_stock);

        mSTApplication = (STApplication) getApplication();
        mSTApplication.addObserver(this);

        mTextView = (TextView) findViewById(R.id.textView1);
        mGainView = (TextView) findViewById(R.id.stock_gain_v);

        mHTextView = (TextView) findViewById(R.id.high_value);
        mOTextView = (TextView) findViewById(R.id.open_value);
        mCTextView = (TextView) findViewById(R.id.close_value);
        mLTextView = (TextView) findViewById(R.id.low_value);

        mSTMarket = (TextView) findViewById(R.id.stock_market_v);
        mSTValue = (TextView) findViewById(R.id.stock_value_v);
        mBalance = (TextView) findViewById(R.id.balance_v);

        mDKline = (Coordinates) findViewById(R.id.kline);
        mDVolume = (Coordinates) findViewById(R.id.volume);

        mHv = (TextView) findViewById(R.id.hv);
        mLv = (TextView) findViewById(R.id.lv);
        mVh = (TextView) findViewById(R.id.vh);
        mVl = (TextView) findViewById(R.id.vl);
        mKh = (TextView) findViewById(R.id.kh);
        mKl = (TextView) findViewById(R.id.kl);
        mMh = (TextView) findViewById(R.id.mh);
        mMl = (TextView) findViewById(R.id.ml);

        mKh.setText("110");
        mKl.setText("-10");
        mDKDJ = (Coordinates) findViewById(R.id.kdj);
        mDMACD = (Coordinates) findViewById(R.id.macd);
        final ImageButton transactionbtn = (ImageButton) findViewById(R.id.btn_transaction);
        transactionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "buybtn");
                if (mSTApplication.haveStock()) {
                    mSTApplication.sell();
//                    mGainView.setText();
                    transactionbtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.buy));
                } else {
                    mSTApplication.buy();
                    transactionbtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.label_blue_buy));
                }
            }
        });


        final ImageButton pausebtn = (ImageButton) findViewById(R.id.btn_pause);
        pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e(TAG, "stopbtn " + android.os.Process.myTid());
                if (mSTApplication.isServerPause()) {
                    mSTApplication.ReversalPauseState();
                    pausebtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.button_blue_pause));
                } else {
                    mSTApplication.ReversalPauseState();
                    pausebtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.button_blue_start));
                }
            }
        });
        final ImageButton startbtn = (ImageButton) findViewById(R.id.btn_start);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "startbtn");
                if (mSTApplication.isServerRun()) {
                    if (mSTApplication.haveStock()) {
                        mSTApplication.sell();
                        transactionbtn.setImageDrawable(getResources().getDrawable(
                                R.drawable.label_blue_buy));
                    }
                    if (mSTApplication.isServerPause()) {
                        mSTApplication.ReversalPauseState();
                        pausebtn.setImageDrawable(getResources().getDrawable(
                                R.drawable.button_blue_pause));
                    }
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
        final ImageButton lookCode = (ImageButton) findViewById(R.id.lookCode);
        mCodeText = (TextView) findViewById(R.id.codeText);
        lookCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isLookCode) {
                    isLookCode = false;
                    mCodeText.setText("");
                } else {
                    isLookCode = true;
                }
//                if (mCodeText.getText() != "") {
//                    mCodeText.setText("");
//                } else {
//                    mCodeText.setText(mSTApplication.getCodeText());
//                }
            }
        });
        final ImageButton changebtn = (ImageButton) findViewById(R.id.btn_change);

        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSTApplication.isServerRun()) {
                    if (mSTApplication.haveStock()) {
                        mSTApplication.sell();
                        transactionbtn.setImageDrawable(getResources().getDrawable(
                                R.drawable.label_blue_buy));
                    }
                    mSTApplication.stop();
                    startbtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.button_blue_start));
                    startbtn.setImageDrawable(getResources().getDrawable(
                            R.drawable.button_blue_stop));
                    mSTApplication.start();

                }
            }
        });


        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        Log.e(TAG, "CPU cores = " + NUMBER_OF_CORES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewTreeObserver vto2 = mDKline.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDKline.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Log.e(TAG, "!!!!!!!!!" + mDKline.getHeight() + "," + mDKline.getWidth());
                mDKline.dayNum = mDKline.getWidth() / mDKline.mOnew;
                mSTApplication.setDisplayNum(mDKline.dayNum);
            }
        });
//        Log.e(TAG, "" + mDKline.getRight() + ":" + mDKline.getLeft());
//        Log.e(TAG, "" + DensityUtil.dip2px(this, mDKline.getWidth()));
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // this.unregisterReceiver(wifiReceiver);
        mSTApplication.unbindService();
        mSTApplication.deleteObserver(this);
    }

    private void UserDataDisplay() {
        UserData mUD = mSTApplication.userData;
        Message message = mHandler.obtainMessage(HANDLE_UPDATE_USERDATA, mUD);
        mHandler.sendMessage(message);
    }

    public class VauleMaxMin {
        public float max;
        public float min;

        public VauleMaxMin(float max, float min) {
            this.max = max;
            this.min = min;
        }

    }

    @SuppressWarnings("null")
    public void updateDisplay() {
        List<StockData> list = mSTApplication.getFinds();//from last
        LinkedList mKDJs = mSTApplication.getKDJLinkedList();
        int kdj_len;
        List<MACDData> mMACDs = mSTApplication.getMACDList();
        int macd_len;
        int avg_len;
        List<AvgValue> mAvgs = mSTApplication.getAvgList();
        mDKline.clearList();
        mDVolume.clearList();
        mDKDJ.clearList();
        mDMACD.clearList();
        if (list != null) {
            StockData sd1 = null;
            StockData pre_sd = null;
            KDJData kdj = null;
            AvgValue avg = null;
            KDJData pre_kdj = null;
            AvgValue pre_avg = null;
            MACDData now_macd = null, pre_macd = null, macd = null;
//            float macd_l = 100; float macd_h = 0;

            // int k_len;
            int len = list.size();
            sd1 = list.get(len - 1);// right one
            if (DEBUG)
                Log.e("TAG", "aMACD -------------- " + sd1.date);
            // KLine kl1 = new KLine(sd1.open, sd1.high, sd1.low, sd1.close);
            float mv = mSTApplication.GetMaxVolume();

            VauleMaxMin vmm = new VauleMaxMin(mv, 0);
            Message vmessage = mHandler.obtainMessage(HANDLE_UPDATE_VOLUME_MAXMIN,
                    vmm);
            mHandler.sendMessage(vmessage);

            float mh = mSTApplication.GetMaxMacd();
            float ml = mSTApplication.GetMinMacd();

            float vmh = mSTApplication.GetHighValue();
            float vml = mSTApplication.GetLowValue();
//            Log.e(TAG, "SET h:" + vmh + " l:" + vml);
            mDKline.setPValue(vmh, vml);

            VauleMaxMin kmm = new VauleMaxMin(vmh, vml);
            Message kmessage = mHandler.obtainMessage(HANDLE_UPDATE_KLINE_MAXMIN,
                    kmm);
            mHandler.sendMessage(kmessage);

            mDKline.addKline(sd1, len - 1);//add right kline

            pre_sd = sd1;
            kdj_len = mKDJs.size();// 3
            macd_len = mMACDs.size();
            avg_len = mAvgs.size();


            if (macd_len > 0) {//max and min
                mDMACD.setPValue(mh, ml);
                VauleMaxMin mmm = new VauleMaxMin(mh, ml);
                Message mmessage = mHandler.obtainMessage(HANDLE_UPDATE_MACD_MAXMIN,
                        mmm);
                mHandler.sendMessage(mmessage);
            }
            if (kdj_len > 0) {//max and min
                mDKDJ.setPValue(110, -10);
                kdj = (KDJData) mKDJs.get(kdj_len - 1);// 2
                if (kdj_len == 1) {// left
                    pre_kdj = new KDJData();
                } else {
                    pre_kdj = (KDJData) mKDJs.get(kdj_len - 2);// 2
                }
                mDKDJ.addKDJ(pre_kdj, kdj, len - 1);
            }
            if (avg_len > 0) {
                avg = mAvgs.get(avg_len - 1);
                if (avg_len == 1) {// left
                    pre_avg = new AvgValue(0, 0, 0);
                } else {
                    pre_avg =  mAvgs.get(avg_len - 2);// 2
                }
                mDKline.addAvg(pre_avg, avg, len - 1);
            }
            mDVolume.addVolume(sd1, len - 1, mv);

            Message tmessage = mHandler.obtainMessage(HANDLE_UPDATE_TEXTVIEW,
                    sd1);
            mHandler.sendMessage(tmessage);

            if (len > 1) {//2,3,4,...
                StockData sd2;
                sd2 = list.get(len - 2);// second right
                if (DEBUG)
                    Log.e("TAG", "s " + sd2.date);
                mDKline.addKline(sd2, len - 2);
                if (kdj_len > 0) {
                    kdj = pre_kdj;
                    if (kdj_len == 2) {// left
                        // Log.e(TAG, "qqq: kkk");
                        pre_kdj = new KDJData();
                    } else {
                        pre_kdj = (KDJData) mKDJs.get(kdj_len - 3);// 1
                    }
                    mDKDJ.addKDJ(pre_kdj, kdj, len - 2);
                }
                if (avg_len > 0) {
                    avg = pre_avg;
                    if (avg_len == 2) {// left
                        pre_avg = new AvgValue(0, 0, 0);
                    } else {
                        pre_avg = (AvgValue) mAvgs.get(avg_len - 3);// 1
                    }
                    mDKline.addAvg(pre_avg, avg, len - 2);
                }
                pre_sd = sd2;
                mDVolume.addVolume(sd2, len - 2, mv);
                float fn, fl;
                fn = sd1.open;
                fl = sd2.close;


                float price = 100 * (fn - fl) / fl;
                String as = Utils.f2String(price) + "%";//gain
                Message gmessage = mHandler.obtainMessage(HANDLE_UPDATE_GAINS, as);
                mHandler.sendMessage(gmessage);
                int num = (macd_len > mDKline.dayNum) ? (macd_len - mDKline.dayNum) : 0;
                for (int i = macd_len - 1; i >= num; i--) {
                    if (macd_len > 0) {
                        macd = mMACDs.get(i);// 0
                        if (i == 0) {// left
                            pre_macd = new MACDData(0, 0, 0, 0, 0, 0);
                        } else {
                            pre_macd = mMACDs.get(i - 1);
                        }
                        int index = i - num;
                        if (DEBUG)
                            Log.e(TAG, "bMACD for: " + (index) + macd.toString());
                        mDMACD.addMACD(pre_macd, macd, index);
                    }
                }

                for (int i = len - 3, j = kdj_len - 3; i >= 0; i--, j--) {
                    StockData sd3;
                    sd3 = list.get(i);// third right
                    if (DEBUG)
                        Log.e("TAG", "  " + sd3.date);
                    mDKline.addKline(sd3, i);
                    if (kdj_len > 0) {
                        kdj = pre_kdj;
                        if ((i == 0) && (kdj_len == len)) {// left
                            pre_kdj = new KDJData();
                        } else {
                            pre_kdj = (KDJData) mKDJs.get(j - 1);// 0
                        }
                        if (DEBUG)
                            Log.e(TAG, "kdj_len: " + kdj_len + " " + j + " "
                                    + pre_kdj.k + " " + kdj.k);
                        mDKDJ.addKDJ(pre_kdj, kdj, i);
                    }
                    if (avg_len > 0) {
                        avg = pre_avg;
                        if ((i == 0) && (avg_len == len)) {// left
                            pre_avg = new AvgValue(0, 0, 0);
                        } else {
                            pre_avg =  mAvgs.get(j - 1);// 1
                        }
                        mDKline.addAvg(pre_avg, avg, i);
                    }
                    pre_sd = sd3;
                    mDVolume.addVolume(sd3, i, mv);
                }
            }

            if (len > 20) {
                ;// display kdj
            }
            mDKline.postInvalidate();
            mDVolume.postInvalidate();
            mDKDJ.postInvalidate();
            mDMACD.postInvalidate();
        } else {
            ;// Log.i(TAG, "wifi list null");
        }
        mSTApplication.DisplayDone();
    }

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
