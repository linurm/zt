package zj.zfenlly.gua;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import zj.zfenlly.gua.activity.Vpn;


/**
 * Created by Administrator on 2016/8/17.
 */
public class WifiStatusLoader {
    private static WifiStatusLoader sInstance;

    private Context mContext;
    private FloatView mFloatView;
    private boolean isStartApp = false;
    private boolean isStartVpn = false;

    private WifiStatusLoader(Context context) {
        mContext = context;
    }

    public static WifiStatusLoader getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WifiStatusLoader(context);
        }
        return sInstance;
    }

    public boolean isStartVpn() {
        return isStartVpn;
    }

    public void setStartVpn(boolean startVpn) {
        isStartVpn = startVpn;
    }

    public void setRecentsPanel(FloatView fv) {
        mFloatView = fv;
    }

    public void setIsStartAPP() {
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
        if (isStartVpn) {
            isStartVpn = false;
            startVpn();
        } else {
            if (isStartApp) {
                isStartApp = false;
                startO(0, 0);
            }
        }
    }

    public void startAPP(int id, int v) {
        startO(id, v);
    }

    public void startVpn() {

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(Vpn.vpnPkg, Vpn.vpnAct));
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

//        OtherAPP.startActivity3(mContext, Vpn.vpnPkg, Vpn.vpnAct);
    }

    public void startO(int id, int v) {
        Intent intent = new Intent(mContext, OverActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("key", id);
        bundle.putInt("value", v);
        intent.putExtras(bundle);
        intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
