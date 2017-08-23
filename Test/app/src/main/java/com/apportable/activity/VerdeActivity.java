package com.apportable.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[4];
        int v = (int) (num);
        for (int ix = 0; ix < 4; ++ix) {
            int offset = (ix) * 8;
            byteNum[ix] = (byte) ((v >> offset) & 0xff);
        }
        return byteNum;
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
        final EditText et = (EditText) findViewById(Rfile.edit_Text);
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
        final TextView key_code = (TextView) findViewById(Rfile.key_code);
        Button btn = (Button) findViewById(Rfile.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent("com.tchip.changeBarHideStatus");
//                Intent i = new Intent("zj.zfenlly.gua.vpn");
//                sendBroadcast(i);
                ;//startGuaApp();
                String s = startRegister(et.getText().toString());
                key_code.setText(s);

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

        //Log.e("ZTAG", "---" + uuid());
    }

    public String startRegister(String mSR) {
        String v11 = mSR;
        int i = 0, j = 0, k = 0, l = 0;
        int z;
        byte[] s = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] s2 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        byte[] aa1 = {0x2b, 0x2f, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39,
                0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a,
                0x61, 0x62, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x6b, 0x6c, 0x6d, 0x6e, 0x6f, 0x70, 0x71, 0x72, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a
        };
        byte[] aa2 = {0x3e, 0x3f, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d,
                0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19,
                0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32, 0x33
        };
        byte[] a2 = new byte[27];
        int v5 = 0;
        int index = 0;
        int[] v12 = {0, 0, 0, 0, 0};
        int[] v13 = {0, 0, 0, 0, 0};
        int v3 = v11.length();
        int found = 0;
        long v7;
        long lastv = 0;
        if ((v11.length()) > 20)
            return "";
        do {
            index = v5 % v3;
            Log.e("ZTAG", "c " + v11.charAt(index));
            v7 = v11.charAt(index) * (v5 + 20160126) * v3;
            v7 = ((((lastv >> 8) & 0X00FFFFFF) + (v7)));

            byte[] lb = long2Bytes(v7);
            s[v5] = (lb[0]);
            s[v5 + 1] = lb[1];
            s[v5 + 2] = lb[2];
            s[v5 + 3] = lb[3];
            lastv = v7;

            v5++;
        } while (v5 != 16);

        v5 = 0;
        do {
            int x = (int) s[v5 * 4] & 0xff;
            x += ((int) (s[v5 * 4 + 1]) << 8) & 0xff00;
            x += ((int) (s[v5 * 4 + 2]) << 16) & 0xff0000;
            x += ((int) (s[v5 * 4 + 3]) << 24) & 0xff000000;
            v12[v5] = x / 10;
            v5++;
        } while (v5 != 5);

        v13[4] = v12[0] + v12[1];
        v13[2] = v13[4] + v12[0];
        v13[3] = v12[2] + v12[3];
        v13[0] = 2 * v12[2] + v12[3];
        v13[1] = 3 * v12[2] - v12[4];//<=

        byte[] a1 = new byte[21];

        for (i = 0; i < 20; i++) {
            a1[i] = (byte) (v13[i / 4] >> (i % 4) * 8);
        }
        a1[20] = 0;
        Log.e("ZTAG", "[18]: " + a1[18] + "[19]: " + a1[19]);
        for (i = 0; i < 64; i++) {
            for (j = 0; j < 64; j++) {

                for (k = 0; k < 64; k++) {
                    byte xx = (byte) ((aa2[i] << 2) & 0xfc);
                    xx = (byte) ((((aa2[j] >> 4) & 0x0f) | xx) & 0xff);
                    if (xx == (a1[18])) {
                        byte yy = (byte) ((((aa2[k] >> 2) & 0x3f) | ((aa2[j] << 4) & 0xf0)) & 0xff);
                        if (yy == a1[19]) {
                            Log.e("ZTAG", " i= " + aa1[i] + " j= " + aa1[j] + " k= " + aa1[k]);
                            a2[24] = aa1[i];
                            a2[25] = aa1[j];
                            a2[26] = aa1[k];
                        }
                    }
                }
            }
        }
        for (z = 0; z < 6; z++) {
            found = 0;
            for (i = 0; i < 64; i++) {
                for (j = 0; j < 64; j++) {
                    if ((((aa2[j] >> 4) & 0x0f | ((aa2[i] << 2) & 0xfc) & 0xff) == (a1[3 * z] & 0xff))) {
                        for (k = 0; k < 64; k++) {
                            if (((((aa2[k] >> 2) & 0x3f) | ((aa2[j] << 4) & 0xf0) & 0xff) == (a1[3 * z + 1] & 0xff))) {
                                for (l = 0; l < 64; l++) {
                                    if (((((aa2[k] << 6) & 0xc0) | aa2[l]) & 0xff) == (a1[3 * z + 2] & 0xff)) {
//                                        Log.e("ZTAG", "i= " + aa2[i] + " j= " + aa2[j] + " k= " + aa2[k] + " m= " + aa2[l]);
                                        Log.e("ZTAG", "value i= " + aa1[i] + " j= " + aa1[j] + " k= " + aa1[k] + " m= " + aa1[l]);
                                        a2[4 * z] = aa1[i];
                                        a2[4 * z + 1] = aa1[j];
                                        a2[4 * z + 2] = aa1[k];
                                        a2[4 * z + 3] = aa1[l];
                                        found = 1;
                                        break;
                                    }
                                }

                            }
                            if (found == 1)
                                break;
                        }

                    }
                    if (found == 1)
                        break;
                }
                if (found == 1)
                    break;
            }
        }


//        String m = "";
//        try {
//            m = new String(s, "UTF-8");
//        } catch (Exception e) {
//
//        }
        Log.e("ZTAG", "key " + new String(a2));
        return new String(a2);
    }

    public String encodeHex(long integer) {
        StringBuffer buf = new StringBuffer(0);
        if ((integer & 0xff000000) < 0x10000000) {
            buf.append("0");
        }
        Log.e("ZTAG", "1 " + buf);
        buf.append(Long.toString((integer /*& 0xff000000 >> 24*/), 16));
        Log.e("ZTAG", "2 " + buf);
//        if (((long) integer & 0xff) < 0x10) {
//            buf.append("0");
//        }
//        Log.e("ZTAG", "3 " + buf);
//        buf.append(Long.toString((integer & 0xff0000 >> 16), 16));
//        Log.e("ZTAG", "4 " + buf);
//        if (((long) integer & 0xff) < 0x10) {
//            buf.append("0");
//        }
//        Log.e("ZTAG", "5 " + buf);
//        buf.append(Long.toString((integer & 0xff00 >> 8), 16));
//        Log.e("ZTAG", "6 " + buf);
//        if (((long) integer & 0xff) < 0x10) {
//            buf.append("0");
//        }
//        Log.e("ZTAG", "7 " + buf);
//        buf.append(Long.toString(integer & 0xff, 16));
//        Log.e("ZTAG", "8 " + buf);
        return buf.toString();
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
//        startRegister("asfvjurr3578");
//        startRegister("asdfghjkl");
//        startRegister("abcdefghijkl");
        //getStartMode();
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
