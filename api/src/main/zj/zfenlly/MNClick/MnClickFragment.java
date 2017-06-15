package zj.zfenlly.MNClick;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.main.BaseFragment;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2017/4/12.
 */
@SuppressLint("ValidFragment")
public class MnClickFragment extends BaseFragment {
    private static Handler messageHandler;
    private final String TAG = this.getClass().getName();
    //    @ViewInject(R.id.ButtonIME)
//    public Button ButtonIME;
    @ViewInject(R.id.EditTextIME)
    public EditText EditTextIME;
    @ViewInject(R.id.button_test)
    public Button b3;
    private int mColorRes = -1;

    public MnClickFragment() {
        this(R.color.white, "MnClick");
    }

    public MnClickFragment(int colorRes, String name) {
        super(name, false);
        mColorRes = colorRes;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @OnClick(R.id.button_test)
    public void Button3(View v) {
        Log.e("TEST", "THIS IS BUTTON 3");
    }

    @OnClick(R.id.ButtonIME)
    public void ButtonIME(View v) {
        //EditTextIME.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, EditTextIME.getLeft() + 5, EditTextIME.getTop() + 5, 0));
        //EditTextIME.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, EditTextIME.getLeft() + 5, EditTextIME.getTop() + 5, 0));
        new Thread() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(1000); //1秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
//                Message message = Message.obtain();
//                message.what = 1;
//                messageHandler.sendMessage(message);
                Instrumentation mInst = new Instrumentation();
                long downTime;
                long eventTime;
                int x = 0, y = 0;
                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();

                    int[] location = new int[2];
                    b3.getLocationOnScreen(location);
                    x = location[0];// + 20;
                    y = location[1];// + 20;
                    x = x + /*dip2px(getActivity(),*/ (b3.getRight() - b3.getLeft()) / 2;
                    y = y + /*dip2px(getActivity(),*/ (b3.getBottom() - b3.getTop()) / 2;
                    //x = dip2px(getActivity(), b3.getLeft() + 5);
                    //y = dip2px(getActivity(), b3.getTop() + 5);
//                    Log.e("instrument", "" + b3.getRa + " : " + y);
//                    Log.e("instrument", "" + dip2px(getActivity(), b3.getLeft()) + ":" + dip2px(getActivity(), b3.getTop()));
                    Log.e("instrument", "图片各个角Left：" + b3.getLeft() + " Right：" + b3.getRight() + " Top：" + b3.getTop() + " Bottom：" + b3.getBottom());
                    MotionEvent me1 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, x, y, 0);

                    mInst.sendPointerSync(me1);
                } catch (Exception e) {
                }
                synchronized (this) {
                    try {
                        wait(5000); //1秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                try {
                    downTime = SystemClock.uptimeMillis();
                    eventTime = SystemClock.uptimeMillis();
                    MotionEvent me2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
                    mInst.sendPointerSync(me2);
                    Log.e("instrument", "send pointersync " + x + ":" + y);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");

        int color = getResources().getColor(mColorRes);
        View view = inflater.inflate(R.layout.fragment_mnclick, container, false);
        view.setBackgroundColor(color);

        ViewUtils.inject(this, view);
        Log.e("TEST", "onCreateView");
        //init_key();
        return view;
    }

    public void init_key() {
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
        //此处的作用是延迟1秒，然后激活点击事件
        //欢迎转载并说明转自：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2013/0111/799.html

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        print("onDestroyView");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mColorRes", mColorRes);
    }

    class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 1:
//                    //模拟点击按钮
//                    bt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, et.getLeft()+5, et.getTop()+5, 0));
//                    bt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, et.getLeft()+5, et.getTop()+5, 0));
                    Instrumentation mInst = new Instrumentation();
                    mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, EditTextIME.getLeft() + 5, EditTextIME.getTop() + 5, 0));
                    mInst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, EditTextIME.getLeft() + 5, EditTextIME.getTop() + 5, 0));

                    //以下代码模拟点击文本编辑框
                    //et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, et.getLeft()+5, et.getTop()+5, 0));
                    //et.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, et.getLeft()+5, et.getTop()+5, 0));
                    break;
                default:
                    break;
            }

        }
    }

}
