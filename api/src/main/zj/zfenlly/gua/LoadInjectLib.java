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
        int i = injectLib("com.teamlava.castlestory");
        Log.e("JTAG", "+" + i);

        Log.e("sTAG", "#     " + SystemTime.nanoTime());
    }

    public static native int injectLib(String pkgName);

    public static native String addHalfHour(String name);

    public static native String decHalfHour(String name);

    public static native String addHour(String name);

    public static native String decHour(String name);

    public static native String setTime(int minite);
}
