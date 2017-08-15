package zj.zfenlly.gua;

import android.os.SystemClock;

/**
 * Created by Administrator on 2017/8/15.
 */

public class SystemTime {

    public static int minites = 0;

    public static void setTime(int v){
        minites += v;
    }

    public static long elapsedRealtime() {
        return SystemClock.elapsedRealtime() + minites * 60 * 1000;
    }//ms

    public static long currentTimeMillis() {
        return System.currentTimeMillis() + minites * 60 * 1000;
    }//ms

    public static long nanoTime()//ns
    {
        return System.nanoTime() + minites * 60 * 1000 * 1000 * 1000;
    }
}
