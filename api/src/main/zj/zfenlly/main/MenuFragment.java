package zj.zfenlly.main;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import zj.zfenlly.arc.ArcFragment;
import zj.zfenlly.bluetooth.BluetoothFragment;
import zj.zfenlly.caculator.CalculationFragment;
import zj.zfenlly.camera.CameraFragment;
import zj.zfenlly.coloradjust.ColorAdjustFragment;
import zj.zfenlly.http.HttpFragment;
import zj.zfenlly.mobeta.DragsortFragment;
import zj.zfenlly.record.RecordFragment;
import zj.zfenlly.stock.StockFragment;
import zj.zfenlly.stock2.Stock2Fragment;
import zj.zfenlly.tools.R;
import zj.zfenlly.usb.UsbFragment;
import zj.zfenlly.wifi.WifiFragment;
import zj.zfenlly.wifiap.WifiApFragment;
import zj.zfenlly.wifidevice.WifiDeviceFragment;

public class MenuFragment extends ListFragment {
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    boolean landOritation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String[] colors = getResources().getStringArray(R.array.color_names);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, colors);
        setListAdapter(colorAdapter);
    }
    //List<Fragment> fragments = new ArrayList<>();
    //fragments.add(new RecordFragment());
    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        Fragment newContent = null;

        // print("position: " + position);
        switch (position) {
            case 20:
                //newContent = ((Fragment) (listF.get(0))).newInstance();
                landOritation = false;
            case 0:
                newContent = new ColorFragment(R.color.white, "color");
                landOritation = false;
                break;
            case 1:
                newContent = new CameraFragment(R.color.green, "camera");
                landOritation = false;
                break;
            case 2:
                newContent = new BluetoothFragment(R.color.white, "Bluetooth");
                landOritation = false;
                break;
            case 3:
                newContent = new CalculationFragment(android.R.color.white, "cal");
                landOritation = false;
                break;
            case 4:
                newContent = new UsbFragment(android.R.color.black, "usb");
                landOritation = false;
                break;
            case 5:
                newContent = new WifiApFragment(R.color.red, "wifi ap");
                landOritation = false;
                break;
            case 6:
                newContent = new HttpFragment(R.color.red, "http");
                landOritation = false;
                break;
            case 7:
                newContent = new ColorAdjustFragment(R.color.red, "color");
                landOritation = false;
                break;
            case 8:
                newContent = new WifiFragment(R.color.white, "wifi");
                landOritation = false;
                break;
            case 9:
                newContent = new RecordFragment(R.color.yezhi, "record");
                landOritation = false;
                break;
            case 10:
                newContent = new StockFragment();
                landOritation = false;
                break;
            case 11:
                newContent = new DragsortFragment();
                landOritation = false;
                break;
            case 12:
                newContent = new ArcFragment();
                landOritation = true;
                break;
            case 13:
                newContent = new WifiDeviceFragment();
                landOritation = false;
                break;
            case 14:
                newContent = new Stock2Fragment();
                landOritation = true;
                break;
        }
        if (landOritation) {
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                print("66666666666666666666666666666666 -");
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setActivityFragment(newContent);
                return;
            }
        }else{
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                print("44444444444444444444444444 |");
                setActivityFragment(newContent);
                return;
            }
        }
        if (newContent != null) {
            switchFragment(newContent);
        }
    }

    @SuppressWarnings("unused")
    private void print(String msg) {
        Log.i(TAG, msg);
    }

    private void setActivityFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.setFragment(fragment);
        }
    }

    // the meat of switching the above fragment
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.switchContent(fragment);
        }
        // else if (getActivity() instanceof StockActivity) {
        // StockActivity fca = (StockActivity) getActivity();
        // fca.switchContent(fragment);
        // }
    }

}
