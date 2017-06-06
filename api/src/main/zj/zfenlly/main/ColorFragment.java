package zj.zfenlly.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import zj.zfenlly.tools.R;

@SuppressLint("ValidFragment")
public class ColorFragment extends BaseFragment {

    private final String TAG = this.getClass().getName();

    private int mColorRes = -1;
    private ColorPresenter pColorPresenter;
    private ApplicationManager am;
    @ViewInject(R.id.button)
    private Button mbutton;
    @ViewInject(R.id.button2)
    private Button mbutton2;
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    public ColorFragment() {
        this(R.color.white, "color");
    }

    public ColorFragment(int colorRes, String name) {
        super(name, false);
        mColorRes = colorRes;
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @OnClick(R.id.button2)
    public void button2(View view) {
        try {
            am.installPackage("mnt/internal_sd/Download/BaiduMapsApiDemo.apk");
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    @OnClick(R.id.button)
    public void button(View view) {
        pColorPresenter.doStartApplicationWithPackageName("baidumapsdk.demo");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null)
            mColorRes = savedInstanceState.getInt("mColorRes");
        print("onCreateView");
        int color = getResources().getColor(mColorRes);
        pColorPresenter = new ColorPresenter(this);
//        try {
//            am = new ApplicationManager(getActivity());
//            am.setOnInstalledPackaged(new OnInstalledPackaged() {
//                public void packageInstalled(String packageName, int returnCode) {
//                    if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
//                        Log.d(TAG, "Install succeeded");
//                    } else {
//                        Log.d(TAG, "Install failed: " + returnCode);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "", e);
//        }
        // print("qqqqqqqqqqqqqqqq");
        // construct the RelativeLayout
        View view = inflater.inflate(R.layout.fragment_color, container, false);
        view.setBackgroundColor(color);

        ViewUtils.inject(this, view);


//		StateMachineTest hsm = StateMachineTest.makeHsm1();
//		synchronized (hsm){
//		     hsm.sendMessage(hsm.obtainMessage(hsm.CMD_1));
//		     hsm.sendMessage(hsm.obtainMessage(hsm.CMD_2));
//		     try {
//		          // wait for the messages to be handled
//		          hsm.wait();
//		     } catch (InterruptedException e) {
//		          ;//loge("exception while waiting " + e.getMessage());
//		     }
//		}
//		HelloWorld hw = HelloWorld.makeHelloWorld();
//	    hw.sendMessage(hw.obtainMessage());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
