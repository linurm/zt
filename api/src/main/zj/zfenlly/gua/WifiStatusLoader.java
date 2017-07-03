package zj.zfenlly.gua;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by Administrator on 2016/8/17.
 */
public class WifiStatusLoader {
    private static WifiStatusLoader sInstance;
    private Context mContext;
    private FloatView mFloatView;
    private boolean isStartApp = false;

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

    }


    public void setIsStopAPP() {
        isStartApp = false;
    }


    public void WifiDisableDisplay() {
        if (mFloatView != null) {
            mFloatView.setbClickable(true);
            mFloatView.setImageResource(Rfile.wifi_off);
        }
    }

    public void WifiEnableDisplay() {
        if (mFloatView != null) {
            mFloatView.setbClickable(true);
            mFloatView.setImageResource(Rfile.wifi_on);
        }
    }

    public void StartAPP() {

        if (isStartApp) {
            isStartApp = false;
            startO(0, "0");
        }
    }

    public void startAPP(int id, String v) {
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
