package com.mine.temprature;

import android.app.Application;
import android.util.Log;

import com.mine.temprature.DaoMaster.DevOpenHelper;

import org.greenrobot.greendao.database.Database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/9/6.
 */

public class App extends Application {
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    public static String readFile(String sys_path) {
        String prop = "waiting";// 默认值
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(sys_path));
            prop = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ZTAG", " ***ERROR*** Here is what I know: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("ZTAG", "readFile cmd from" + sys_path + "data" + " -> prop = " + prop);
        return prop;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DevOpenHelper helper = new DevOpenHelper(this, ENCRYPTED ? "temps-db-encrypted" : "temps-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * get CPU rate
     *
     * @return
     */
    public String getProcessCpuRate() {

        StringBuilder tv = new StringBuilder();
        int rate = 0;

        try {
            String Result;
            Process p;
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");

                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
                    break;
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rate + "%";
    }

    public String getCPUtemprature() {
        String a = "/sys/class/hwmon/hwmon1/device/temp1_input";

        return readFile(a)+"℃";
    }
}
