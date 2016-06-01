package zj.zfenlly.wifi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.tools.R;


/**
 * Created by Administrator on 2016/5/30.
 */
public class WifiActivity extends Activity {

    @ViewInject(R.id.wifion)
    public Button wifion;
    @ViewInject(R.id.wifioff)
    public Button wifioff;
    @ViewInject(R.id.before30)
    public Button before30;
    @ViewInject(R.id.after30)
    public Button after30;
    @ViewInject(R.id.synctime)
    public Button synctime;
    private WifiAdmin mWifiAdmin = null;

    private boolean screen_landscape = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifitest);
        mWifiAdmin = new WifiAdmin(this);
        ViewUtils.inject(this);

        Log.e("wifi act", "onCreate wifi activity");
    }

    @OnClick(R.id.after30)
    public void after30minites(View v) {

    }

    @OnClick(R.id.synctime)
    public void synctimes(View v) {

    }

    @OnClick(R.id.before30)
    public void before30minites(View v) {

    }

    @OnClick(R.id.wifion)
    public void wifion(View v) {
        mWifiAdmin.openWifi();
        //finish();
        startOtherActivity();
    }

    @OnClick(R.id.wifioff)
    public void wifioff(View v) {
        mWifiAdmin.closeWifi();
        //finish();
        startOtherActivity();
    }

    private void startOtherActivity() {
//        Intent intent = new Intent();
//        intent.setClass(this, MainActivity.class);
//        startActivity(intent);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ComponentName cn = new ComponentName("com.teamlava.castlestory", "com.apportable.activity.VerdeActivity");
        intent.setComponent(cn);
//        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
//        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            screen_landscape = true;
//        }else{
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        super.onResume();
        Log.e("wifi act", "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(screen_landscape){
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //}
        Log.e("wifi act", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mWifiAdmin.openWifi();
        if (mWifiAdmin.isWifiEnabled()) {
            wifion.setVisibility(View.INVISIBLE);
            wifioff.setVisibility(View.VISIBLE);
        } else {
            wifion.setVisibility(View.VISIBLE);
            wifioff.setVisibility(View.INVISIBLE);
        }
        Log.e("wifi act", "onStart wifi activity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("wifi act", "onStop wifi activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("wifi act", "onDestroy wifi activity");
    }
}
