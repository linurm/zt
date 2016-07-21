package zj.zfenlly.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.others.zxing.integration.android.IntentIntegrator;
import com.others.zxing.integration.android.IntentResult;

import zj.zfenlly.tools.R;

public class BaseActivity extends SlidingFragmentActivity {


    protected ListFragment mFrag;
    private int mTitleRes;


    public BaseActivity(int titleRes) {
        mTitleRes = titleRes;
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(mTitleRes);
        // customize the SlidingMenu
        SlidingMenu sm = getSlidingMenu();
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void start_ScanQRCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
        integrator.initiateScan();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
            case R.id.action_scan:
                start_ScanQRCode();
                //Log.e("DDD", "DDDDDDDDDDD");
                break;
            case R.id.action_settings:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getSupportMenuInflater().inflate(R.menu.menu_settings, menu);

        //menu.add(R.menu.menu_settings, 0, 0, "scan");

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                showDialog(R.string.result_succeeded,
                        result.toString());
                //AppJavaClass a = new AppJavaClass();
                //a.ScanCodeSuccess(result.toString());
            } else {
                showDialog(R.string.result_failed,
                        getString(R.string.result_failed_why));
                // AppJavaClass a = new AppJavaClass();
                // a.ScanCodeError("error");
            }
        } else {
            ;//other startActivity()
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void showDialog(int title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok_button, null);
        builder.show();
    }
}
