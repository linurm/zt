package zj.zfenlly.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;
import com.lidroid.xutils.view.annotation.event.OnRadioGroupCheckedChange;

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
    @ViewInject(R.id.floatwin)
    public CheckBox floatWin;
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
    @ViewInject(R.id.myRadioGroup)
    public RadioGroup mRadioGroup;
    @ViewInject(R.id.startapp)
    public Button startApp_btn;
    @ViewInject(R.id.autostart)
    public CheckBox autostart;
    @ViewInject(R.id.myRadio1app)
    public RadioButton mRB1app;
    @ViewInject(R.id.myRadio2app)
    public RadioButton mRB2app;
    private boolean mAutoStart = false;
    private WifiAdmin mWifiAdmin = null;
    private boolean autoMode = false;
    private int selectId = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onTheCreate(savedInstanceState);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "_______________________________" + requestCode);

        if ((requestCode & FLAG_START_ACTIVITY) == FLAG_START_ACTIVITY) {
        } else {
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

    void onTheCreate(Bundle savedInstanceState) {

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
        } else {
            wifioff_btn.setVisibility(View.INVISIBLE);
            wifion_btn.setVisibility(View.VISIBLE);
        }
        if (getMobileNet()) {
            net3gon.setVisibility(View.INVISIBLE);
            net3goff.setVisibility(View.VISIBLE);
        } else {
            net3gon.setVisibility(View.VISIBLE);
            net3goff.setVisibility(View.INVISIBLE);
        }

        //mAutoStart = autostart.isChecked();
        if (getGuaAutoStart().equals("true")) {
            mAutoStart = true;
            autostart.setChecked(true);
            startFloatWinAndSet();
            floatWin.setChecked(true);
        } else {
            mAutoStart = false;
            autostart.setChecked(false);
            if (!getGuaFloatWin().equals("true")) {
                startFloatWinAndSet();
                floatWin.setChecked(true);
            }
        }
        Log.e(TAG, "start check: " + (mAutoStart ? "true" : "false"));

        selectId = getStartAppNumber();

        if (selectId == 1) {
            mRB1app.setChecked(true);
            mRB2app.setChecked(false);
        } else if (selectId == 2) {
            mRB2app.setChecked(true);
            mRB1app.setChecked(false);
        } else {
            mRB1app.setChecked(false);
            mRB2app.setChecked(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAutoStart) {
            openWifiAndStartAPP();
        }
        Log.e(TAG, "onResume");
    }

    public void openWifiAndStartAPP() {
        if (mWifiAdmin.openWifi()) {
            OtherAPP.setWillStartAPP(this, selectId);
        } else {
            Log.e(TAG, "startOtherActivity selectId: " + selectId);
            OtherAPP.startOtherActivity(this, selectId);
        }
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
        stopFloatWin();
    }

    @OnClick(R.id.wifioff)
    public void wifiOff(View v) {
        mWifiAdmin.closeWifi();
        OtherAPP.startOtherActivity(this, selectId);
    }

    @OnRadioGroupCheckedChange(R.id.myRadioGroup)
    public void radioGroup(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.myRadio1app:
                selectId = 1;
                break;
            case R.id.myRadio2app:
                selectId = 2;
                break;
            default:
                selectId = getStartAppNumber();
                break;
        }
        Log.e(TAG, "selectId: " + selectId);
        setStartAppNumber(selectId);
    }

    @OnClick(R.id.startapp)
    public void startApp(View v) {

        //mAutoStart = autostart.isChecked();
        openWifiAndStartAPP();
        //finish();
    }

    @OnClick(R.id.wifion)
    public void wifiOn(View v) {
        openWifiAndStartAPP();
    }


    @OnCompoundButtonCheckedChange(R.id.autostart)
    public void autoStart(CompoundButton buttonView,
                          boolean isChecked) {
        mAutoStart = isChecked;
        Log.e(TAG, "set check: " + (isChecked ? "true" : "false"));
        setGuaAutoStart(mAutoStart);
    }

    int getStartAppNumber() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("app_num", 1);
    }

    void setStartAppNumber(int n) {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("app_num", n);
        editor.commit();
    }

    String getGuaAutoStart() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("autostart", "false");
    }

    void setGuaAutoStart(boolean autostart) {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("autostart", autostart ? "true" : "false");
        editor.putString("reverse", "nouse");
        editor.commit();
    }

    String getGuaFloatWin() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("floatwin", "false");
    }

    void setGuaFloatWin(boolean floatWin) {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("floatwin", floatWin ? "true" : "false");
        editor.putString("reverse", "nouse");
        editor.commit();
    }

    @OnCompoundButtonCheckedChange(R.id.floatwin)
    public void floatWin(CompoundButton buttonView,
                         boolean isChecked) {
        if (isChecked) {
            startFloatWinAndSet();
        } else {
            stopFloatWinAndSet();
        }
    }

    private void startFloatWin() {
        Intent intent = new Intent(this, FloatWinService.class);
        startService(intent);
    }

    private void startFloatWinAndSet() {
        startFloatWin();
        setGuaFloatWin(true);
    }

    private void stopFloatWinAndSet() {
        stopFloatWin();
        setGuaFloatWin(false);
    }

    private void stopFloatWin() {
        Intent intent = new Intent(this, FloatWinService.class);
        stopService(intent);

    }


}
