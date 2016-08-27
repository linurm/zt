package zj.zfenlly.net;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatView extends ImageView {
    private static int i = 0;
    private float mTouchX;
    private float mTouchY;
    private float x;
    private float y;
    private float mStartX;
    private float mStartY;

    public boolean isbClickable() {
        return bClickable;
    }

    public void setbClickable(boolean bClickable) {
        this.bClickable = bClickable;
    }

    private boolean bClickable;
    private OnClickListener mClickListener;
    private WindowManager windowManager = (WindowManager) getContext()
            .getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    // 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性
    private WindowManager.LayoutParams windowManagerParams;

    public FloatView(Context context, WindowManager.LayoutParams wmParams) {
        super(context);
        windowManagerParams = wmParams;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到状态栏的高度
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top - 48;
        //System.out.println("statusBarHeight:" + statusBarHeight);
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight; // statusBarHeight是系统状态栏的高度
        Log.i("tag", "currX " + x + "==== currY " + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                i = 0;
                Log.e("tag", "frame:" + frame.toString());
                Log.e("tag", "down:[ " + x + ", "
                        + y + "]");
                break;

            case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                Log.e("tag", "move:[ " + x + ", "
                        + y + "]" + " [ " + event.getX() + ", "
                        + event.getY() + "]");
                updateViewPosition();
                break;

            case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                updateViewPosition();
                mTouchX = mTouchY = 0;
                Log.e("tag", "up:[" + (x - mStartX) + ", "
                        + (y - mStartY) + "]");
                if ((x - mStartX) < 5 && (y - mStartY) < 5) {
                    if (mClickListener != null) {
                        mClickListener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mClickListener = l;
    }

    private void updateViewPosition() {
        if (i++ > 5) {
            //i = 0;
            // 更新浮动窗口位置参数
            windowManagerParams.x = (int) (x - mTouchX);
            windowManagerParams.y = (int) (y - mTouchY);
            windowManager.updateViewLayout(this, windowManagerParams); // 刷新显示
        }
    }

}
