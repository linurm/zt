package zj.zfenlly.main;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import zj.zfenlly.record.MyAlertDialogFragment;
import zj.zfenlly.record.RecordFragment;
import zj.zfenlly.stock2.Stock2Fragment;
import zj.zfenlly.tools.R;
import zj.zfenlly.wifidevice.util.Constant;

public class MainActivity extends BaseActivity {


    private static final String KEY_CURRENT_PROGRESS = "current_fragment";
    private final String TAG = this.getClass().getName();
    private Fragment mContent = null;

    public MainActivity() {
        super(R.string.changing_fragments);
        // TODO Auto-generated constructor stub
    }
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    public void showDialog(View mView) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(
                R.string.alert_dialog_two_buttons_title, mView);
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void print(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        print("onCreate");
        setContentView(R.layout.fragment_color);
        //Log.e("0000000", "show" + this);
        // set the Above View
        if (savedInstanceState != null) {
            // mContent = getSupportFragmentManager().getFragment(
            // savedInstanceState, FRAGMENT_CONTENT_TAG);
            if (1 == savedInstanceState.getInt(KEY_CURRENT_PROGRESS)) {
                mContent = new Stock2Fragment();
                print("33333333333333333333333 new Stock2Fragment");
            } else {
                mContent = new RecordFragment();
            }
        }

        if (mContent == null)
            //mContent = new RecordFragment();
            mContent = new ColorFragment();


        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new MenuFragment()).commit();

        // set the Above View
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        // customize the SlidingMenu
         getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
       // getSlidingMenu().showContent();

        switchContent(mContent);
    }

    @Override
    public void onResume() {
        super.onResume();
        print("onResume");
    }

    @Override
    public void onStart() {
        super.onStart();
        print("onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        print("onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        print("onPause");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (null != data) {
                int selectType = data.getExtras().getInt("selectType");
                if (selectType == Constant.SELECT_FILES) {
                    Log.e("TAG", "select type");
//                    WifiDeviceFragment.
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // getSupportFragmentManager().putFragment(outState,
        // FRAGMENT_CONTENT_TAG,
        // mContent);
        print("onSaveInstanceState: " + mContent);
        if (mContent instanceof Stock2Fragment) {
            outState.putInt(KEY_CURRENT_PROGRESS, 1);
        }
        mContent = null;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            int t = savedInstanceState.getInt(KEY_CURRENT_PROGRESS);
            print("savedInstanceState " + t);
        }
        print("onRestoreInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
    }

    public void switchContent(Fragment fragment) {
        if (fragment == null)
            return;
        mContent = fragment;

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.content_frame, fragment);
        //transaction.addToBackStack(null);
        transaction.commit();
        getSlidingMenu().showContent();
        print("switchContent: " + mContent);
    }

    public void setFragment(Fragment fragment) {
        mContent = fragment;
    }
}
