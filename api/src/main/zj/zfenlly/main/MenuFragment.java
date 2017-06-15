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

import java.util.List;

import zj.zfenlly.tools.R;

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

        List<BaseFragment> list = ((MainApplication) getActivity().getApplication()).getFragments();
        String[] colors = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            colors[i] = list.get(i).getName();
        }

        //String[] colors = getResources().getStringArray(R.array.color_names);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1,
                android.R.id.text1, colors);
        setListAdapter(colorAdapter);
    }


    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        BaseFragment newContent = null;


        newContent = ((MainApplication) getActivity().getApplication()).getFragments().get(position);
        landOritation = newContent.getOrientation();
        print("position: " + position + " " + newContent.getName());
        position = 0;
       /* switch (position) {
            case 0:
                //newContent = ((fragments.get(0)));
                //landOritation = false;
                break;
            case 20:
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
        }*/
//        print("              @@@@@@@@" + getActivity().getRequestedOrientation());
        if (landOritation) {
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                print("--------------");
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                setActivityFragment(newContent);
                return;
            }
        } else {
            if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                print("||||||||||||||||||");
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
        Log.e(TAG, msg);
    }

    private void setActivityFragment(BaseFragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainActivity) {
            MainActivity fca = (MainActivity) getActivity();
            fca.setFragment(fragment);
        }
    }

    // the meat of switching the above fragment
    private void switchFragment(BaseFragment fragment) {
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
