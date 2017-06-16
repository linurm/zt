package zj.zfenlly.gua;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnCompoundButtonCheckedChange;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;

import static zj.zfenlly.gua.SystemInfo.CPU_TYPE;


/**
 * Created by Administrator on 2016/5/30.
 */
public class SelectModeActivity extends Activity {
    private static final int FLAG_START_ACTIVITY = 0x2;
    private static final int FLAG_EXIT = 0x1;
    private final String TAG = "SelectModeActivity";
    @ViewInject(R.id.floatwin)
    public CheckBox floatWin;
    @ViewInject(R.id.mTV)
    public TextView mtv;
    @ViewInject(R.id.mIV)
    public ImageView miv;

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
    @ViewInject(R.id.click_interval)
    public EditText cinterval;
    @ViewInject(R.id.click_times)
    public EditText ctimes;

    @ViewInject(R.id.startapp)
    public Button startApp_btn;
    @ViewInject(R.id.autostart)
    public CheckBox autostart;

    String[] sa = null;
    String[] packagelist = null;
    String[] activityname = null;

    private boolean mAutoStart = false;
    private WifiAdmin mWifiAdmin = null;
    private boolean autoMode = false;


    @Override
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

//    private void setAirplaneMode(Context context, int flags) {
//        Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
//        startActivityForResult(intent, flags);
//    }
//
//    //is ok
//    private boolean isAirplaneMode(Context context) {
//        int isAirplaneMode = Settings.System.getInt(context.getContentResolver(),
//                Settings.Global.AIRPLANE_MODE_ON, 0);
//        return (isAirplaneMode == 1) ? true : false;
//    }

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

        if (mWifiAdmin.isWifiEnabled()) {
            wifioff_btn.setVisibility(View.VISIBLE);
            wifion_btn.setVisibility(View.INVISIBLE);
        } else {
            wifioff_btn.setVisibility(View.INVISIBLE);
            wifion_btn.setVisibility(View.VISIBLE);
        }
        if (getMobileNet()) {
            net3gon.setVisibility(View.INVISIBLE);
            net3goff.setBackgroundColor(Color.RED);
            net3goff.setVisibility(View.VISIBLE);
            if (!android.os.Build.MODEL.equals(CPU_TYPE)) {
                setGuaAutoStart(false);
            }
        } else {
            net3gon.setVisibility(View.VISIBLE);

            net3goff.setVisibility(View.INVISIBLE);
        }
        //mAutoStart = autostart.isChecked();
        if (getGuaAutoStart().equals("true")) {
            mAutoStart = true;
            autostart.setChecked(true);
//            startFloatWin();
//            floatWin.setChecked(true);
        } else {
            mAutoStart = false;
            autostart.setChecked(false);

        }
        if (getGuaFloatWin().equals("true")) {
            startFloatWin();
            floatWin.setChecked(true);
        }
        ctimes.setText("" + getTimes());
        cinterval.setText("" + getInterval());
        Log.e(TAG, "start check: " + (mAutoStart ? "true" : "false"));
        Log.e(TAG, "floatwin check: " + (getGuaFloatWin().equals("true") ? "true" : "false"));

    }

    private int getTimes() {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_times", 0);
    }

    private void setTimes(int t) {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_times", t);
        editor.commit();
    }

    private int getInterval() {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("click_interval", 0);
    }

    private void setInterval(int t) {
        SharedPreferences mySharedPreferences = getSharedPreferences("auto_click",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("click_interval", t);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        OtherAPP.setActivity(this);
        try {
            if (getStartAppPkg() == null) {
                return;
            }
            PackageInfo mPackageInfo = getPackageManager().getPackageInfo(getStartAppPkg(), 0);
            Drawable d = mPackageInfo.applicationInfo.loadIcon(getPackageManager());

            miv.setBackground(d);
            mtv.setText(getStartAppPkg());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mAutoStart) {
            openWifiAndStartAPP();
        }
        Log.e(TAG, "onResume");
    }

    public void openWifiAndStartAPP() {
        if (mWifiAdmin.openWifi()) {
            OtherAPP.setWillStartAPP(this);
        } else {
            OtherAPP.startActivity3(this, getStartAppPkg(), getStartAppAct());
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
        OtherAPP.setWillStopAPP(this);
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

    @OnClick(R.id.selectapp)
    public void selectAPP(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PackageManager packageManager = getPackageManager();
        Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);
        //判断是否系统应用：
        //listAllApps.size();
        StringBuilder sb = new StringBuilder();
        StringBuilder pk = new StringBuilder();
        StringBuilder an = new StringBuilder();
        List<Drawable> listdrawable = new ArrayList<Drawable>();
        for (ResolveInfo appInfo : listAllApps) {
            String pkgName = appInfo.activityInfo.packageName;//获取包名
            if (pkgName.equals(getPackageName()))
                continue;
            //根据包名获取PackageInfo mPackageInfo;（需要处理异常）
            try {
                PackageInfo mPackageInfo = getPackageManager().getPackageInfo(pkgName, 0);
                if ((mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                    //第三方应用
                    String a = appInfo.loadLabel(getPackageManager()).toString();
                    sb.append(a);
                    sb.append("#");
                    pk.append(pkgName);
                    pk.append("#");
                    an.append(appInfo.activityInfo.name);
                    an.append("#");
                    Drawable appdrawable = appInfo.activityInfo.loadIcon(getPackageManager());
                    //if (appdrawable == null) {
                    Log.e("zj", "+++++++++++++++" + appdrawable.toString());
                    //}
                    listdrawable.add(appdrawable);
                    Log.e("zj", a + ":" + pkgName + "/" + appInfo.activityInfo.name);
                } else {
                    //系统应用
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            sa = sb.toString().split("#");
            packagelist = pk.toString().split("#");
            activityname = an.toString().split("#");
        }
        BaseAdapter adapter = new AppListAdapter(this, sa, listdrawable);
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO 自动生成的方法存根
                //System.out.println();
                Log.e("zj", packagelist[arg1] + "/" + activityname[arg1]);
                setStartAppPkg(packagelist[arg1]);
                setStartAppAct(activityname[arg1]);
                arg0.dismiss();
            }
        };
        builder.setAdapter(adapter, clickListener);
        builder.setNegativeButton("cancle", null);
        builder.setPositiveButton("ok", null);
        builder.show();
    }

    @OnClick(R.id.appexit)
    public void airplaneModeAndExit(View v) {
        finish();
        stopFloatWin();
    }

    @OnClick(R.id.wifioff)
    public void wifiOff(View v) {
        mWifiAdmin.closeWifi();
        OtherAPP.startActivity3(this, getStartAppPkg(), getStartAppAct());
    }

//    @OnRadioGroupCheckedChange(R.id.myRadioGroup)
//    public void radioGroup(RadioGroup group, int checkedId) {
//        switch (checkedId) {
//            case R.id.myRadio1app:
//                selectId = 1;
//                break;
//            case R.id.myRadio2app:
//                selectId = 2;
//                break;
//            default:
//                selectId = getStartAppNumber();
//                break;
//        }
//        Log.e(TAG, "selectId: " + selectId);
//        setStartAppNumber(selectId);
//    }

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

    String getStartAppPkg() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("pkg", null);
    }

    void setStartAppPkg(String n) {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("pkg", n);
        editor.commit();
    }

    String getStartAppAct() {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        return mySharedPreferences.getString("act", null);
    }

    void setStartAppAct(String n) {
        SharedPreferences mySharedPreferences = getSharedPreferences("gua",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("act", n);
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
        floatWin.setChecked(true);
    }

    private void stopFloatWinAndSet() {
        stopFloatWin();
        setGuaFloatWin(false);
        floatWin.setChecked(false);
    }

    private void stopFloatWin() {
        Intent intent = new Intent(this, FloatWinService.class);
        stopService(intent);
    }
}
