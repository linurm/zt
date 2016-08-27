package zj.zfenlly.net;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import zj.zfenlly.tools.R;
import zj.zfenlly.wifi.WifiAdmin;

/**
 * Created by Administrator on 2016/8/17.
 */
public class FloatWinService extends Service {

    private static final String TAG = "FloatWinService";
    LinearLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    // ���������������ò��ֲ����Ķ���
    WindowManager mWindowManager;
    Button mFloatView;
    private FloatView floatView = null;
    private BroadcastReceiver wifiReceiver;
    //Button mRecentView;
    private WifiAdmin mWifiAdmin = null;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        //createFloatView();
        // Toast.makeText(FxService.this, "create FxService",
        // Toast.LENGTH_LONG);
        mWifiAdmin = new WifiAdmin(this);
        createView2();


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void createView2() {

        // 获取WindowManager
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplicationContext().WINDOW_SERVICE);
        // 设置LayoutParams(全局变量）相关参数
        wmParams = new WindowManager.LayoutParams();

        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
        wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为：
		 * 下面的flags属性的效果形同“锁定”。
		 * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
		 * LayoutParams.FLAG_NOT_FOCUSABLE  不可聚焦
		 * LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
		 */
        // 调整悬浮窗口至左上角，便于调整坐标
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = 0;
        // 设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 显示myFloatView图像


        floatView = new FloatView(getApplicationContext(), wmParams);
        floatView.setbClickable(true);
        floatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View var1) {

                //floatView.setEnabled(false);
                if (floatView.isbClickable()) {
                    Log.e("click", "click");
                    floatView.setbClickable(false);
                    if (mWifiAdmin.isWifiEnabled()) {
                        mWifiAdmin.closeWifi();
                        Toast.makeText(FloatWinService.this, "close wifi", Toast.LENGTH_LONG).show();
                    } else {
                        mWifiAdmin.openWifi();
                        Toast.makeText(FloatWinService.this, "open wifi", Toast.LENGTH_LONG).show();
                    }
                }
                //return true;
            }

        });

        if (mWifiAdmin.isWifiEnabled()) {
            floatView.setImageResource(R.drawable.wifi_on); // 这里简单的用自带的icon来做演示
        } else {
            floatView.setImageResource(R.drawable.wifi_off);
        }

        final WifiStatusLoader mWifiStatusLoader = WifiStatusLoader.getInstance(this);
        mWifiStatusLoader.setRecentsPanel(floatView);
        mWindowManager.addView(floatView, wmParams);

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //if (mFloatLayout != null) {
        mWindowManager.removeView(floatView);
        //}
    }

}
