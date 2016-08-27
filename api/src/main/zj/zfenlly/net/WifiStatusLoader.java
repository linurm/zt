package zj.zfenlly.net;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/8/17.
 */
public class WifiStatusLoader {
    private static WifiStatusLoader sInstance;
    private Context mContext;
    private FloatView mFloatView;
    private boolean isStartApp = false;
    private Activity theActivity;

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
        theActivity = act;
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
            OtherAPP.startActivity(theActivity);
        }
    }

}
