package com.apportable.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.zj.stock.R;

import java.lang.reflect.Method;

public class VerdeActivity extends Activity {
    static NotifySound ns = new NotifySound();
    private static Vibrator vibrator;
    public  static int a;

    public static void doVibrate() {
        long[] pattern = {100, 200, 100, 200, 100, 200};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设
    }

    public static void playSound() {
        ns.play(2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ns.init(this);
        a = 0;
        Log.e("ZTAG", "Z LOOK");
        setContentView(Rfile.content_view);

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent("com.tchip.changeBarHideStatus");
                sendBroadcast(i);
            }
        });
        playSound();
        testreflect();
        vibrateInit();
        doVibrate();
        //test();
        Log.e("TEST", "---" + uuid());
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
        @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        vibrator.cancel();
    }

    public  static void NotifyVibrate() {
        if (a == 0) {
            a = 1;
            doVibrate();
        }
    }

    public void vibrateInit() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void testreflect() {
        try {
            Class<?> threadClazz = Class.forName("com.test.testzj.Rfile");
            Method getTipMethod1 = threadClazz.getDeclaredMethod("yes");
            getTipMethod1.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String uuid() {
        return prefsUuid();
    }

    private String prefsUuid() {
        String a = "";
        return a;
    }
}
