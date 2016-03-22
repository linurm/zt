package zj.zfenlly.wifi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import zj.zfenlly.tools.R;


public class WifiConfigDialogFragment extends DialogFragment {


    @ViewInject(R.id.wifi_ssid)
    private TextView wifi_ssid;
    @ViewInject(R.id.wifi_psk)
    private EditText wifi_psk;
    @ViewInject(R.id.checkbox)
    private CheckBox display_psk;
    @ViewInject(R.id.OK)
    private Button okBT;
    @ViewInject(R.id.Forget)
    private Button forgetBT;
    @ViewInject(R.id.Cancle)
    private Button ccBT;


    private String mWifiSsid = "";
    private WifiAdmin mWifiAdmin;
    WifiConfiguration mWifiConfiguration;
    List<WifiConfiguration> listWCF;
    private final String TAG = this.getClass().getName();

    //.substring(this.getClass().getName().lastIndexOf(".") + 1);
    private void print(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        print("onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi_config, container, false);

        ViewUtils.inject(this, view);

        initMainUI();
        initMainUIListener();

        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    private void initMainUI() {
        //setContentView(R.layout.wifi_config);
//        wifi_ssid = (TextView) findViewById(R.id.wifi_ssid);
//        wifi_psk = (EditText) findViewById(R.id.wifi_psk);
//        display_psk = (CheckBox) findViewById(R.id.checkbox);
//        okBT = (Button) findViewById(R.id.OK);
//        forgetBT = (Button) findViewById(R.id.Forget);
//        ccBT = (Button) findViewById(R.id.Cancle);
        mWifiSsid = getArguments().getString("ssid", "");
        mWifiAdmin = new WifiAdmin(getActivity());
        if (mWifiSsid != null) {
            wifi_ssid.setText(mWifiSsid);
            getNetWorkConfig(mWifiSsid);
        }


    }

    public void getNetWorkConfig(String ssid) {
        // Log.i(TAG, ssid);
        boolean found = false;

        listWCF = mWifiAdmin.getConfiguration();
        // listItem.clear();
        if (listWCF != null) {
            // listItem.clear();
            for (int i = 0; i < listWCF.size(); i++) {
                mWifiConfiguration = listWCF.get(i);
                if (mWifiConfiguration.SSID
                        .equalsIgnoreCase("\"" + ssid + "\"")) {
                    // Log.i(TAG, "1111111111111");
                    wifi_psk.setText("********");
                    found = true;
                    break;
                    // mWifiAdmin.connetionConfiguration(i);
                }
                // Log.i(TAG, "wifi config: " + mWifiConfiguration.SSID + ""
                // + mWifiConfiguration.preSharedKey);
                // UpdateNet(mWifiConfiguration.SSID, ""
                // + mWifiConfiguration.preSharedKey);
            }
            if (found == false) {
                if (ssid.startsWith("HH_")) {
                    Log.i(TAG, "*************************************");
                    String s = GenPasswd(ssid);
                    wifi_psk.setText(s);
                }
            }
            // Log.i(TAG, " listWCF.size() = " + listWCF.size());

        } else {
            Log.i(TAG, "wifi configuration null");
        }
    }

    private String GenPasswd(String ssid) {
        String passwd = "";
        // String prepw = "";
        // String pw = "";
        // char[] d = new char[11];
        for (int i = 0; i < 5; i++) {
            // d[i] = (char) ssid.getBytes()[12 - i];
            passwd += ssid.substring(12 - i, 13 - i);
            // Log.i(TAG,
            // "prepw== " + prepw + "   "
            // + ssid.substring(12 - i, 12 - i + 1));
        }
        for (int i = 0; i < 5; i++) {
            // d[i + 5] = (char) ssid.getBytes()[3 + i];
            passwd += ssid.substring(3 + i, 4 + i);
            // Log.i(TAG, "pw== " + pw + "   " + ssid.substring(3 + i, 3 + i +
            // 1));
        }
        // d[10] = 0;
        // passwd = prepw + pw;

        return passwd;
    }

    private void initMainUIListener() {
        if (display_psk != null) {
            display_psk
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // TODO Auto-generated method stub
                            if (isChecked) {
                                wifi_psk.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                                // Editable etable = wifi_psk.getText();
                                System.out.println("nomal psk:"
                                        + wifi_psk.getText().toString());
                            } else {
                                wifi_psk.setInputType(InputType.TYPE_CLASS_TEXT
                                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                System.out.println("password psk:"
                                        + wifi_psk.getText().toString());
                            }
                        }
                    });
        }
        if (okBT != null) {
            okBT.setOnClickListener(new MyOnClickListener());
        }
        if (forgetBT != null) {
            forgetBT.setOnClickListener(new MyOnClickListener());
        }

        if (ccBT != null) {
            ccBT.setOnClickListener(new MyOnClickListener());
        }

    }

    private void toConnectWifi() {
        String wifi_ssid_string = wifi_ssid.getText().toString();
        String wifi_psk_string = wifi_psk.getText().toString();
        // mWifiAdmin.ConnectWifi(wifi_ssid_string, wifi_psk_string);

        // 判断空，我就不判断了。。。。
        Intent data = new Intent();
        data.putExtra("ssid", wifi_ssid_string);
        data.putExtra("psk", wifi_psk_string);
        // 请求代码可以自己设置，这里设置成20
        getActivity().setResult(20, data);
    }

    private class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.OK:
                    String ssidStr = wifi_ssid.getText().toString();
                    String pskStr = wifi_psk.getText().toString();
                    print("ssidStr:" + ssidStr);
                    print("pskStr:" + pskStr);
                    if (ssidStr == null || ssidStr.equals("") || pskStr == null) {
                        new AlertDialog.Builder(getActivity())
                                .setMessage(R.string.wifi_no_ssid_psk)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                // TODO Auto-generated method stub
                                                dialog.dismiss();
                                            }
                                        }).show();
                        return;
                    }
                    toConnectWifi();
                    Toast.makeText(getActivity(), R.string.have_done,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    break;
                case R.id.Forget:
                    // WifiConfigDialogFragment.this.finish();
                    Intent data = new Intent();
                    String wifi_ssid_string = wifi_ssid.getText().toString();
                    data.putExtra("ssid", wifi_ssid_string);
                    // 请求代码可以自己设置，这里设置成20
                    getActivity().setResult(30, data);
                    getActivity().finish();
                    break;
                case R.id.Cancle:

                    // 请求代码可以自己设置，这里设置成20
                    getActivity().setResult(40, null);
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        }
    }

}