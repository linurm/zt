package zj.zfenlly.gua;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import static zj.zfenlly.gua.FloatWinService.dip2px;
import static zj.zfenlly.gua.SystemInfo.CPU_TYPE;

/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatView extends ImageView {
    public static int i = 0;
    private Context mContext = null;
    public float mTouchX;
    public float mTouchY;
    public float x;
    public float y;
    private Long t_down;
    private Long t_up;
    private float mStartX;
    private float mStartY;
    private boolean bClickable;
    private OnClickListener mClickListener;
    public LinearLayout mLinearLayout;
    public WindowManager windowManager = null;
    // 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性
    public WindowManager.LayoutParams windowManagerParams;



    public FloatView(Context context, LinearLayout mLinearLayout, WindowManager mWindowManager, WindowManager.LayoutParams wmParams) {
        super(context);
        mContext = context;
        this.windowManager = mWindowManager;
        this.mLinearLayout = mLinearLayout;
        this.windowManagerParams = wmParams;
    }

    public boolean isbClickable() {
        return bClickable;
    }

    public void setbClickable(boolean bClickable) {
        this.bClickable = bClickable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取到状态栏的高度
        Rect frame = new Rect();
        getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;// - 48;
        //System.out.println("statusBarHeight:" + statusBarHeight);
        // 获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - statusBarHeight;// - 200; // statusBarHeight是系统状态栏的高度
        if (android.os.Build.MODEL.equals(CPU_TYPE)) {
            y = y - dip2px(mContext, 80);
            x = x - dip2px(mContext, 80);
        }
//        Log.i("tag", "rawXY " + event.getRawX() + "==== Y " + event.getRawY());
//        Log.i("tag", "getXY:" + event.getX() + ":" + event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                // 获取相对View的坐标，即以此View左上角为原点
                mTouchX = event.getX();
                mTouchY = event.getY();
                mStartX = x;
                mStartY = y;
                i = 0;
                t_down = System.currentTimeMillis();
//                Log.e("tag", "frame:" + frame.toString());
//                Log.e("tag", "down:[ " + x + ", "
//                        + y + "] t_down " + t_down);
                break;
            case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
//                mTouchX = event.getX();
//                mTouchY = event.getY();
//                Log.e("tag", "move:[ " + x + ", "
//                        + y + "]" + " [ " + event.getX() + ", "
//                        + event.getY() + "]");
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                updateViewPosition();
                mTouchX = mTouchY = 0;
                t_up = System.currentTimeMillis();
                //if(t_up-t_down>)
//                Log.e("tag", "up:[" + (x - mStartX) + ", "
//                        + (y - mStartY) + "] t_up " + "  " + (t_up - t_down));
                float h = x - mStartX;
                float v = y - mStartY;
                if (h < 10 && v < 10 && h > -10 && v > -10) {//move little
                    Long t = t_up - t_down;
                    if (t > 50 && t < 500) {//time match
                        if (mClickListener != null) {
                            mClickListener.onClick(this);
                        }
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

    public void updateViewPosition() {
        if (i++ > 5) {
            //i = 0;
            // 更新浮动窗口位置参数
            windowManagerParams.x = (int) (x - mTouchX);
            windowManagerParams.y = (int) (y - mTouchY);
            this.windowManager.updateViewLayout(this.mLinearLayout, this.windowManagerParams); // 刷新显示
        }
    }

}
