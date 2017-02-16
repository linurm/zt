package zj.zfenlly.net;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/8/17.
 */
public class WifiStatusLoader {
    private static WifiStatusLoader sInstance;
    private Context mContext;
    private FloatView mFloatView;
    private boolean isStartApp = false;
    //private Activity theActivity;
    private int selectId = 1;

    private WifiStatusLoader(Context context) {
        mContext = context;
    }

    public static WifiStatusLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WifiStatusLoader(context);
        }
        return sInstance;
    }

    public void setRecentsPanel(FloatView fv) {
        mFloatView = fv;
    }

    public void setIsStartAPP(Activity act) {
        isStartApp = true;
        //theActivity = act;
        //selectId = id;
    }

//    public void setActivity(Activity act) {
//       // theActivity = act;
//    }

    public void setIsStopAPP() {
        isStartApp = false;
    }


    public void WifiDisableDisplay() {
        if (mFloatView != null) {
            mFloatView.setbClickable(true);
            mFloatView.setImageResource(R.drawable.wifi_off);
            //mFloatView.setEnabled(true);
        }
    }

    public void WifiEnableDisplay() {
        if (mFloatView != null) {
            mFloatView.setbClickable(true);
            mFloatView.setImageResource(R.drawable.wifi_on);
            //mFloatView.setEnabled(true);
            //mFloatView.setClickable(true);
        }

    }

    public void StartAPP() {

        if (isStartApp) {
            isStartApp = false;
            //OtherAPP.startActivity2(theActivity, selectId);
            startO(0, "0");
        }
    }

    public void startAPP(int id, String v) {
        //OtherAPP.startActivity2(theActivity, id);
        startO(id, v);
    }

    public void startO(int id, String v) {
        Intent intent = new Intent(mContext, OverActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("key", id);
        bundle.putString("value", v);
        intent.putExtras(bundle);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
