package zj.zfenlly.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.net.wifi.WifiManager.WifiLock;

import java.util.List;

public class WifiAdmin {
    // 定义一个WifiManager对象
    private WifiManager mWifiManager;
    // 定义一个WifiInfo对象
    private WifiInfo mWifiInfo;

    private MulticastLock multicastLock = null;

    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    WifiLock mWifiLock;

    enum WifiPasswdType {
        WIFICIPHER_NOPASS, WIFICIPHER_WEP, WIFICIPHER_WPA,

    }

    public WifiAdmin(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象

    }

    // 打开wifi
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 关闭wifi
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    public void allowMulticast() {
        multicastLock = mWifiManager.createMulticastLock("multicast");
        multicastLock.acquire();
    }

    public void releaseMulticast() {
        if (multicastLock != null) {
            multicastLock.release();
        }
    }

    public void changeWifiState() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        } else {
            mWifiManager.setWifiEnabled(true);
        }
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁wifiLock
    public void releaseWifiLock() {
        // 判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        return mWifiConfigurations;
    }

    // 指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        // mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        // mWifiList = mWifiManager.getScanResults();

        // 得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        mWifiList = mWifiManager.getScanResults();
        return mWifiList;
    }

    public int getWifiLevel() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        int signalLevel = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(),
                4);
        return signalLevel;
    }

    // 查看扫描结果
    @SuppressLint("UseValueOf")
    public StringBuffer lookUpScan() {
        mWifiList = mWifiManager.getScanResults();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getBSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();

    }

    public String getSSID() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    public int getIpAddress() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetWordId() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到wifiInfo的所有信息
    public String getWifiInfo() {
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    // 添加一个网络并连接
    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    // 断开指定ID的网络
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);

    }

    public boolean disconnect() {
        return mWifiManager.disconnect();
    }

    public boolean reconnect() {
        return mWifiManager.reconnect();
    }

    public boolean reassociate() {
        return mWifiManager.reassociate();
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    // 保存网络配置信息
    public void saveConnectionWifi() {
        mWifiManager.saveConfiguration();
        // mWifiManager.removeNetwork(netId);
    }

    public void removeConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        // mWifiManager.
        mWifiManager.removeNetwork(netId);

    }

    public static final int OPEN_INDEX = 0;
    public static final int WPA_INDEX = 1;
    public static final int WPA2_INDEX = 2;

    // private int mSecurityTypeIndex = OPEN_INDEX;

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();

        config.SSID = "\"" + SSID + "\"";

        switch (Type) {
            case WifiAccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;

            case WifiAccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (Password.length() != 0) {
                    int length = Password.length();
                    String password = Password;
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)
                            && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;

            case WifiAccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (Password.length() != 0) {
                    String password = Password;
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

            case WifiAccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                // config.eap.setValue((String)
                // mEapMethodSpinner.getSelectedItem());
                //
                // config.phase2
                // .setValue((mPhase2Spinner.getSelectedItemPosition() == 0) ? ""
                // : PHASE2_PREFIX + mPhase2Spinner.getSelectedItem());
                // config.ca_cert.setValue((mEapCaCertSpinner
                // .getSelectedItemPosition() == 0) ? "" : KEYSTORE_SPACE
                // + Credentials.CA_CERTIFICATE
                // + (String) mEapCaCertSpinner.getSelectedItem());
                // config.client_cert.setValue((mEapUserCertSpinner
                // .getSelectedItemPosition() == 0) ? "" : KEYSTORE_SPACE
                // + Credentials.USER_CERTIFICATE
                // + (String) mEapUserCertSpinner.getSelectedItem());
                // final boolean isEmptyKeyId = (mEapUserCertSpinner
                // .getSelectedItemPosition() == 0);
                // config.key_id.setValue(isEmptyKeyId ? ""
                // : Credentials.USER_PRIVATE_KEY
                // + (String) mEapUserCertSpinner.getSelectedItem());
                // config.engine
                // .setValue(isEmptyKeyId ? WifiConfiguration.ENGINE_DISABLE
                // : WifiConfiguration.ENGINE_ENABLE);
                // config.engine_id.setValue(isEmptyKeyId ? ""
                // : WifiConfiguration.KEYSTORE_ENGINE_ID);
                // config.identity.setValue((mEapIdentityView.length() == 0) ? ""
                // : mEapIdentityView.getText().toString());
                // config.anonymous_identity
                // .setValue((mEapAnonymousView.length() == 0) ? ""
                // : mEapAnonymousView.getText().toString());
                // if (mPasswordView.length() != 0) {
                // config.password.setValue(mPasswordView.getText().toString());
                // }
                break;

            default:
                return null;
        }

        // config.proxySettings = mProxySettings;
        // config.ipAssignment = mIpAssignment;
        // config.linkProperties = new LinkProperties(mLinkProperties);

        return config;
    }
}
