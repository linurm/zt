package com.apportable.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.adjust.win.FloatWindowView;
import com.iflytek.sunflower.FlowerCollector;
import com.iflytek.voicedemo.AsrDemo;
import com.iflytek.voicedemo.IatDemo;
import com.iflytek.voicedemo.TtsDemo;
import com.iflytek.voicedemo.UnderstanderDemo;
import com.zj.stock.STApplication;

import java.io.File;
import java.lang.reflect.Method;

public class VerdeActivity extends Activity {
    public static int a;
    static NotifySound ns = new NotifySound();
    private static Vibrator vibrator;
    public int startMode = 0;
    Context mContext = null;

    public static void doVibrate() {
        long[] pattern = {100, 200, 100, 200, 100, 200};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设
    }

    public static void playSound() {
        ns.play(2);
    }

    public static void NotifyVibrate() {
        if (a == 0) {
            a = 1;
            doVibrate();
        }
    }

    public static void zLog(String a) {
        Log.e("ZTAG", "" + a);
    }

    public static void zLog(File a) {
        Log.e("ZTAG", a.getAbsolutePath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = VerdeActivity.this;
        ((STApplication) getApplication()).setActivity(this);
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
//        a = 0;
//        Log.e("ZJTAG", "Z LOOK" + this);
//        Log.e("ZJTAG", "Z LOOK" + getApplication());
//        Log.e("ZJTAG", "Z LOOK" + getApplicationContext());
        setContentView(Rfile.content_view);

        //voice iat
        Button iat = (Button) findViewById(Rfile.iat_button);
        iat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, IatDemo.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        //voice tts
        Button tts = (Button) findViewById(Rfile.tts_button);
        tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TtsDemo.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        //voice asr
        Button asr = (Button) findViewById(Rfile.asr_button);
        asr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AsrDemo.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
//        voice understand
        Button und = (Button) findViewById(Rfile.und_button);
        und.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, UnderstanderDemo.class);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        Button btn = (Button) findViewById(Rfile.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent("com.tchip.changeBarHideStatus");
//                Intent i = new Intent("zj.zfenlly.gua.vpn");
//                sendBroadcast(i);
                startGuaApp();

            }
        });
        final FloatWindowView wv = new FloatWindowView(mContext);
        wv.init(VerdeActivity.this);
        Button win_btn = (Button) findViewById(Rfile.win_button);
        win_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wv.isSeekBarOnView == false) {
//                    wv.startSeekBar(219, 636, 350, 40);
                    wv.startSeekBar(236, 636, 320, 40, 100, 1);
                } else {
                    wv.stopSeekBar();
                }
            }
        });

        wv.startSeekBar(236, 636, 320, 40, 100, 1);
//        SeekBar sb = (SeekBar) findViewById(Rfile.light_seekbar);
//        try {
//            float f = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f;
//            int l = (int) (f * 100);
////            Log.e("ZTAG",""+l);
//            sb.setProgress(l);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                adjustBackgroundLight(i);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
        playSound();
        testreflect();
        vibrateInit();
        //doVibrate();
        test();
        Log.e("ZTAG", "---" + uuid());
    }

    public void getStartMode() {
        Intent intent = getIntent();
        String a = intent.getAction();
        if (a.equals(Intent.ACTION_VIEW)) {
            //start vpn
//            Log.e("ZTAG", "onResume:" + intent.toString());
            startMode = 1;
        } else if (a.equals(Intent.ACTION_MAIN)) {
//            Log.e("ZTAG", "onResume1:" + intent.toString());
            startMode = 2;
        }
    }

    public void startGuaApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String packageName = "zj.zfenlly.tools";
        String className = "zj.zfenlly.gua.SelectModeActivity";
        intent.setClassName(packageName, className);
        Log.e("ZTAG", "" + startMode);
        if (startMode == 1)
            startActivity(intent);
    }

    public void doSeek(int i) {
        adjustBackgroundLight(i);
    }

    public void adjustBackgroundLight(int i) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (i == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (i <= 0 ? 1 : i) / 255f;
        }
        Log.e("ZTAG", " " + lp.screenBrightness);
        window.setAttributes(lp);
    }

    private void test() {
        File a = new File("abc.txt");
        zLog(a);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FlowerCollector.onPause(this);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        FlowerCollector.onResume(this);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        Log.e("ZTAG", "" + getFilePath());

        Log.e("ZTAG", Build.VERSION.INCREMENTAL);//V8.5.3.0.MBGCNED
        Log.e("ZTAG", Build.MANUFACTURER);//Xiaomi
        Log.e("ZTAG", Build.MODEL);//MI 5s Plus
        Log.e("ZTAG", Build.CPU_ABI);//arm64-v8a
        Log.e("ZTAG", Build.TAGS);//release-keys
        Log.e("ZTAG", Build.PRODUCT);//natrium
        Log.e("ZTAG", Build.VERSION.RELEASE);//release-keys
        Log.e("ZTAG", Build.DEVICE);//natrium
        Log.e("ZTAG", Build.ID);//MXB48T
        Log.e("ZTAG", Build.TYPE);//user

        //07-18 15:40:38.540 15348-15348/? E/ZTAG: V8.5.3.0.MBGCNED
        //07-18 15:40:38.540 15348-15348/? E/ZTAG: Xiaomi
        //07-18 15:40:38.540 15348-15348/? E/ZTAG: MI 5s Plus
        //07-18 15:40:38.540 15348-15348/? E/ZTAG: arm64-v8a
        // 07-18 15:40:38.540 15348-15348/? E/ZTAG: release-keys
        // 07-18 15:40:38.540 15348-15348/? E/ZTAG: natrium
        // 07-18 15:40:38.540 15348-15348/? E/ZTAG: 6.0.1
        // 07-18 15:40:38.540 15348-15348/? E/ZTAG: natrium
        //
//        Log.e("ZTAG", Build.VERSION.RELEASE);

        getStartMode();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        vibrator.cancel();
    }

    private StringBuilder getFilePath() {

        StringBuilder filePathBuilder = new StringBuilder();

        // 返回通过Context.openFileOutput()创建和存储的文件系统的绝对路径，应用程序文件，这些文件会在程序被卸载的时候全部删掉。

        filePathBuilder.append("getFilesDir == ").append(getFilesDir()).append("\n");

        // 返回应用程序指定的缓存目录，这些文件在设备内存不足时会优先被删除掉，所以存放在这里的文件是没有任何保障的，可能会随时丢掉。

        filePathBuilder.append("getCacheDir == ").append(getCacheDir()).append("\n");

        // 这是一个可以存放你自己应用程序自定义的文件，你可以通过该方法返回的File实例来创建或者访问这个目录，注意该目录下的文件只有你自己的程序可以访问。

        filePathBuilder.append("getDir == ").append(getDir("test.txt", Context.MODE_WORLD_WRITEABLE)).append("\n");

    /* 需要写文件权限 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> */

        // 调用该方法会返回应用程序的外部文件系统（Environment.getExternalStorageDirectory()）目录的绝对路径，它是用来存放应用的缓存文件，它和getCacheDir目录一样，目录下的文件都会在程序被卸载的时候被清除掉。

        filePathBuilder.append("getExternalCacheDir == ").append(getExternalCacheDir()).append("\n");

        // 这个目录是与应用程序相关的外部文件系统，它和getExternalCacheDir不一样的是只要应用程序存在它就会一直存在，这些文件只属于你的应用，不能被其它人访问。同样，这个目录下的文件在程序被卸载时也会被一同删除。

        filePathBuilder.append("getExternalFilesDir == ").append(getExternalFilesDir("/")).append("\n");

        /**

         * 和上面的方法一样，只是返回的是其目录下某一类型的文件，这些类型可以是：

         * Environment#DIRECTORY_MUSIC 音乐

         * Environment#DIRECTORY_PODCASTS 音频

         * Environment#DIRECTORY_RINGTONES 铃声

         * Environment#DIRECTORY_ALARMS 闹铃

         * Environment#DIRECTORY_NOTIFICATIONS 通知铃声

         * Environment#DIRECTORY_PICTURES 图片

         * Environment#DIRECTORY_MOVIES 视频

         *

         * */

        filePathBuilder.append("getExternalFilesDir == ").append(getExternalFilesDir(Environment.DIRECTORY_PICTURES)).append("\n");

        // 保存通过Context.openOrCreateDatabase 创建的数据库文件

        filePathBuilder.append("getDatabasePath == ").append(getDatabasePath("DATA_BASE_NAME")).append("\n");

        // 返回android 安装包的完整路径，这个包是一个zip的压缩文件，它包括应用程序的代码和assets文件

        filePathBuilder.append("getPackageCodePath == ").append(getPackageCodePath()).append("\n");

        // 返回android 安装包的完整路径，这个包是一个ZIP的要锁文件，它包括应用程序的私有资源。

        filePathBuilder.append("getPackageResourcePath == ").append(getPackageResourcePath()).append("\n");

        // 返回应用程序的OBB文件目录（如果有的话），注意如果该应用程序没有任何OBB文件，这个目录是不存在的。

        filePathBuilder.append("getObbDir == ").append(getObbDir()).append("\n");

        return filePathBuilder;

    }

    public void vibrateInit() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void testreflect() {
        try {
            Class<?> threadClazz = Class.forName("com.apportable.activity.Rfile");
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
