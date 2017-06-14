package zj.zfenlly.gua;

import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/6/14.
 */

public class MZFloatView extends FloatView {

    public int x1, y1;

    public MZFloatView(Context context, LinearLayout mLinearLayout, WindowManager mWindowManager, WindowManager.LayoutParams wmParams) {
        super(context, mLinearLayout, mWindowManager, wmParams);
    }

    @Override
    public void updateViewPosition() {
        if (i++ > 5) {
            //i = 0;
            // 更新浮动窗口位置参数
            windowManagerParams.x = (int) (x - mTouchX);
            windowManagerParams.y = (int) (y - mTouchY);
            this.windowManager.updateViewLayout(this.mLinearLayout, this.windowManagerParams); // 刷新显示
            Log.e("TAG", "UPDATE location");
            final int[] anchorPos = new int[2];
            getLocationOnScreen(anchorPos);

            x1 = anchorPos[0] + (getRight() - getLeft()) / 2;
            y1 = anchorPos[1] + (getBottom() - getTop()) / 2;
        }
    }

}
