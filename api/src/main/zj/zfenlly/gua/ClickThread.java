package zj.zfenlly.gua;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/8/3.
 */

public class ClickThread extends Thread {

    public Handler mHandler;
    //        int x, y;
    int times;// = TimeSetting.getTimes(mContext);
    int interval;// = TimeSetting.getInterval(mContext) * 100;
    Instrumentation mInst = new Instrumentation();
    long downTime;
    long eventTime;
    boolean isStart = false;
    private Context mContext;
    private MZFloatView mCoordinateView;

    public ClickThread(Context context, Handler handler, MZFloatView coordinateView,int t,int inter) {
        mContext = context;
        mHandler = handler;
        times = t;//TimeSetting.getTimes(mContext);
        interval = inter;//TimeSetting.getInterval(mContext) * 100;
        mCoordinateView = coordinateView;
    }


    public void setTempTimes(int t) {
        times = t;
    }

    public void stopThread() {
        isStart = false;
        if (isAlive()) {
            interrupt();
        }
    }

    @Override
    public void run() {
        isStart = true;
        if (mHandler != null)
            mHandler.sendMessage(mHandler.obtainMessage(
                    FloatWinService.SET_VIEW_STOP, null));
        while (times-- > 0 && isStart) {

            try {
                downTime = SystemClock.uptimeMillis();
                eventTime = SystemClock.uptimeMillis();
                int x_zb = 1360;
                int y_zb = 996;

                if (mCoordinateView != null) {
                    x_zb = mCoordinateView.x1;
                    y_zb = mCoordinateView.y1;
                }
                MotionEvent me1 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x_zb, y_zb, 0);
                mInst.sendPointerSync(me1);
            } catch (Exception e) {
            }
            synchronized (this) {
                try {
                    wait(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (isStart == false) break;
            try {
                downTime = SystemClock.uptimeMillis();
                eventTime = SystemClock.uptimeMillis();

                int x_zb = 1360;
                int y_zb = 996;

                if (mCoordinateView != null) {
                    x_zb = mCoordinateView.x1;
                    y_zb = mCoordinateView.y1;
                }
                MotionEvent me2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x_zb, y_zb, 0);
                mInst.sendPointerSync(me2);
                Log.e("instrument", "send pointersync " + x_zb + ":" + y_zb);
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (this) {
                try {
                    wait(interval); //1秒
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (isStart == false) break;
        }
        isStart = false;
        if (mHandler != null)
            mHandler.sendMessage(mHandler.obtainMessage(
                    FloatWinService.SET_VIEW_START, null));
    }
}
