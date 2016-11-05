package zj.zfenlly.arc;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import zj.zfenlly.camera.CameraJni;
import zj.zfenlly.other.Name;
import zj.zfenlly.tools.R;

/**
 * Created by Administrator on 2016/9/27.
 */
@SuppressLint("ValidFragment")
public class ArcFragment extends Fragment implements Name {
    private static String soName = "libjiagu";
    private final String TAG = this.getClass().getName();
    public String mName;
    public ArcView mArcView;
    @ViewInject(R.id.add_button)
    public Button add_bt;
    @ViewInject(R.id.dec_button)
    public Button dec_bt;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private int mColorRes = -1;

    public ArcFragment() {
        this(R.color.white, "color");
    }

    public ArcFragment(int colorRes, String name) {
        mColorRes = colorRes;
        setName(name);
        setRetainInstance(true);
    }

    public static void attactContext() throws Exception {
        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = activityThreadClass.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThread = currentActivityThreadField.get(null);

        Field mInstrumentationField = null;
        try {
            mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        } catch (NoSuchFieldException e) {
            Log.e("", e.toString());
            return;
        }
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        Class<?> cls = mInstrumentation.getClass();
        Method method = cls.getMethod("execStart", String.class);
        method.invoke(mInstrumentation, "zzzzj");
        // 创建代理对象
        Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);

        // 偷梁换柱
        mInstrumentationField.set(currentActivityThread, evilInstrumentation);

        mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        Class<?> cls2 = mInstrumentation.getClass();
        Method method2 = cls2.getMethod("execStart", String.class);
        method2.invoke(mInstrumentation, "zzzzj");

        Log.e("", "end context");
    }



    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return mName;
    }

    @Override
    public void setName(String name) {
        // TODO Auto-generated method stub
        mName = name;
    }

    @OnClick(R.id.add_button)
    public void add_bt_fun(View v) {
        print("add button");
        //Toast.makeText(getActivity(), "add button", Toast.LENGTH_LONG).show();
        mArcView.add_text();
    }

    @OnClick(R.id.dec_button)
    public void dec_bt_fun(View v) {
        print("dec button");
        //Toast.makeText(getActivity(), "dec button", Toast.LENGTH_LONG).show();
        mArcView.dec_text();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        int color = getResources().getColor(mColorRes);
        ScreenUtils.initScreen(getActivity());
        // construct the RelativeLayout
        View view = inflater.inflate(R.layout.fragment_arc, container, false);
        view.setBackgroundColor(color);
        ViewUtils.inject(this, view);
        mArcView = (ArcView) view.findViewById(R.id.arc_view);

        return view;
    }

    public void test() {
        try {
            attactContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void test1() {
        File dataDir = Environment.getDataDirectory();
        File mAppDataDir = new File(dataDir, "data");
        File mSecureAppDataDir = new File(dataDir, "secure/data");
        File mDrmAppPrivateInstallDir = new File(dataDir, "app-private");


        File mFrameworkDir = new File(Environment.getRootDirectory(), "framework");
        File mDalvikCacheDir = new File(dataDir, "dalvik-cache");

        Log.w("testfst", mFrameworkDir.getAbsolutePath());

/*10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/testfst: /system/framework
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/test: /system/app
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/test: /vendor/app
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/test: /data/app-private
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/test: /data/data
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/test: /data/secure/data
10-08 15:25:17.364 5442-5442/zj.zfenlly.tools W/tested: /data/dalvik-cache*/


        // Collect all system packages.
        File mSystemAppDir = new File(Environment.getRootDirectory(), "app");
        Log.w("test", mSystemAppDir.getAbsolutePath());


        // Collect all vendor packages.
        File mVendorAppDir = new File("/vendor/app");
        Log.w("test", mVendorAppDir.getAbsolutePath());

        Log.w("test", mDrmAppPrivateInstallDir.getAbsolutePath());

        Log.w("test", mAppDataDir.getAbsolutePath());
        Log.w("test", mSecureAppDataDir.getAbsolutePath());
        Log.w("tested", mDalvikCacheDir.getAbsolutePath());
    }



    @Override
    public void onResume() {
        super.onResume();
        //test2();
        print("onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        print("onPause");
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
}
