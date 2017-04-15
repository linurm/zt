package com.android.wifimulticast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by Administrator on 2017/4/12.
 */

public class NetUtil {

    private static final String TAG = "Net.Utils";
    private static final int MULTICAST_PORT = 5111;
    private static final String GROUP_IP = "224.5.0.7";
    private static byte[] sendData;

    static {
        sendData = new byte[4];
// 0xEE78F1FB
        sendData[3] = (byte) 0xEE;
        sendData[2] = (byte) 0x78;
        sendData[1] = (byte) 0xF1;
        sendData[0] = (byte) 0xFB;
    }

    public static void sendIpToNet() {

    }
    private static String int2string(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public static String getWifiIP(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddr = wifiInfo.getIpAddress();
        String ipStr = int2string(ipAddr);
        return ipStr;
    }



    public static boolean isWiFiConnected(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }
    public static String findServerIpAddress() throws IOException {
        String ip = null;
        MulticastSocket multicastSocket = new MulticastSocket(MULTICAST_PORT);
        multicastSocket.setLoopbackMode(true);
        InetAddress group = InetAddress.getByName(GROUP_IP);
        multicastSocket.joinGroup(group);
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, group, MULTICAST_PORT);
        for (; ; ) {
            multicastSocket.send(packet);
            Log.d(TAG, ">>>send packet ok");
            byte[] receiveData = new byte[256];
            packet = new DatagramPacket(receiveData, receiveData.length);
            multicastSocket.receive(packet);
            String packetIpAddress = packet.getAddress().toString();
            packetIpAddress = packetIpAddress.substring(1, packetIpAddress.length());
            Log.d(TAG, "packet ip address: " + packetIpAddress);
            StringBuilder packetContent = new StringBuilder();
            for (int i = 0; i < receiveData.length; i++) {
                if (receiveData[i] == 0) {
                    break;
                }
                packetContent.append((char) receiveData[i]);
            }
            ip = packetContent.toString();
            Log.d(TAG, "packet content ip is: " + ip);
            if (ip.equals(packetIpAddress)) {
                Log.d(TAG, "find server ip address: " + ip);
                break;
            } else {
                Log.d(TAG, "not find server ip address, continue …");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }
        return ip;
    }

}
