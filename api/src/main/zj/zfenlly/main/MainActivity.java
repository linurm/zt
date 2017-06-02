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
import zj.zfenlly.stock2.Stock2Fragment;
import zj.zfenlly.tools.R;
import zj.zfenlly.wifidevice.util.Constant;

public class MainActivity extends BaseActivity {


    private final String TAG = this.getClass().getName();
    private Fragment mContent = null;

    public MainActivity() {
        super(R.string.changing_fragments);
        // TODO Auto-generated constructor stub
    }

    public void showDialog(View mView) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(
                R.string.alert_dialog_two_buttons_title, mView);
        newFragment.show(getFragmentManager(), "dialog");
    }
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_color);
        Log.e("0000000", "show" + this);
        // set the Above View
        if (savedInstanceState != null) {
            // mContent = getSupportFragmentManager().getFragment(
            // savedInstanceState, FRAGMENT_CONTENT_TAG);
            print("have");
        }
        if (mContent == null)
            //mContent = new RecordFragment();
            mContent = new ColorFragment();
        // set the Above View
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, mContent).commit();

        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new MenuFragment()).commit();

        // customize the SlidingMenu
        getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        print("onCreate");
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
        print("onSaveInstanceState:" + mContent);
        outState.putInt("abc", 1);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            print("savedInstanceState");
        print("onRestoreInstanceState");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        print("onDestroy");
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        getSlidingMenu().showContent();
        print("switchContent:" + mContent);
    }
}
