package zj.zfenlly.stock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;

import zj.zfenlly.main.MainApplication;
import zj.zfenlly.other.Observable;
import zj.zfenlly.other.Observer;
import zj.zfenlly.tools.R;


@SuppressLint({"ValidFragment"})
@ContentView(R.layout.fragment_stock)
public class StockFragment extends Fragment implements Observer {

    private final String TAG = this.getClass().getName();
    private final boolean Debug = true;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private static final int MESSAGE_STOCK_SERVER_DISPLAYRUN = 1;
    private static final int MESSAGE_STOCK_SERVER_SIMULATION = 2;
    private static final int MESSAGE_STOCK_UPDATE_VIEW = 3;
    private int mColorRes = -1;
    // private SinaStockClient mClient;
    //@ViewInject(R.id.toggleButton)
    ToggleButton toggleButton;
    //@ViewInject(R.id.toggleButton2)
    ToggleButton tb_Simulation;

//    NotificationManager mNotificationManager;
//    Notification mNotification;
//    PendingIntent mPendingIntent;

    float p[] = new float[10];
    long v[] = new long[10];

    private StockInfo preStockInfo = null;

    public MainApplication mStockApplication;

    private ViewHolder holder;

    // TextView stock_name, now_open, last_close, high, low, now, vol, change,
    // percent;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    public String mName;

    public StockFragment() {
        StockFragmentInit(R.color.white, "stock");
    }

    public void StockFragmentInit(int colorRes, String name) {
        mColorRes = colorRes;
        mName = name;
        setRetainInstance(true);
    }

//    private void initNotify() {
//        mNotificationManager = (NotificationManager) getActivity()
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int icon = R.drawable.octocat;
//        CharSequence tickerText = "service is run background";
//        mNotification = new Notification(icon, tickerText,
//                System.currentTimeMillis());
//    }
//
//    private void initNotify2() {
//        mPendingIntent = PendingIntent.getActivity(getActivity(), 0,
//                new Intent(getActivity(), MainActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        mNotification.setLatestEventInfo(getActivity(), "1111111111111",
//                "2222222222222222222", mPendingIntent);
//        mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
//        mNotificationManager.notify(0, mNotification);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        int color = getResources().getColor(mColorRes);
        // print("qqqqqqqqqqqqqqqq");
        // construct the RelativeLayout
        View v = inflater.inflate(R.layout.fragment_stock, null);

        holder = new ViewHolder();
        ViewUtils.inject(holder, v);

//        initNotify();
        // View v = new View(getActivity());
        v.setBackgroundColor(color);
        initServiceDisplay();

        initToggleButton(v);

//        initNotify2();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

    public class ViewHolder {
        @ViewInject(R.id.textView00)
        TextView tv00;
        @ViewInject(R.id.textView01)
        TextView tv01;
        @ViewInject(R.id.textView02)
        TextView tv02;
        @ViewInject(R.id.textView10)
        TextView tv10;
        @ViewInject(R.id.textView11)
        TextView tv11;
        @ViewInject(R.id.textView12)
        TextView tv12;
        @ViewInject(R.id.textView20)
        TextView tv20;
        @ViewInject(R.id.textView21)
        TextView tv21;
        @ViewInject(R.id.textView22)
        TextView tv22;
        @ViewInject(R.id.textView30)
        TextView tv30;
        @ViewInject(R.id.textView31)
        TextView tv31;
        @ViewInject(R.id.textView32)
        TextView tv32;
        @ViewInject(R.id.textView40)
        TextView tv40;
        @ViewInject(R.id.textView41)
        TextView tv41;
        @ViewInject(R.id.textView42)
        TextView tv42;
        @ViewInject(R.id.textView50)
        TextView tv50;
        @ViewInject(R.id.textView51)
        TextView tv51;
        @ViewInject(R.id.textView52)
        TextView tv52;
        @ViewInject(R.id.textView60)
        TextView tv60;
        @ViewInject(R.id.textView61)
        TextView tv61;
        @ViewInject(R.id.textView62)
        TextView tv62;
        @ViewInject(R.id.textView70)
        TextView tv70;
        @ViewInject(R.id.textView71)
        TextView tv71;
        @ViewInject(R.id.textView72)
        TextView tv72;
        @ViewInject(R.id.textView80)
        TextView tv80;
        @ViewInject(R.id.textView81)
        TextView tv81;
        @ViewInject(R.id.textView82)
        TextView tv82;
        @ViewInject(R.id.textView90)
        TextView tv90;
        @ViewInject(R.id.textView91)
        TextView tv91;
        @ViewInject(R.id.textView92)
        TextView tv92;
        @ViewInject(R.id.nowopen)
        TextView now_open;
        @ViewInject(R.id.lastclose)
        TextView last_close;
        @ViewInject(R.id.low)
        TextView low;
        @ViewInject(R.id.high)
        TextView high;
        @ViewInject(R.id.now)
        TextView now;
        @ViewInject(R.id.change)
        TextView change;
        @ViewInject(R.id.percent)
        TextView percent;
        @ViewInject(R.id.vol)
        TextView vol;

        public ViewHolder() {

        }

        public void setChangeValueEmpty() {
            tv02.setText("");
            tv12.setText("");
            tv22.setText("");
            tv32.setText("");
            tv42.setText("");
            tv52.setText("");
            tv62.setText("");
            tv72.setText("");
            tv82.setText("");
            tv92.setText("");
        }

        public void displayOtherValue(StockInfo.BuyOrSellInfo[] b, StockInfo.BuyOrSellInfo[] s,
                                      float todayp, float yestodayp, float highp, float lowp,
                                      float nowp, float tradec) {
            int i;

            now_open.setText("" + todayp + "    ");
            last_close.setText("" + yestodayp + "    ");

            high.setText("" + highp + "    ");
            low.setText("" + lowp + "    ");
            now.setText("" + nowp);

            // color
            int c;
            if (nowp >= yestodayp) {
                c = Color.rgb(255, 0, 0);
            } else {
                c = Color.rgb(0, 139, 0);
            }
            now.setTextColor(c);
            change.setTextColor(c);
            percent.setTextColor(c);

            change.setText("" + decimalFormat.format(nowp - yestodayp));
            percent.setText(""
                    + decimalFormat
                    .format((nowp - yestodayp) / yestodayp * 100) + "%");

            vol.setText("" + tradec);

            if (preStockInfo != null) {
                for (i = 0; i < 10; i++) {
                    // print(":" + p[i]);
                    if (s[4].mPrice == p[i]) {
                        tv02.setText("" + (s[4].mCount - v[i]));
                        continue;
                    }
                    if (s[3].mPrice == p[i]) {
                        tv12.setText("" + (s[3].mCount - v[i]));
                        continue;
                    }
                    if (s[2].mPrice == p[i]) {
                        tv22.setText("" + (s[2].mCount - v[i]));
                        continue;
                    }
                    if (s[1].mPrice == p[i]) {
                        tv32.setText("" + (s[1].mCount - v[i]));
                        continue;
                    }
                    if (s[0].mPrice == p[i]) {
                        tv42.setText("" + (s[0].mCount - v[i]));
                        continue;
                    }
                    if (b[0].mPrice == p[i]) {
                        tv52.setText("" + (b[0].mCount - v[i]));
                        continue;
                    }
                    if (b[1].mPrice == p[i]) {
                        tv62.setText("" + (b[1].mCount - v[i]));
                        continue;
                    }
                    if (b[2].mPrice == p[i]) {
                        tv72.setText("" + (b[2].mCount - v[i]));
                        continue;
                    }
                    if (b[3].mPrice == p[i]) {
                        tv82.setText("" + (b[3].mCount - v[i]));
                        continue;
                    }
                    if (b[4].mPrice == p[i]) {
                        tv92.setText("" + (b[4].mCount - v[i]));
                        continue;
                    }
                }
            }

            // preStockInfo = event.mStockInfo;

            tv00.setText("" + s[4].mCount);
            tv01.setText("" + s[4].mPrice);
            p[0] = s[4].mPrice;
            v[0] = s[4].mCount;

            tv10.setText("" + s[3].mCount);
            tv11.setText("" + s[3].mPrice);
            p[1] = s[3].mPrice;
            v[1] = s[3].mCount;

            tv20.setText("" + s[2].mCount);
            tv21.setText("" + s[2].mPrice);
            p[2] = s[2].mPrice;
            v[2] = s[2].mCount;

            tv30.setText("" + s[1].mCount);
            tv31.setText("" + s[1].mPrice);
            p[3] = s[1].mPrice;
            v[3] = s[1].mCount;

            tv40.setText("" + s[0].mCount);
            tv41.setText("" + s[0].mPrice);
            p[4] = s[0].mPrice;
            v[4] = s[0].mCount;

            tv50.setText("" + b[0].mCount);
            tv51.setText("" + b[0].mPrice);
            p[5] = b[0].mPrice;
            v[5] = b[0].mCount;

            tv60.setText("" + b[1].mCount);
            tv61.setText("" + b[1].mPrice);
            p[6] = b[1].mPrice;
            v[6] = b[1].mCount;

            tv70.setText("" + b[2].mCount);
            tv71.setText("" + b[2].mPrice);
            p[7] = b[2].mPrice;
            v[7] = b[2].mCount;

            tv80.setText("" + b[3].mCount);
            tv81.setText("" + b[3].mPrice);
            p[8] = b[3].mPrice;
            v[8] = b[3].mCount;

            tv90.setText("" + b[4].mCount);
            tv91.setText("" + b[4].mPrice);
            p[9] = b[4].mPrice;
            v[9] = b[4].mCount;
        }

    }

    private void initServiceDisplay() {
        Message textMsg = mHandler
                .obtainMessage(MESSAGE_STOCK_SERVER_DISPLAYRUN);
        textMsg.obj = "yes";
        mHandler.sendMessage(textMsg);
    }

    private void initToggleButton(View v) {

        toggleButton = (ToggleButton) v.findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                toggleButton.setChecked(isChecked);

                Message textMsg = mHandler
                        .obtainMessage(MESSAGE_STOCK_SERVER_DISPLAYRUN);
                textMsg.obj = isChecked ? "yes" : "no";
                mHandler.sendMessage(textMsg);
            }

        });

        tb_Simulation = (ToggleButton) v.findViewById(R.id.toggleButton2);

        tb_Simulation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                tb_Simulation.setChecked(isChecked);
                // print("fffff: " + isChecked);
                Message textMsg = mHandler
                        .obtainMessage(MESSAGE_STOCK_SERVER_SIMULATION);
                textMsg.obj = isChecked ? "yes" : "no";
                mHandler.sendMessage(textMsg);
            }

        });

        //tb_Simulation.setChecked(!mStockApplication.isServerSimulation());
        tb_Simulation.setChecked(false);
        boolean a = mStockApplication.isServerDisplayRun();
//        print("------------  " + (a ? "1" : "2"));
        toggleButton.setChecked(a);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getActivity().setContentView(R.layout.stock);

        //ViewUtils.inject(getActivity());

        mStockApplication = (MainApplication) getActivity().getApplication();
        mStockApplication.addObserver(this);

        Intent intent = new Intent(getActivity(), StockService.class);
        // mAPService =
        // print("========onCreate");
        getActivity().startService(intent);

    }

    private Handler mHandler = new Handler() {
        // String ssid;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STOCK_SERVER_DISPLAYRUN:
                    boolean running = (String) (msg.obj) == "yes" ? true : false;
                    mStockApplication.setServerDisplayRun(running);
                    //print("run:" + msg.obj);
                    // scanWifi();
                    break;
                case MESSAGE_STOCK_SERVER_SIMULATION:
                    boolean Simulation = (String) (msg.obj) == "yes" ? true : false;
                    mStockApplication.setServerSimulation(Simulation);
                    //print("simu:" + msg.obj);
                    // scanWifi();
                    break;
                case MESSAGE_STOCK_UPDATE_VIEW:

                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent intent = new Intent(getActivity(), StockService.class);
        // mAPService =
        // print("========onCreate");
        getActivity().stopService(intent);
        print("onDestroy");
        // finish();
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
        mStockApplication.deleteObserver(this);
        if (!Debug) {
            Intent intent = new Intent(getActivity(), StockService.class);
            getActivity().stopService(intent);
        }
        print("onDestroyView");

    }

    @Override
    public void onStart() {
        super.onStart();
        print("event bus is register");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        print("event bus is unregister");
        EventBus.getDefault().unregister(this);
        tb_Simulation.setChecked(false);
        super.onStop();
    }

    // This method will be called when a MessageEvent is posted
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(StockEvent event) {
//        int i;
        float todayp, yestodayp, highp, lowp, nowp;
        long tradec;

        StockInfo.BuyOrSellInfo[] b = event.mStockInfo.getBuyInfo();
        StockInfo.BuyOrSellInfo[] s = event.mStockInfo.getSellInfo();

        // stock_name.setText(event.mStockInfo.getName());
        holder.setChangeValueEmpty();

        todayp = event.mStockInfo.getTodayPrice();
        yestodayp = event.mStockInfo.getYestodayPrice();
        highp = event.mStockInfo.getHighestPrice();
        lowp = event.mStockInfo.getLowestPrice();
        nowp = event.mStockInfo.getNowPrice();
        tradec = event.mStockInfo.getTradeCount();

        preStockInfo = event.mStockInfo;

        holder.displayOtherValue(b, s, todayp, yestodayp, highp, lowp, nowp,
                tradec);

        // Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
        // print("event: " + event.mStockInfo.toString());
    }

    // This method will be called when a SomeOtherEvent is posted
    // public void onEvent(SomeOtherEvent event) {
    // doSomethingWith(event);
    // }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub

    }

}
