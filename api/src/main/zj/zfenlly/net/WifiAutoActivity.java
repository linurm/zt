package zj.zfenlly.net;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Administrator on 2016/5/30.
 */
public class WifiAutoActivity extends Activity {

    private final String TAG = "zwifi auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();
        //设置传递方向
        intent.setClass(this, AirPlaneModeActivity.class);
//绑定数据
        intent.putExtra("auto", true);

        this.startActivity(intent);
        Log.e(TAG, "onCreate");
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
        //mWifiAdmin.closeWifi();
        //OtherAPP.startOtherActivity(this);
        //finish();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if(screen_landscape){
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //}
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //mWifiAdmin.openWifi();

        Log.e(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

}
