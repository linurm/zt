package zj.zfenlly.gua;

import android.util.Log;

/**
 * Created by Administrator on 2017/8/1.
 */

public final class LoadInjectLib {
    static {
        System.loadLibrary("inject");
    }

    public static void init() {
        int i = injectLib("com.teamlava.castlestory", "libs3e_android.so", "clock_gettime");
        Log.e("Inject", "+" + i);
    }

    public static native int injectLib(String pkgName, String libName, String funcName);

    public static native String addHalfHour(String name);

    public static native String decHalfHour(String name);

    public static native String addHour(String name);

    public static native String decHour(String name);
}
