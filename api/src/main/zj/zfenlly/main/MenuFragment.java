package zj.zfenlly.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import zj.zfenlly.arc.ArcFragment;
import zj.zfenlly.bluetooth.BluetoothFragment;
import zj.zfenlly.camera.CameraFragment;
import zj.zfenlly.coloradjust.ColorAdjustFragment;
import zj.zfenlly.http.HttpFragment;
import zj.zfenlly.mobeta.DragsortFragment;
import zj.zfenlly.record.RecordFragment;
import zj.zfenlly.stock.StockFragment;
import zj.zfenlly.tools.R;
import zj.zfenlly.usb.UsbFragment;
import zj.zfenlly.wifi.WifiFragment;
import zj.zfenlly.wifiap.WifiApFragment;

public class MenuFragment extends ListFragment {
    private final String TAG = this.getClass().getName();
    //.substring(this.getClass().getName().lastIndexOf(".") + 1);

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

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        Fragment newContent = null;
        // print("position: " + position);
        switch (position) {
            case 0:
                newContent = new ColorFragment(R.color.white, "color");
                break;
            case 1:
                newContent = new CameraFragment(R.color.green, "camera");
                break;
            case 2:
                newContent = new BluetoothFragment(R.color.white, "Bluetooth");
                break;
            case 3:
                //newContent = new SpeechSynthesisFragment(android.R.color.white, "voice");
                break;
            case 4:
                newContent = new UsbFragment(android.R.color.black, "usb");
                break;
            case 5:
                newContent = new WifiApFragment(R.color.red, "wifi ap");
                break;
            case 6:
                newContent = new HttpFragment(R.color.red, "http");
                break;
            case 7:
                newContent = new ColorAdjustFragment(R.color.red, "color");
                break;
            case 8:
                newContent = new WifiFragment(R.color.white, "wifi");
                break;
            case 9:
                newContent = new RecordFragment(R.color.white, "record");
                break;
            case 10:
                newContent = new StockFragment();
                break;
            case 11:
                newContent = new DragsortFragment();
                break;
            case 12:
                newContent = new ArcFragment();
                break;

        }
        if (newContent != null)
            switchFragment(newContent);
    }

    @SuppressWarnings("unused")
    private void print(String msg) {
        Log.i(TAG, msg);
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
