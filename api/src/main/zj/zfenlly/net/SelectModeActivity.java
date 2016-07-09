package zj.zfenlly.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;

import java.lang.reflect.Method;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;


/**
 * Created by Administrator on 2016/5/30.
 */
public class SelectModeActivity extends Activity {
    private static final int FLAG_START_ACTIVITY = 0x2;
    private static final int FLAG_EXIT = 0x1;
    private final String TAG = "SelectModeActivity";
    @ViewInject(R.id.airplane1)
    public Button airplane1_btn;
    @ViewInject(R.id.airplane2)
    public Button airplane2_btn;
    @ViewInject(R.id.appexit)
    public Button exit;
    @ViewInject(R.id.net3gon)
    public Button net3gon;
    @ViewInject(R.id.net3goff)
    public Button net3goff;
    @ViewInject(R.id.wifion)
    public Button wifion_btn;
    @ViewInject(R.id.wifioff)
    public Button wifioff_btn;
    @ViewInject(R.id.wifiok)
    public Button wifiok_btn;
    @ViewInject(R.id.startapp)
    public Button startApp_btn;
    @ViewInject(R.id.autostart)
    public CheckBox autostart;
    private boolean mAutoStart = false;
    private WifiAdmin mWifiAdmin = null;
    private boolean autoMode = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "_______________________________" + requestCode);

        if ((requestCode & FLAG_START_ACTIVITY) == FLAG_START_ACTIVITY) {
            OtherAPP.startOtherActivity(this, mAutoStart);
        } else {
            if (mAutoStart) {
                mWifiAdmin.openWifi();
                OtherAPP.startOtherActivity(this, mAutoStart);
            }
        }
        if ((requestCode & FLAG_EXIT) == FLAG_EXIT) {
            finish();
        }
        Log.i(TAG, "onActivityResult");
    }

    private void setAirplaneMode(Context context, int flags) {
        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
        startActivityForResult(intent, flags);
    }

    //is ok
    private boolean isAirplaneMode(Context context) {
        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0);
        return (isAirplaneMode == 1) ? true : false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            autoMode = intent.getBooleanExtra("auto", false);
            Log.e(TAG, "auto mode");
        }
        setContentView(R.layout.nettest);
        mWifiAdmin = new WifiAdmin(this);
        ViewUtils.inject(this);
        Log.e(TAG, "onCreate wifi activity");
        //finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        //Log.e(TAG, intent.);
        if (intent != null) {
            autoMode = intent.getBooleanExtra("auto", false);
            if (autoMode) {
                Log.e(TAG, "onstart auto mode");
            } else {
                Log.e(TAG, "onstart not auto mode");
            }
        } else {
            autoMode = false;
            Log.e(TAG, "onstart manual mode");
        }
        updateView();
        Log.e(TAG, "onStart wifi activity");
    }

    private void updateView() {
        startApp_btn.setVisibility(View.VISIBLE);
        if (isAirplaneMode(this)) {
            if (autoMode) {
                setAirplaneMode(this, FLAG_START_ACTIVITY | FLAG_EXIT);
            } else {
                airplane1_btn.setVisibility(View.INVISIBLE);
//                exit.setVisibility(View.INVISIBLE);
                airplane2_btn.setVisibility(View.VISIBLE);
            }
        } else {
            if (autoMode) {
                setAirplaneMode(this, FLAG_START_ACTIVITY | FLAG_EXIT);
            } else {
                airplane1_btn.setVisibility(View.VISIBLE);
//                exit.setVisibility(View.VISIBLE);
                airplane2_btn.setVisibility(View.INVISIBLE);
            }
        }
        if (mWifiAdmin.isWifiEnabled()) {
            wifioff_btn.setVisibility(View.VISIBLE);
            wifion_btn.setVisibility(View.INVISIBLE);
            wifiok_btn.setVisibility(View.INVISIBLE);
        } else {
            wifioff_btn.setVisibility(View.INVISIBLE);
            wifion_btn.setVisibility(View.VISIBLE);
            wifiok_btn.setVisibility(View.VISIBLE);
        }
        if (getMobileNet()) {
            net3gon.setVisibility(View.INVISIBLE);
            net3goff.setVisibility(View.VISIBLE);
        } else {
            net3gon.setVisibility(View.VISIBLE);
            net3goff.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop wifi activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy wifi activity");
    }

    private void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try {
            setMobileDataEnabl = connectivityManager.getClass().getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getMobileNet() {
        boolean isMobileDataEnable = false;


        Object[] arg = null;
        try {
            isMobileDataEnable = invokeMethod("getMobileDataEnabled", arg);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isMobileDataEnable;
    }


    public boolean invokeMethod(String methodName,
                                Object[] arg) throws Exception {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Class ownerClass = mConnectivityManager.getClass();
        Class[] argsClass = null;
        if (arg != null) {
            argsClass = new Class[1];
            argsClass[0] = arg.getClass();
        }

        Method method = ownerClass.getMethod(methodName, argsClass);
        Boolean isOpen = (Boolean) method.invoke(mConnectivityManager, arg);
        return isOpen;
    }

    public Object invokeBooleanArgMethod(String methodName,
                                         boolean value) throws Exception {

        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Class ownerClass = mConnectivityManager.getClass();
        Class[] argsClass = new Class[1];
        argsClass[0] = boolean.class;
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(mConnectivityManager, value);
    }

    @OnClick(R.id.net3gon)
    public void net3gon(View v) {
        if (!getMobileNet()) {
            toggleMobileData(this, true);
        }
    }

    @OnClick(R.id.net3goff)
    public void net3goff(View v) {
        if (getMobileNet()) {
            toggleMobileData(this, false);
        }
    }

    @OnClick(R.id.airplane1)
    public void airplaneOff(View v) {
        setAirplaneMode(this, FLAG_START_ACTIVITY | FLAG_EXIT);
    }

    @OnClick(R.id.airplane2)
    public void airplaneOn(View v) {
        setAirplaneMode(this, FLAG_START_ACTIVITY | FLAG_EXIT);
    }

    @OnClick(R.id.appexit)
    public void airplaneModeAndExit(View v) {
        finish();
    }

    @OnClick(R.id.wifioff)
    public void wifiOff(View v) {
        mWifiAdmin.closeWifi();
        OtherAPP.startOtherActivity(this, mAutoStart);
    }

    @OnClick(R.id.startapp)
    public void startApp(View v) {
        mWifiAdmin.openWifi();
        OtherAPP.startOtherActivity(this, mAutoStart);
        //finish();
    }

    @OnClick(R.id.wifion)
    public void wifiOn(View v) {
        mWifiAdmin.openWifi();
        OtherAPP.startOtherActivity(this, mAutoStart);
    }

    @OnClick(R.id.wifiok)
    public void wifiOk(View v) {
        mWifiAdmin.openWifi();
        finish();
    }

    @OnCompoundButtonCheckedChange(R.id.autostart)
    public void autoStart(CompoundButton buttonView,
                          boolean isChecked) {

        mAutoStart = isChecked;

    }
}
