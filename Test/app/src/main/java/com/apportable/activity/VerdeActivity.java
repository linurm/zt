package com.apportable.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import java.lang.reflect.Method;

public class VerdeActivity extends Activity {
    static NotifySound ns = new NotifySound();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ns.init(this);
        Log.e("ZTAG", "Z LOOK");
        setContentView(Rfile.content_view);
        playSound();
        testreflect();
        vibrateInit();
        doVibrate();
        Log.e("TEST", "---" + uuid());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        vibrator.cancel();
    }

    public static void doVibrate(){
        long [] pattern = {100,200,100,200,100,200};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设
    }

    public void vibrateInit(){
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

    }
    private static Vibrator vibrator;
    public void testreflect(){
        try {
            Class<?> threadClazz = Class.forName("com.test.testzj.Rfile");
            Method getTipMethod1 = threadClazz.getDeclaredMethod("yes");
            getTipMethod1.invoke(null);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void playSound() {
        ns.play(2);
    }

    public String uuid() {
        return prefsUuid();
    }

    private String prefsUuid() {
        String a = "";

        return a;
    }
}
